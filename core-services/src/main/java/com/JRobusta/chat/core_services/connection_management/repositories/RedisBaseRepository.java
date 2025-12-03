package com.JRobusta.chat.core_services.connection_management.repositories;



public interface RedisBaseRepository<T, ID> {

  void save(T entity);

  void deleteById(ID id);

  T findById(ID id);

  boolean existsById(ID id);
}
