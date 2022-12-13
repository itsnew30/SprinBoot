package com.ezen.springboard.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.springboard.entity.User;
import com.ezen.springboard.repository.UserRepository;
import com.ezen.springboard.service.user.UserService;

@Service
public class UserServiceImpl implements UserService {
//	@Autowired
//	private UserMapper userMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void join(User user) {
		userRepository.save(user);
	}
	
	@Override
	public User idCheck(User user) {
		// null값이 empty로 와서 userRepository.findById(user.getUserId()).get()으로 불러올 때 에러 발생해서 if문 활용해서 null이 아닐 때 만듦. 
		if(!userRepository.findById(user.getUserId()).isEmpty())
			return userRepository.findById(user.getUserId()).get();
		else 
			return null; //비어있으면 null을 리턴
	}
	
}
