package com.capestone.capsproject.Service;

import com.capestone.capsproject.Repo.UserRepository;
import com.capestone.capsproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
@CacheConfig(cacheNames = {"UserLogin"})
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    public UserService() {
    }

    @Cacheable(key = "#email")


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> foundUser= userRepository.findById(email);
        if(foundUser.isEmpty()) {
            return null;
        }
        String emailId=foundUser.get().getEmail();
        String password=foundUser.get().getPassword();
        return new org.springframework.security.core.userdetails.User(emailId,password,new ArrayList<>());
    }


}

