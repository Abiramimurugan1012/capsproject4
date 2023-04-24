package com.capestone.capsproject.Controller;

import com.capestone.capsproject.Repo.UserFeedRepo;
import com.capestone.capsproject.Repo.UserRepository;
import com.capestone.capsproject.model.User;
import com.capestone.capsproject.model.UserFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feed")
public class UserFeedController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserFeedRepo userFeedRepo;

    @GetMapping("/myfeed")
    public ResponseEntity<?> myfeed() {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        if(!userFeedRepo.findByEmail(email).isEmpty()){
            return ResponseEntity.ok(userFeedRepo.findByEmail(email));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Feed is empty");
    }
    @PostMapping("/newpost")
    public ResponseEntity<?> newpost(@RequestBody UserFeed userFeed) {
        if(userRepository.existsById(userFeed.getEmail()) && userFeed.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            userFeedRepo.save(userFeed);
            return ResponseEntity.ok("Feed uploaded");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not valid");
    }
    @GetMapping("/myfeed/{id}")
    public ResponseEntity<?> myfeedById(@PathVariable String id) {
        System.out.println(id);
        if(userFeedRepo.existsById(id)) {
            return ResponseEntity.ok(userFeedRepo.findById(id).get());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found");
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<?> deletefeed(@PathVariable String id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userFeedRepo.existsById(id) && userFeedRepo.findById(id).get().getEmail().equals(email)) {
            Optional<UserFeed> userFeed=userFeedRepo.findById(id);
            userFeed.get().setAvailable(false);
            userFeedRepo.save(userFeed.get());
            return ResponseEntity.ok("Post is deleted.You can see it in archeive");

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found");
    }

    @GetMapping("/archeive")
    public ResponseEntity<?> archeive() {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserFeed> myfeed=new ArrayList<>();
        List<UserFeed> allfeed=userFeedRepo.findAll();
        for(UserFeed i:allfeed) {
            if(i.getEmail().equals(email) && !i.isAvailable()) {
                myfeed.add(i);
            }
        }
        if(myfeed.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your archieve is empty");
        }
        else {
            return ResponseEntity.ok(myfeed);
        }

    }




    @DeleteMapping("/archeive/{id}")
    public ResponseEntity<?> archeivefeed(@PathVariable String id) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userFeedRepo.existsById(id) && userFeedRepo.findById(id).get().getEmail().equals(email)) {
            Optional<UserFeed> userFeed=userFeedRepo.findById(id);
            if(!userFeed.get().isAvailable()) {
                userFeedRepo.deleteById(id);
                userFeedRepo.save(userFeed.get());
                return ResponseEntity.ok("post deleted from archeive");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Content not in false");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found");
    }


    @GetMapping("/restore/{id}")
    public ResponseEntity<?> restorefeed(@PathVariable String id) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userFeedRepo.existsById(id) && userFeedRepo.findById(id).get().getEmail().equals(email)) {
            Optional<UserFeed> userFeed=userFeedRepo.findById(id);
            userFeed.get().setAvailable(true);
            userFeedRepo.save(userFeed.get());
            return ResponseEntity.ok("Restored content from archeive");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id not found");
    }


    @GetMapping("/view/friends")
    public List<List<UserFeed>> friendsfeed() {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user=userRepository.findById(email);
        ArrayList<String> myfrdlist=user.get().getFriends();
        return myfrdlist.stream().map((frd) -> userFeedRepo.findAllByEmailAndAvailableAndVisibility(frd,true,"friends")).collect(Collectors.toList());

    }

    @GetMapping("/view/public")
    public List<UserFeed> publicfeed() {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        return userFeedRepo.findAllByVisibility("public");
    }


}
