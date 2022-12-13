package com.ezen.springboard.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration //이 클래스가 설정파일이다.
@PropertySource("classpath:/application.properties") // jar에 있는.., 어떤 폴더 안에 있는 파일을 읽어서 사용할 것인지 선언문
//매퍼 인터페이지 위치 지정 (=>매퍼 읽을 위치) 
@MapperScan(basePackages="com.ezen.springboard.mapper") 
public class DataConfiguration {
	@Autowired
	// 스프링 컨테이너 호출 : 현재 프로젝트의 설정파일 등을 가져올 수 있다. 
	ApplicationContext applicationContext;
	
	@Bean //메소드 위에 bean을 붙여줌, 거의 대부분 생성자 메소드 위에 선언
	//application.properties 파일의 어떤 내용을 읽어서 설정할지 
	@ConfigurationProperties(prefix="spring.datasource.hikari")
	public HikariConfig hikariConfig() {	
		return new HikariConfig(); //히카리 컨피그를 만들어주는 bean 생성
	}

	@Bean
	public DataSource dateSource() throws Exception {
		DataSource dataSource = new HikariDataSource(hikariConfig());
		
		return dataSource;
	}
	
	//MyBatis 연동 
	@Bean 
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setConfigLocation(
				applicationContext.getResource("classpath:mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(
				applicationContext.getResources("classpath:mapper/**/*-mapper.xml"));

		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	//JPA 연동
	@Bean
	@ConfigurationProperties(prefix="spring.jpa")
	public Properties hibernateConfig() {
		
		return new Properties();		
	}
}
