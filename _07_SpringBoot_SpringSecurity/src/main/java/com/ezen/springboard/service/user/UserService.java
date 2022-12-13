package com.ezen.springboard.service.user;

import com.ezen.springboard.entity.User;

public interface UserService {
	
	void join(User user);
	
	//user Entity 리턴하는 idCheck
	User idCheck(User user);

}
