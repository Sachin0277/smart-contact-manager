package com.veersa.training.smartcontactmanager.repositories;

import com.veersa.training.smartcontactmanager.entities.Contact;
import com.veersa.training.smartcontactmanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer> {

    @Query("from Contact as c where c.user.id =:userId")
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.name LIKE %:keyword% AND c.user = :user")
    List<Contact> findByNameContainingAndUser(@Param("keyword") String keyword, @Param("user") User user);

}
