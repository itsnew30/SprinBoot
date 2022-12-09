package com.ezen.springboard.service.board.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.springboard.dto.BoardDTO;
import com.ezen.springboard.mapper.BoardMapper;
import com.ezen.springboard.service.board.BoardService;

@Service
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardMapper boardMapper;

	@Override
	public BoardDTO getBoard(int boardNo) {
		return boardMapper.getBoard(boardNo);
	}

	@Override
	public List<BoardDTO> getBoardList() {
		// TODO Auto-generated method stub
		return boardMapper.getBoardList();
	}

	@Override
	public void insertBoard(BoardDTO boardDTO) {
		// TODO Auto-generated method stub
		boardMapper.insertBoard(boardDTO);
	}

	@Override
	public void updateBoard(BoardDTO boardDTO) {
		// TODO Auto-generated method stub
		boardMapper.updateBoard(boardDTO);
	}

	@Override
	public void deleteBoard(int boardNo) {
		// TODO Auto-generated method stub
		boardMapper.deleteBoard(boardNo);
	}
}
