package com.capestone.capsproject.Controller;

import com.capestone.capsproject.Repo.UserRepository;
import com.capestone.capsproject.UserDto.UserDto;
import com.capestone.capsproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PutMapping("/update")
    public ResponseEntity<?> profileUpdate( @RequestBody UserDto userDto, BindingResult bindingResult) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
       // System.out.println(email);
        if (userRepository.existsById(email)) {
            Optional<User> user = userRepository.findById(email);
            if (bindingResult.getAllErrors().isEmpty()) {
                user.get().setFname(userDto.getFname());

                userRepository.save(user.get());
                return ResponseEntity.ok("User has been updated");

            }
                List<ObjectError> errors = bindingResult.getAllErrors();
                List<String> listError = new ArrayList<>();
                for (ObjectError i : errors) {
                    listError.add(i.getDefaultMessage());
                }
                HashMap<String, List> errorMessage = new HashMap<>();
                errorMessage.put("Validation error", listError);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        @GetMapping("/requestlist")
        public ResponseEntity<?> requestList() {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user=userRepository.findOneById(email);
        if(!user.get().getFriendrequest().isEmpty()) {
            return ResponseEntity.ok(user.get().getFriendrequest());
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Friends request list is empty");
        }
        }

        @GetMapping("/myfriends")
        public ResponseEntity<?> friendsList() {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user=userRepository.findById(email);
            if(!user.get().getFriends().isEmpty()) {
                return ResponseEntity.ok(user.get().getFriends());
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Friends list is empty");
            }

        }
        @GetMapping("/follow/{id}")
        public ResponseEntity<?> follow(@PathVariable String id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user=userRepository.findById(email);
        Optional<User> thatUser=userRepository.findById(id);
        if(userRepository.existsById(id)) {
            if(user.get().getFriends().contains(id)) {
                if(thatUser.get().getFriendrequest().contains(email)) {
                    return ResponseEntity.ok("Already friend request present.");
                }
                return ResponseEntity.ok("You are already friend with each other");
            }
            user.get().getFriends().add(id);
            thatUser.get().getFriendrequest().add(email);
            userRepository.save(user.get());
            userRepository.save(thatUser.get());
            return ResponseEntity.ok("Friend request sent to "+id);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        @GetMapping("/unfollow/{id}")
        public ResponseEntity<?> unfollow(@PathVariable String id) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userRepository.existsById(email) && userRepository.existsById(id)){
        Optional<User> Myuser=userRepository.findById(email);
        Optional<User> thatUser=userRepository.findById(id);
        if(Myuser.get().getFriends().contains(email) && thatUser.get().getFriends().contains(id)) {
            Myuser.get().getFriends().remove(email);
            thatUser.get().getFriends().remove(id);
            userRepository.save(Myuser.get());
            userRepository.save(thatUser.get());
            return ResponseEntity.ok("Friend removed You are not a friend");

        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Friend not in the list");
        }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
            }
        }
        @GetMapping("/accept/{id}")
        public ResponseEntity<?> accept(@PathVariable String id) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userRepository.existsById(email)&&userRepository.existsById(id)) {
            Optional<User> user=userRepository.findById(email);
            Optional<User> thatUser=userRepository.findById(id);
            if (user.get().getFriendrequest().contains(id)) {
                user.get().getFriends().add(id);
                ArrayList<String> MyUserReq=user.get().getFriendrequest();
                MyUserReq.remove(id);
                thatUser.get().getFriends().add(email);
                userRepository.save(user.get());
                userRepository.save(thatUser.get());
                return ResponseEntity.ok("Friend request accept");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("friend request not found");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        @GetMapping("/reject/{id}")
        public ResponseEntity<?> reject(@PathVariable String id) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user=userRepository.findById(email);
        if(user.isPresent()) {
            user.get().getFriendrequest().remove(id);
            userRepository.save(user.get());
            return ResponseEntity.ok("Friend request rejected");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Friend request not found");
        }

}
