package com.veersa.training.smartcontactmanager.repositories;

import com.veersa.training.smartcontactmanager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
