package com.neha.SpringBootWithGraphQl.repository;

import com.neha.SpringBootWithGraphQl.entity.PersonEntity;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<PersonEntity,Integer> {
    PersonEntity findByEmail(String email);
}
