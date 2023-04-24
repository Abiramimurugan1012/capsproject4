package com.capestone.capsproject.Controller;


import com.capestone.capsproject.Repo.UserRepository;
import com.capestone.capsproject.Service.EmailService;
import com.capestone.capsproject.Service.UserService;
import com.capestone.capsproject.jwt.JwtToken;
import com.capestone.capsproject.model.LoginAuthRequest;
import com.capestone.capsproject.model.LoginAuthResponse;
import com.capestone.capsproject.model.User;
import com.sun.source.tree.CatchTree;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

//import javax.mail.MessagingException;
//import java.io.UnsupportedEncodingException;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserAuthController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    JwtToken jwttoken;
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
  @Autowired
  PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public  String signIn(@RequestBody User user) throws MessagingException, UnsupportedEncodingException {
      if(!userRepository.existsById(user.getEmail())) {
          BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
          String encryptPwd = bcrypt.encode(user.getPassword());
          user.setPassword(encryptPwd);
          user.setFriends(new ArrayList<>());
          user.setFriendrequest(new ArrayList<>());
          userRepository.save(user);
          emailService.register(user);
          return "Verificaion mail sended.";
      } else {
          Optional<User> userDetails=userRepository.findById(user.getEmail());
          if(userDetails.get().getEnabled()) {
              return "Already exists.try alter mail";
          }
          else {
              return "waiting for verification";
          }

      }


      }

     @GetMapping("/verify")
    public String verifyUser(@RequestParam String code) {
        String[] paramList=code.split("-");
        String verifyCode = paramList[0];
        String email=paramList[1];
        Optional<User> user=userRepository.findById(email);

        if(user.get().getVerifyotp().equals(verifyCode) && user.get().getVerifyotp() !=null) {
         //   System.out.println(verifyCode);
                user.get().setEnabled(true);
                user.get().setVerifyotp(null);
                userRepository.save(user.get());
                return "Account verified";
        }
        else {
                return "Invalid Url";
        }
     }
     @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody LoginAuthRequest loginAuthRequest) {
         String email=loginAuthRequest.getEmail();
//         System.out.println(email);
         String password=loginAuthRequest.getPassword();
//         System.out.println(password);
         if(userRepository.existsById(email)) {
             Optional<User> user=userRepository.findById(email);
//             System.out.println(user.get());
             if(user.get().getEnabled()){
                try {

                     System.out.println(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password)));
                 }catch (Exception e){
//                     System.out.println(e);
                     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
                 }
                // System.out.println("sssss");
                 UserDetails userDetails= userService.loadUserByUsername(loginAuthRequest.getEmail());
//                 System.out.println(userDetails);
                 String jwt= jwttoken.generateToken(userDetails);
//                 System.out.println(jwt);
                 return ResponseEntity.ok(jwt);
             }
             else{
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body("Account is not enabled");
             }
         }
         else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Account no found, Please create an account.");
         }

     }

     @GetMapping("/forgot/{email}")
    public ResponseEntity<?> forgotPwdLink(@PathVariable String email) throws MessagingException, UnsupportedEncodingException {
        if(userRepository.existsById(email)) {
            Optional<User> user = userRepository.findById(email);
            emailService.forgotPassword(user.get());
            return ResponseEntity.ok("Forgot password link sent to " +email);
        }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found");
     }
     @PostMapping("/forgot/verify")
    public ResponseEntity<?> forgotPassword(@RequestBody HashMap<String,String> forgotUser, @RequestParam String code) {
        String[] paramList=code.split("-");
        String verifyCode=paramList[0];
        String emailId=paramList[1];
        try {
            Optional<User> user = userRepository.findById(emailId);
            if (user.get().getVerifyotp() !=null && user.get().getVerifyotp().equals(verifyCode)) {
                if(forgotUser.get("new Password").equals(forgotUser.get("renew password"))) {
                    user.get().setPassword(passwordEncoder.encode(forgotUser.get("new Password")));
                    user.get().setVerifyotp(null);
                    userRepository.save(user.get());
                    return ResponseEntity.ok("Password changed");
                }
                else {
                    return ResponseEntity.status(HttpStatus.OK).body("confirm password not matched");
                }
            }

                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not valid verification");

            }

        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification");
        }

     }



}
