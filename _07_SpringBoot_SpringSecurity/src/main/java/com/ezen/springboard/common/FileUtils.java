package com.ezen.springboard.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.ezen.springboard.entity.BoardFile;

//boardFileVO boardFile로 수정하고 import com.ezen.springboard.entity.BoardFile; 변경 
public class FileUtils {
		// Map<String, String> => 파일 업로드 기능이 여러군데에서 사용 될 때 범용성을 높이기 위해
		// Map을 사용한다. Map을 사용할 경우 매퍼 까지 Map으로 보내준다.
		public static BoardFile parseFileInfo (MultipartFile file, 
				String attachPath) throws IOException {
			BoardFile boardFile = new BoardFile();
			
			String boardOriginFileNm = file.getOriginalFilename();
			
			//같은 파일명을 업로드했을 때 덮어써지지 않게 하기 위한 실제 업로드 되는 파일명 설정
			SimpleDateFormat formmater = new SimpleDateFormat("yyyyMMddHHmmss");
			Date nowDate = new Date();
			String nowDateStr = formmater.format(nowDate);
			UUID uuid = UUID.randomUUID(); // 유효아이디 만들기 -> 겹치지 않게 하기 위해서
			
			String boardFileNm = nowDateStr + "_" + uuid.toString() +"_"+ boardOriginFileNm;
			String boardFilePath = attachPath;
			
			//이미지인지 다른 파일 형태 인지 검사
			File checkFile = new File(boardOriginFileNm);
			//업로드한 파일의 형식을 가져옴(이미지파일들은 image/jpg, image/png ....) 
			String type=  Files.probeContentType(checkFile.toPath()); // checkFile의 type이 나오게 됨
			
			if(type != null) {
				if(type.startsWith("image")) {
					boardFile.setBoardFileCate("img");
				} else {
					boardFile.setBoardFileCate("etc");
				}						
			} else {
				boardFile.setBoardFileCate("etc");
			}	
			 
			boardFile.setBoardFileNm(boardFileNm);
			boardFile.setBoardOriginFileNm(boardOriginFileNm);
			boardFile.setBoardFilePath(boardFilePath);

			// 실제 파일 업로드            파일경로        파일이름
			File uploadFile = new File(attachPath + boardFileNm);
			// 매개변수는 업로드 될 폴더와 파일명을 파일객체의 형태로 넣어준다.
			//파일 업로드 시 IOException처리
			file.transferTo(uploadFile); // WAS 루트 폴더에 업로드 됨
		
			return boardFile;		
		}
}
