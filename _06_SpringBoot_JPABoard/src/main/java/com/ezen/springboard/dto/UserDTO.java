package com.ezen.springboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //기본 생성자
@AllArgsConstructor //모든 매개변수를 받는 생성자
@Builder //객체 생성
public class UserDTO {
	private String userId;
	private String userPw;
	private String userNm;
	private String userMail;
	private String userTel;
	private String userRegdate;
	private String userRole;
	
}
