package com.ezen.springboard.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Table(name="T_BOARD_FILE")
@Data
// 복합키니까 IdClass
@IdClass(BoardFileId.class)
public class BoardFile {
	@Id
	@ManyToOne 
	@JoinColumn(name="BOARD_NO")
	//private int boardNo;
	
	private Board board; // foreign key 되었으니 이렇게 작성
	@Id
	private int boardFileNo;
	private String boardFileNm;
	private String boardOriginFileNm;
	private String boardFilePath;
	private LocalDateTime boardFileRegDate = LocalDateTime.now();
	private String boardFileCate;
	@Transient
	private String boardFileStatus;
	
}
