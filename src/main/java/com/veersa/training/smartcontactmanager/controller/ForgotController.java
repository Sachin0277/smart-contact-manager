package com.veersa.training.smartcontactmanager.controller;


import com.veersa.training.smartcontactmanager.entities.User;
import com.veersa.training.smartcontactmanager.repositories.UserRepository;
import com.veersa.training.smartcontactmanager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    Random random = new Random(1000);

    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){
        System.out.println("EMAIL "+email);
        /* Generate 4 digit otp */
        int otp = random.nextInt(9999);
        System.out.println("OTP :"+otp);
        /* Sending otp to email */
        String subject = "OTP from SCM";
        String message = " "
                +"<div style='border:1px solid #e2e2e2'; padding:20px;>"
                +"<h1>"
                +"<b>"
                +"OTP is "+otp
                +"</b>"
                +"<h1>"
                +"</div>";
        boolean flag = this.emailService.sendEmail(subject,message, email);
        if(flag){
            session.setAttribute("myotp",otp);
            session.setAttribute("email",email);
            return "verify_otp";

        }
        else{
            session.setAttribute("message","check your email id!!!");
            return "forgot_email_form";
        }

    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session){
        int myOtp = (int)session.getAttribute("myotp");
        String email = (String) session.getAttribute("email");
        if(myOtp == otp){
            /* Good to go*/
            User user = this.userRepository.getUserByUserName(email);
            if(user == null){

                //send error message
                session.setAttribute("message","User does not exist with this email !!!");
                return "forgot_email_form";
            }
            else{
                // send password change form
            }
            return "password_change_form";
        }
        else{
            session.setAttribute("message","You have entered wrong OTP!!");
            return "verify_otp";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newPassword,HttpSession session){
        String email = (String)session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(user);
        return "redirect:/signin?change=password changed successfully";
    }
}
