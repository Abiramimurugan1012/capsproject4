package com.capestone.capsproject.Repo;

import com.capestone.capsproject.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends MongoRepository<User,String> {


   public default Optional<User> findOneById(String s){
    return findById(s);
}



    Optional<User> findByEmail(String email);

    Optional<User> findById(String email);
}
