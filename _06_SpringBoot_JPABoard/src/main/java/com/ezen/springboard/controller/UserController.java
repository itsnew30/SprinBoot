package com.ezen.springboard.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.ezen.springboard.dto.ResponseDTO;
import com.ezen.springboard.dto.UserDTO;
import com.ezen.springboard.entity.User;
import com.ezen.springboard.service.user.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	// 회원갑입 화면단으로 이동
	@GetMapping("/join")
	public ModelAndView joinView() {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("user/join.html");
		
		return mv;
	}
	
	@PostMapping("/join")
	public ResponseEntity<?> join(UserDTO userDTO){
		ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
		Map<String, String> returnMap = new HashMap<String, String>();
		try {
			User user = User.builder()
							.userId(userDTO.getUserId())
							.userPw(userDTO.getUserPw())
							.userNm(userDTO.getUserNm())
							.userMail(userDTO.getUserMail())
							.userTel(userDTO.getUserTel())
							.userRegdate(LocalDateTime.now())
							.build();
			
			// 유저서비스에서 조인의 유저 호출
			userService.join(user);
			
			returnMap.put("joinMsg", "JoinSuccess");
			
			responseDTO.setItem(returnMap);
			
			return ResponseEntity.ok().body(responseDTO);
							
		} catch(Exception e) { // 에러 발생시
			returnMap.put("JoinMsg","joinFail");
			responseDTO.setErrorMessage(e.getMessage());
			
			responseDTO.setItem(returnMap);
			
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
		
	@GetMapping("/login")
	public ModelAndView loginView() {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("user.login.html");
		return mv;
	}
		
	@PostMapping("/idCheck")
	public ResponseEntity<?> idCheck(UserDTO userDTO){
		ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
		Map<String, String> returnMap = new HashMap<String, String>();
		
		try {
			User user = User.builder()
						.userId(userDTO.getUserId())
						.build();
			
			User checkedUser = userService.idCheck(user);
			
			if(checkedUser != null) {
				returnMap.put("msg", "duplicatedId");
			} else {
				returnMap.put("msg", "idOK");
			}
			
			responseDTO.setItem(returnMap);
			
			return ResponseEntity.ok().body(responseDTO);
		
		} catch(Exception e) {
			responseDTO.setErrorMessage(e.getMessage());
			return ResponseEntity.badRequest().body(responseDTO);
		}
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(UserDTO userDTO, HttpSession session){
		ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
		Map<String, String> returnMap = new HashMap<String, String>();
		
		try {
			User user = User.builder()
						.userId(userDTO.getUserId())
						.userPw(userDTO.getUserPw())
						.build();
			
			User checkedUser = userService.idCheck(user);
			
			if(checkedUser == null) {
				returnMap.put("msg", "idFail");
			} else {
				//User loginUser = userService.login(user);
				
				if(checkedUser.getUserPw().equals(userDTO.getUserPw())) {
				returnMap.put("msg", "PwFail");
				} else {
					UserDTO loginUser = UserDTO.builder()
										 .userId(checkedUser.getUserId())
										 .userNm(checkedUser.getUserNm())
										 .userMail(checkedUser.getUserMail())
										 .userTel(checkedUser.getUserTel())
										 .userRegdate(checkedUser.getUserRegdate().toString())
										 .userRole(checkedUser.getUserRole())
										 .build();
					
					session.setAttribute("loginUser", loginUser);
					returnMap.put("msg", "loginSuccess");
				}
			}
			
			responseDTO.setItem(returnMap);
			
			return ResponseEntity.ok().body(responseDTO);
		
		} catch(Exception e) {
			responseDTO.setErrorMessage(e.getMessage());
			return ResponseEntity.badRequest().body(responseDTO);
		}
		
		
		
	}
	
	
	
	
	
	
	
}
	

