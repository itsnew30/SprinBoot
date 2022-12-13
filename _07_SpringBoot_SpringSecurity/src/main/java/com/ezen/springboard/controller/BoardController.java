package com.ezen.springboard.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ezen.springboard.common.FileUtils;
import com.ezen.springboard.dto.BoardDTO;
import com.ezen.springboard.dto.BoardFileDTO;
import com.ezen.springboard.dto.ResponseDTO;
import com.ezen.springboard.dto.UserDTO;
import com.ezen.springboard.entity.Board;
import com.ezen.springboard.entity.BoardFile;
import com.ezen.springboard.service.board.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/board")
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/boardList")
	public ModelAndView getBoardList(BoardDTO boardDTO, 
			@PageableDefault(page=0, size=10) Pageable pageable){ // 데이터 받아오고 하는 것은 DTO사용
		Board board = Board.builder()
				           .boardTitle(boardDTO.getBoardTitle())
				           .boardContent(boardDTO.getBoardContent())
				           .boardWriter(boardDTO.getBoardWriter())
				           .searchCondition(boardDTO.getSearchCondition())
				           .searchKeyword(boardDTO.getSearchKeyword())
				           .build();
		List<Board> boardList = boardService.getBoardList(board);
		
		Page<Board> pageBoardList = boardService.getPageBoardList(board, pageable); //원래는 하나만 그런데 편의상 하나 더 만듦
														//pageBoard 엔티티 라서 pageboard에서 꺼내야함
		Page<BoardDTO>pageBoardDTOList = pageBoardList.map(pageBoard -> 
														   BoardDTO.builder()
														   		   .boardNo(pageBoard.getBoardNo())
														   		   .boardTitle(pageBoard.getBoardTitle())
														   		   .boardContent(pageBoard.getBoardContent())
														   		   .boardWriter(pageBoard.getBoardWriter())
														   		   .boardRegdate(
														   				   		   pageBoard.getBoardRegdate() == null?
																				   null :
																				   pageBoard.getBoardRegdate().toString())
														   		   .boardCnt(pageBoard.getBoardCnt())
														   		   .build()
														   );  //화살표함수 람다식
		
		// 화면으로 보낼 때는 boardDTO로 보내줘야함
		List<BoardDTO> getBoardList = new ArrayList<BoardDTO>();
		
		for(int i=0; i<boardList.size(); i++) {
			BoardDTO returnBoard = BoardDTO.builder()
										   .boardNo(boardList.get(i).getBoardNo())
										   .boardTitle(boardList.get(i).getBoardTitle())
										   .boardContent(boardList.get(i).getBoardContent())
										   .boardWriter(boardList.get(i).getBoardWriter())
										   .boardRegdate(
												   boardList.get(i).getBoardRegdate() == null?
												   null : 
												   boardList.get(i).getBoardRegdate().toString())//NULL값은 toString 안되서 에러남..이전에 null 값이 있었음
										   .boardCnt(boardList.get(i).getBoardCnt())
										   .build();
			//builder로 내용 추가한 객체를 생성
			getBoardList.add(returnBoard);
		}
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("board/getBoardList.html");
		mv.addObject("getBoardList", pageBoardDTOList); //-> 콘텐트부터 페이져블까지만 오게됨
		
		if(boardDTO.getSearchCondition() != null && !boardDTO.getSearchCondition().equals("")){
			mv.addObject("searchCondition", boardDTO.getSearchCondition());
		}
		if(boardDTO.getSearchKeyword() != null && !boardDTO.getSearchKeyword().equals("")){
			mv.addObject("searchKeyword", boardDTO.getSearchKeyword());
		}
		
		return mv;
	}
	
	@GetMapping("/updateBoardCnt/{boardNo}")
	public void updateBoardCnt(@PathVariable int boardNo, HttpServletResponse response) throws IOException {
		boardService.updateBoardCnt(boardNo);
		
		response.sendRedirect("/board/board/" + boardNo);	
	}
	
	//th:href="@{/board/board/{boardNo} (boardNo=${board.boardNo})}">
	@GetMapping("/board/{boardNo}")
	public ModelAndView getBoard(@PathVariable int boardNo){
		// 데이터를 가져오는 것 
		Board board = boardService.getBoard(boardNo);
		
		BoardDTO boardDTO = BoardDTO.builder()
									.boardNo(board.getBoardNo())
									.boardTitle(board.getBoardTitle())
									.boardContent(board.getBoardContent())
									.boardWriter(board.getBoardWriter())
									.boardRegdate(
												board.getBoardRegdate() == null?
												null :
												board.getBoardRegdate().toString()) // .toString 수정 
									.boardCnt(board.getBoardCnt())
									.build();
		
		//여기서 보드 파일리스트도 함께 가져와야함 
		List<BoardFile> boardFileList = boardService.getBoardFileList(boardNo);
		List<BoardFileDTO> boardFileDTOList = new ArrayList<BoardFileDTO>();
		
		for(BoardFile boardFile : boardFileList ) {
			BoardFileDTO boardFileDTO = BoardFileDTO.builder()
													.boardNo(boardNo)
													.boardFileNo(boardFile.getBoardFileNo())
													.boardFileNm(boardFile.getBoardFileNm())
													.boardOriginFileNm(boardFile.getBoardFileNm())
													.boardFilePath(boardFile.getBoardFilePath())
													.boardFileRegdate(boardFile.getBoardFileRegdate().toString())
													.boardFileCate(boardFile.getBoardFileCate())
													.build();
			System.out.println(boardFileDTO.toString());
			
			boardFileDTOList.add(boardFileDTO);
		}	
		
				
		ModelAndView mv = new ModelAndView();
		mv.setViewName("board/getBoard.html");
		mv.addObject("getBoard", boardDTO);
		//            이 키값은 getBoard.html th:each="boardFile : ${boardFileList}"의 boardFileList이름과 동일하게 작성
		mv.addObject("boardFileList", boardFileDTOList);

		return mv;
	}
	
//	@PutMapping("/board")
//	public void updateBoard(BoardDTO boardDTO, HttpServletResponse response) throws IOException {
//		Board board = Board.builder()
//							.boardNo(boardDTO.getBoardNo())
//							.boardTitle(boardDTO.getBoardTitle())
//							.boardContent(boardDTO.getBoardContent())
//							.build();
//		boardService.updateBoard(board); 
//		
//		response.sendRedirect("/board/board"+boardDTO.getBoardNo());
//	}
	
	@Transactional // 쿼리가 실행된 후 바로 트랜잭션을 호출 // 다이나믹쿼리랑 함께 쓰면 null값을 무시해줌
	@PutMapping("/board")
	public ResponseEntity<?> updateBoard(BoardDTO boardDTO, HttpServletResponse response,
			MultipartFile[] uploadFiles, MultipartFile[] changedFiles, HttpServletRequest request,
			@RequestParam("originFiles") String originFiles) throws IOException {
		ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();
		
		// 제이슨 스트링 놈이 List<BoardFileDTO> 형태로 오게된다.
		List<BoardFileDTO> originFileList = new ObjectMapper().readValue(originFiles, 
												new TypeReference<List<BoardFileDTO>>() {});
		
		String attachPath = request.getSession().getServletContext().getRealPath("/") + "/upload/";
		
		File directory = new File(attachPath);
		
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		// DB에서 수정, 삭제, 추가 될 파일 정보를 담는 리스트
		List<BoardFile> uFileList = new ArrayList<BoardFile>();

		try {
			Board board = Board.builder()
					.boardNo(boardDTO.getBoardNo())
					.boardTitle(boardDTO.getBoardTitle())
					.boardContent(boardDTO.getBoardContent())
					.boardWriter(boardDTO.getBoardWriter()) // 세팅을 해주는 방법 뿐 
					.boardRegdate(
							boardDTO.getBoardRegdate() == null ||
							boardDTO.getBoardRegdate().equals("") ? 
							null :
							LocalDateTime.parse(boardDTO.getBoardRegdate())) // boardDTO.getBoardRegdate()를 LocalDateTime.parse로 형변환
					.boardCnt(boardDTO.getBoardCnt()) // getBoard에서 받는 값을 보냈기 때문에 추가로 작성해봐야한다. 
					.build();
			
			//파일 처리
			for(int i=0; i<originFileList.size(); i++) {
				//수정되는 파일 처리
				if(originFileList.get(i).getBoardFileStatus().equals("U")) {
					for(int j=0; j < changedFiles.length; j++) {
						if(originFileList.get(i).getNewFileNm().equals(changedFiles[j].getOriginalFilename())) { //NewFileNm은 getBoard.html 만들어줌
							BoardFile boardFile = new BoardFile(); // entity 객체 
							
							MultipartFile file = changedFiles[j];
							
							boardFile = FileUtils.parseFileInfo(file, attachPath); // FileUtils를 통해서 DB 저장형식에 맞게 전환
							
							boardFile.setBoard(board);//boardNo을 담고 있는 board
							boardFile.setBoardFileNo(originFileList.get(i).getBoardFileNo()); 
							boardFile.setBoardFileStatus("U");
							
							//이렇게 되면 'boardFile' entity가 생성 됨 
							
							uFileList.add(boardFile);
						}
					}
					
				// 삭제되는 파일 처리 	
				} else if (originFileList.get(i).getBoardFileStatus().equals("D")) {
					BoardFile boardFile = new BoardFile();
					
					boardFile.setBoard(board);
					boardFile.setBoardFileNo(originFileList.get(i).getBoardFileNo());
					boardFile.setBoardFileStatus("D");
					
					// 실제 파일 삭제 
					File dFile = new File(attachPath + originFileList.get(i).getBoardFileNm());
					dFile.delete();
					
					//DB에서도 삭제 되어하니깐
					uFileList.add(boardFile);
				}
			}
			
			//추가된 파일 처리 
			if(uploadFiles.length > 0 ) {
				for(int i=0; i<uploadFiles.length; i++) {
					MultipartFile file = uploadFiles[i];
					
					if(file.getOriginalFilename() != null && 
							!file.getOriginalFilename().equals("")) {
						BoardFile boardFile = new BoardFile(); // BoardFile entity 생성
						boardFile = FileUtils.parseFileInfo(file, attachPath);
						
						boardFile.setBoard(board);
						boardFile.setBoardFileStatus("I");
						
						// 마찬가지로 DB에서도 수정되어야 하니깐 
						uFileList.add(boardFile);
					}
				}
			}
			//uFileList도 같이 보내줘야함
			boardService.updateBoard(board, uFileList); 
			//board = boardService.getBoard(boardDTO.getBoardNo());
			
			BoardDTO returnBoard = BoardDTO.builder()
											   .boardNo(board.getBoardNo())
											   .boardTitle(board.getBoardTitle())
											   .boardContent(board.getBoardContent())
											   .boardWriter(board.getBoardWriter())
											   .boardRegdate(
													   board.getBoardRegdate() == null?
													   null :
													   board.getBoardRegdate().toString())
											   .boardCnt(board.getBoardCnt())
											   .build();
			
			//
			List<BoardFile> boardFileList = boardService.getBoardFileList(board.getBoardNo());
			// DTO 변환
			List<BoardFileDTO> boardFileDTOList = new ArrayList<BoardFileDTO>();
			
			//boardFileDTO를 builder로 만들어줌
			for(BoardFile boardFile : boardFileList) {
				BoardFileDTO boardFileDTO = BoardFileDTO.builder()
												.boardNo(board.getBoardNo())
												.boardFileNo(boardFile.getBoardFileNo())
												.boardFileNm(boardFile.getBoardOriginFileNm())
												.boardOriginFileNm(boardFile.getBoardOriginFileNm())
												.boardFilePath(boardFile.getBoardFilePath())
												.boardFileRegdate(boardFile.getBoardFileRegdate().toString())
												.boardFileCate(boardFile.getBoardFileCate())
												.build();
				boardFileDTOList.add(boardFileDTO);
			}
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap.put("getBoard", returnBoard);
			returnMap.put("boardFileList", boardFileDTOList);
			responseDTO.setItem(returnMap);
			
			return ResponseEntity.ok().body(responseDTO);
			
		} catch(Exception e) {
			responseDTO.setErrorMessage(e.getMessage());
			
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
	
	@DeleteMapping("/board")
	public void deleteBoard(@RequestParam("boardNo") int boardNo) {
		boardService.deleteBoard(boardNo);				
	}
	
	@GetMapping("/insertBoard")
	public ModelAndView insertBoardView(HttpSession session) throws IOException {
		UserDTO loginUser = (UserDTO)session.getAttribute("loginUser");

		ModelAndView mv = new ModelAndView();
		
		if(loginUser == null ){
			mv.setViewName("user/login.html");
		} else {
			mv.setViewName("board/insertBoard.html");
		}
		
		return mv;
	}
	
	@PostMapping("/board")
	public void insertBoard(BoardDTO boardDTO, MultipartFile[] uploadFiles,
			HttpServletResponse response, HttpServletRequest request) throws IOException { // response 다시 게시글 목록으로 갈 수 있게
		// 3가지만 받은 엔티티 생성 
		Board board = Board.builder()
						   .boardTitle(boardDTO.getBoardTitle())
						   .boardContent(boardDTO.getBoardContent())
						   .boardWriter(boardDTO.getBoardWriter())
						   .boardRegdate(LocalDateTime.now())
						   .build();
		//DB에 입력될 파일 정보리스트 
		System.out.println(uploadFiles.length);
		List<BoardFile> uploadFileList = new ArrayList<BoardFile>();
		if(uploadFiles.length>0) {	
			String attachPath  = request.getSession().getServletContext().getRealPath("/")
					+ "/upload/";
			
			File directory  = new File(attachPath);
			if(!directory.exists()) {
				directory.mkdir();
			}
			
			//mutipartFile 형식의 데이터를 DB테이블에 맞는 구조로 변경
			for(int i=0; i<uploadFiles.length; i++) {
				MultipartFile file = uploadFiles[i];
				
				if(!file.getOriginalFilename().equals("") &&
					file.getOriginalFilename() != null ) {
					BoardFile boardFile = new BoardFile();
					
					boardFile = FileUtils.parseFileInfo(file, attachPath);
					uploadFileList.add(boardFile);
				}
			}
		}
		
		boardService.insertBoard(board, uploadFileList);
		
		response.sendRedirect("/board/boardList");
	}
	
	@GetMapping("/pageBoardListApi")
	public ResponseEntity<?> getPageBoardList(Board board, Pageable pageable){
		ResponseDTO<Page<BoardDTO>> responseDTO = new ResponseDTO<>();
		
		try {
			Page<Board> pageBoardList = boardService.getPageBoardList(board, pageable);
			
			Page<BoardDTO>pageBoardDTOList = pageBoardList.map(pageBoard -> 
															   BoardDTO.builder()
															   		   .boardNo(pageBoard.getBoardNo())
															   		   .boardTitle(pageBoard.getBoardTitle())
															   		   .boardContent(pageBoard.getBoardContent())
															   		   .boardWriter(pageBoard.getBoardWriter())
															   		   .boardRegdate(
																					   pageBoard.getBoardRegdate() == null?
																					   null :
															   				           pageBoard.getBoardRegdate().toString())
															   		   .boardCnt(pageBoard.getBoardCnt())
															   		   .build()
															   );  
			
			responseDTO.setItem(pageBoardDTOList);
			
			return ResponseEntity.ok().body(responseDTO);
					
		} catch(Exception e) {
			responseDTO.setErrorMessage(e.getMessage());
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
}
