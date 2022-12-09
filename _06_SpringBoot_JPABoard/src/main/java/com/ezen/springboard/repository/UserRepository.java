package com.ezen.springboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ezen.springboard.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
	User findByUserIdAndUserPw(
			@Param("userId") String userId,
			@Param("userPw") String userPw);
}
