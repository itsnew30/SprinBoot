package com.exen.springboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data // @Data: 모든 메소드 통함 (getter, setter, toString)
@NoArgsConstructor
@AllArgsConstructor
@Builder // 생성자 함수 호출 안하고 객체를 만듦
public class TestDTO {
	private int testNo;
	private String testTitle;
}
