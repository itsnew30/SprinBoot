package com.ezen.springboard.service.user;

import com.ezen.springboard.entity.User;

public interface UserService {

	void join(User user);
	
	User idCheck(User user);
	
	User login(User user);

}
