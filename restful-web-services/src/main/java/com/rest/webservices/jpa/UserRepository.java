package com.rest.webservices.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rest.webservices.user.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
