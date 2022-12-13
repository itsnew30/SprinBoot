package com.ezen.springboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ezen.springboard.entity.Board;
import com.ezen.springboard.entity.BoardFile;
import com.ezen.springboard.entity.BoardFileId;

public interface BoardFileRepository extends JpaRepository<BoardFile, BoardFileId>{
	//@Query: 원하는 쿼리를 작성할 수 있는 어노테이션
	//nativeQuery: true 설정하면 원하는 대로 쿼리 작성, 메소드명도 JPA 규칙에서 벗어날 수 있다.  
	//value: 쿼리 작성
	@Query(value="SELECT IFNULL(MAX(BOARD_FILE_NO), 0) +1 FROM T_BOARD_FILE F WHERE F.BOARD_NO=:boardNo", nativeQuery=true)
	//ServiceImpl에서 넘겨주는 파라미터의 변수명이 받아주는 변수이름과 다를 때 해당 파라미터이름을 명시 
	//매퍼나 repository에 여러개의 파라미터를 보낼 때 @Param을 어노테이션이용 
	int getMaxFileNo(@Param("boardNo") int boardNo);
	// 레포지토리 사용 시 매퍼 안써도 되고 어려운 쿼리는 mapper 이용해서 짠다.
	
	// SELECT * FROM T_BOARD_FILE
	// WHERE BOARD_NO = :boardNo
	List<BoardFile> findByBoard(Board board); // 매게변수:  보드 엔티티
}
