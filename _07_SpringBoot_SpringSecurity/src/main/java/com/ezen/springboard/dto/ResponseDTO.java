package com.ezen.springboard.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseDTO<T> { 
	private List<T> items;
	
	private T item; // 겟보드 같은 객체는 item으로 만들어서 던져버리는..
	
	private String errorMessage;
	
	private int statusCode;
}
