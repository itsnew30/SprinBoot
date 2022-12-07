package com.exen.springboard.configuration;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// 이 클래스가 설정 파일이다 알려주는 것
@Configuration
// 어떤 파일을 읽어 올건지 -> classpath:/application.properties 이거 읽어올거
@PropertySource("classpath:/application.properties")
public class DataConfiguration {
	
	@Bean // 대부분 생성자 메소드 위에 선언
	// application.properties 파일의 어떤 내용을 읽어서 설정할지 지정해줌
	// prefix="spring.datasource.hikari 내용을 읽어서 HikariConfig로 던져줌
	@ConfigurationProperties(prefix="spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}
	
	@Bean
	public DataSource dataSource() throws Exception{
		DataSource dataSource = new HikariDataSource(hikariConfig());
		return dataSource;
	}
	
	
	
	
	
	
	
	
	
	
	
}
