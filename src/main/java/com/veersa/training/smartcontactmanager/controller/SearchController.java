package com.veersa.training.smartcontactmanager.controller;

import com.veersa.training.smartcontactmanager.entities.Contact;
import com.veersa.training.smartcontactmanager.entities.User;
import com.veersa.training.smartcontactmanager.repositories.ContactRepository;
import com.veersa.training.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    /* Search Handler */
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
        try {
            if (query == null || query.trim().isEmpty()) {
                // Handle the case where the query is empty or null
                return ResponseEntity.ok(Collections.emptyList()); // or another appropriate response
            }

            System.out.println(query);
            User userByUserName = this.userRepository.getUserByUserName(principal.getName());
            List<Contact> listContacts = this.contactRepository.findByNameContainingAndUser(query, userByUserName);
            return ResponseEntity.ok(listContacts);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during the search.");
        }
    }
}
