package com.capestone.capsproject.Repo;

import com.capestone.capsproject.model.UserFeed;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserFeedRepo extends MongoRepository<UserFeed,String> {

    List<UserFeed> findByEmail(String email);

    List<UserFeed> findAllByEmailAndAvailableAndVisibility(String email, boolean available, String visibility);

    List<UserFeed> findAllByVisibility(String visibility);
}
