package com.projects.cardpayment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.cardpayment.entities.User;

@Repository
public interface UserAppRepository extends JpaRepository<User, Integer> {
	
	User findByUserNameAndPassword(String userName,String password);

}
