package com.veersa.training.smartcontactmanager.controller;

import com.veersa.training.smartcontactmanager.entities.Contact;
import com.veersa.training.smartcontactmanager.entities.User;
import com.veersa.training.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    /*@Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        User newUser = new User();
        newUser.setName("Sachin Yadav");
        newUser.setEmail("sachinyadav0278@gmail.com");
        newUser.setEnabled(true);
        newUser.setImageUrl("www.pixel.com/sachin.png");
        newUser.setRole("Intern");
        newUser.setPassword("123456789");
        newUser.setAbout("Sachin is a fresher who is doing java backend development.");
        Contact contact = new Contact();
        newUser.getContacts().add(contact);

        userRepository.save(newUser);
        return "working properly";
    }*/
}
