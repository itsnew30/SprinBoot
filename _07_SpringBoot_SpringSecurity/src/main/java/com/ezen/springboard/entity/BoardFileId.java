package com.ezen.springboard.entity;

import java.io.Serializable;

import lombok.Data;

@Data   
//복합키 만들기 위해서 BoardFileId 클래스 생성함 
public class BoardFileId implements Serializable{	
	private int board;
	private int boardFileNo;
}
