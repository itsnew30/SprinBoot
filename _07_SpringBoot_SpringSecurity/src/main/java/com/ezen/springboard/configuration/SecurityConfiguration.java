package com.ezen.springboard.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ezen.springboard.handler.LoginFailureHandler;
import com.ezen.springboard.oauth.Oauth2UserService;

@Configuration
//security의 filterchain을 구현하기 위한 어노테이션 - 필수적으로 작성해줘야함 
@EnableWebSecurity
public class SecurityConfiguration {
	@Autowired
	private LoginFailureHandler loginFailureHandler;
	
	@Autowired
	private Oauth2UserService oauth2UserService;
	// 비밀번호 암호화를 위한 PasswordEncoder // 어디서든 사용할 수 있게 static으로 설정 
	// security에 의해 로그인 처리될 때 비밀번호 비교시 무조건 사용
	// 복호화는 불가능, match(사용자입력(그냥 String), DB에 저장된 암호화된 비밀번호) => true나 false로 리턴 
	@Bean 
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	//필터 체인 구현(HttpSecurity 객체 사용) 
	@Bean 
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//권한에 따른 요청 주소 매핑 
		//http.authorizeHttpRequests().antMatchers("/").permitAll(); //모두에게 허용
		//그래서 인증없이 http://localhost:9090/ 주소창에 넣고 엔터치면 게시판 홈 바로 나옴 
		
		// 홈컨트롤러에 mainpage()메소드 생성 후 권한에 따른 요청 주소 매핑
												//"/home"으로 시작하는 요청리소스는 모든 사람에게 허용 
		http.authorizeRequests().antMatchers("/").permitAll()
									.antMatchers("/home/**").permitAll()
									//css, js, iamges, upload 같은 정적 리소스들도 권한 처리 필수 
									.antMatchers("/css/**").permitAll()
									.antMatchers("/js/**").permitAll()
									.antMatchers("/images/**").permitAll()
									.antMatchers("/upload/**").permitAll()
									.antMatchers("/user/join").permitAll()
									.antMatchers("/user/login").permitAll()
									.antMatchers("/user/idCheck").permitAll()
									.antMatchers("/user/loginProc").permitAll()
									//권한을 가지고 있는 유저들만 접는근할 수 있는 요청리소스 설정
									//Authentication객체를 만든 다음에 가져올 수 있는 권한들
									.antMatchers("/board/**").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
									.antMatchers("/adim/**").access("hasRole('ROLE_ADMIN')")
									//위에 설정하는 요청 주소 제외한 나머지 요청 리소스는 인증된 사용자만 접근가능. 
									.anyRequest().authenticated();
		
		//로그인, 로그아웃 설정 
		//AuthenticationProvider에게 사용자가 입력한 정보로 만든 
		//인증용 객체 UsernamePassowordAuthenticationToken을 전달 상태까지 설정(4번까지 설정 완료 day05)
		http.formLogin()
		    .loginPage("/user/login")
		    //Spring Security에서는 id는 username,
		    //비밀번호는 password 사용 
		    //다른 키 값으로 사용하고 싶으면 username, password 키값과 매핑하여 사용한다. >> 우리는 userPw으로 되어 있어 매핑해주는 기능을 사용해야한다.
		    .usernameParameter("userId")
			.passwordParameter("userPw")
			//로그인 요청이 오면 security에서 낚아채서 처리해준다.
			//낚아챌 로그인 요청 url 지정 
			.loginProcessingUrl("/user/loginProc")
			//loginProcessingUrl메소드사용하려면 ajax를 form으로 수정해야함 login.html으로 이동
			//로그인 성공 후 띄워줄 화면 url
		    .defaultSuccessUrl("/home/main")
			.failureHandler(loginFailureHandler)
			//OAuth기반 로그인 처리
			.and()
			.oauth2Login()
			.loginPage("/user/login")
			//토큰 발행 후 처리
			//토큰이 발행되면 사용자 정보를 받아서 처리 가능해지는 데, 사용자 정보를 웹 사이트에 맞도록 변경해주는 작업 필요
			.userInfoEndpoint() // 사용자 정보를 다 가지고 왔을 때
			//사용자 정보를 웹 사이트에 맞도록 변경해주는 service클래스 등록
			.userService(oauth2UserService);
		http.logout()
			.logoutUrl("/user/logout")
			.invalidateHttpSession(true)
			.logoutSuccessUrl("/user/login");
		
		// 크로스도메인 이슈를 방지설정 	
		// 크로스도메인: 웹 생태계 원칙적으로 하나의 웹 어플리케이션에서 요청에 대한 주소가 변경이 되면 안된다
		// localhost:3000(리액트 화면단 어플리케이션) 다른 도메인으로 요청 -> localhost:9090 (스프링부트 API)
		// ...다른 도메인으로 요청을 보내면 해킹이슈가 발생함 요새는 api를 요청해야하기 때문에 서버단에 풀어주는 방식으로 하고 있음
		// 스프링부트(백엔드) 어플리케이션에서 크로스도메인(해당 도메인을 지정하여)을 허용해주는 식으로 사용 
		// CorsRegistry객체를 사용해서 해당 도메인을 크로스 메인으로 허용
		// CorsRegistry.addMapping("허용될 요청 url")
		//             .allowedOrigins("허용될 해당 도메인 주소 : (ex)http://localhost:3000")
		//             .allowedMethods("허용될 요청 메소드 타입("GET", "POST", ....)")
		//             타임아웃시간, 인증에 대한 요청, 요청 헤더를 허용 여부 등을 설정 
		// 열어주는 것이 더 상위로 적용됨.
		//기본으로 크로스도메인 방지
		http.csrf().disable();
			
		return http.build(); 
	}
}
