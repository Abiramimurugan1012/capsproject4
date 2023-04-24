package com.capestone.capsproject.Controller;


import com.capestone.capsproject.Repo.UserRepository;
import com.capestone.capsproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserRepository userRepository;
    @RequestMapping(value = "/categories")
    public ResponseEntity<?> categories() {
        Optional<User> admin=userRepository.findById("abirami@divum.in");
        return ResponseEntity.ok(admin.get().getCategories());

    }
    @PutMapping("/additems/{item}")
    public ResponseEntity<?> addcategories(@PathVariable String item)
    {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
       // System.out.println(email);
        if(userRepository.findById(email).get().getRole().equals("Admin")) {
            Optional<User> admin=userRepository.findById(email);
            if(!admin.get().getCategories().contains(item)) {
                admin.get().getCategories().add(item);
                userRepository.save(admin.get());
                return ResponseEntity.ok("Categories added");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categories already exists");

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");

    }
    @DeleteMapping("/deleteitem/{item}")
    public ResponseEntity<?> deleteitem(@PathVariable String item) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userRepository.findById(email).get().getRole().equals("Admin")) {
            Optional<User> admin=userRepository.findById(email);
            if(admin.get().getCategories().contains(item)) {
                admin.get().getCategories().contains(item);
                userRepository.save(admin.get());
                return ResponseEntity.ok("Categories deleted");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("category is not exist");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Use not found");
    }

    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteall() {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userRepository.findById(email).get().getRole().equals("Admin")) {
            Optional<User> admin=userRepository.findById(email);
            System.out.println(admin);
            admin.get().setCategories(new ArrayList<>());
            userRepository.save(admin.get());
            return ResponseEntity.ok("Category has been reset");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }
}
