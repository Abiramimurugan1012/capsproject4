package com.capestone.capsproject.Service;

import com.capestone.capsproject.Repo.UserRepository;
import com.capestone.capsproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;
@Service
public class EmailService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JavaMailSender mailSender;
    public void register(User user) throws MessagingException, UnsupportedEncodingException {
        Random r=new Random();
        int n=r.nextInt();
        String code =Integer.toHexString(n);
        user.setVerifyotp(code);
        userRepository.save(user);
        sendVerificationEmail(user,code);

    }

    private void sendVerificationEmail(User user,String code) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "muruganabirami476@gmail.com";
        String senderName = "Capstone";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br><br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">Click Me To Verify</a></h3><br><br>"
                + "Thank you,<br>"
                + "Capstone";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getFname()+" "+user.getLname());
        String verifyURL = "http://127.0.0.1:8080/api/verify?code=" + code+"-"+user.getEmail();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }


    public void forgotPassword(User user) throws MessagingException,UnsupportedEncodingException {
        Random r=new Random();
        int n=r.nextInt();
        String code=Integer.toHexString(n);
        user.setVerifyotp(code);
        System.out.println(code);
        userRepository.save(user);
        sendForgotMail(user,code);
    }
    @RequestMapping("/forgot")
    public void sendForgotMail(User user,String code) throws MessagingException,UnsupportedEncodingException {
        String toAddress= user.getEmail();
        String fromAddress="muruganabirami476@gmail.com";
        String senderName="Capstone";
        String subject="forgot password";
        String content="Dear [[name]],<br>"
                +"click the link to change the password<br><br>"
                +"<h3><a href=\"[[URL]]\" target=\"_self\">Change password(click me)</a></h3><br><br>"
                +"Thank you,<br>"
                +"Capstone";

        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message);
        helper.setFrom(fromAddress,senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content=content.replace("[[name]]",user.getFname()+" "+user.getLname());
        String verifyURL="http://127.0.0.1:8080/forgot?code="+code+"-"+user.getEmail();
        content=content.replace("[[URL]]",verifyURL);
        helper.setText(content,true);
        mailSender.send(message);
    }
}
