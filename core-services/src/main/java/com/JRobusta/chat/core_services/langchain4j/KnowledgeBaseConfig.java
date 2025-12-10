package com.JRobusta.chat.core_services.langchain4j;

import com.JRobusta.chat.core_services.langchain4j.db.Game;
import com.JRobusta.chat.core_services.langchain4j.db.GameRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static dev.langchain4j.data.document.Metadata.metadata;

@Configuration
public class KnowledgeBaseConfig {


    // 1) Embedding model
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    // 2) Store (in-memory)
    @Bean
    public InMemoryEmbeddingStore<TextSegment> embeddingStore () {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return userId -> MessageWindowChatMemory.withMaxMessages(20);
    }

    // 3) Load dữ liệu game từ DB vào vector store
    @Bean
    public CommandLineRunner ingestData(
            EmbeddingStore<TextSegment> store,
            EmbeddingModel model,
            GameRepository gameRepo
    ) {
        return args -> {
            List<Game> games = gameRepo.findAll();

            for (Game game : games) {
                // Skip if already vectorized
                if (Boolean.TRUE.equals(game.getIsVectorized())) {
                    continue;
                }

                String info = """
                    Tên game: %s.
                    Thể loại: %s.
                    Giá: %s %s.
                    Nhà phát triển: %s.
                    Nhà phát hành: %s.
                    Ngày phát hành: %s.
                    Đánh giá: %s.
                    Mô tả: %s.
                    """.formatted(
                        game.getTitle(),
                        game.getGenreTags() != null ? game.getGenreTags() : "N/A",
                        game.getPrice() != null ? game.getPrice() : "N/A",
                        game.getCurrency() != null ? game.getCurrency() : "VND",
                        game.getDeveloper() != null ? game.getDeveloper() : "N/A",
                        game.getPublisher() != null ? game.getPublisher() : "N/A",
                        game.getReleaseDate() != null ? game.getReleaseDate() : "N/A",
                        game.getReviewSummary() != null ? game.getReviewSummary() : "N/A",
                        game.getDescription() != null ? game.getDescription() : "N/A"
                );

                TextSegment segment = TextSegment.from(
                        info,
                        metadata("gameId", game.getId().toString())
                            .put("title", game.getTitle())
                            .put("price", game.getPrice() != null ? game.getPrice().toString() : "0")
                );

                Embedding embedding = model.embed(segment).content();
                store.add(embedding, segment);

                // Mark as vectorized
                game.setIsVectorized(true);
                gameRepo.save(game);
            }

            System.out.println("[AI] Đã nạp " + games.size() + " game vào vector store.");
        };
    }

    // 4) Content retriever (cho RAG)
    @Bean
    public ContentRetriever contentRetriever(
            EmbeddingStore<TextSegment> store,
            EmbeddingModel model
    ) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(model)
                .maxResults(5)
                .minScore(0.55)
                .build();
    }
}
