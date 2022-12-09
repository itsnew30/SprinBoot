package com.ezen.springboard.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
public class WebMvcConfiguration {
	@Bean
	public HiddenHttpMethodFilter httpMethodFilter() {
		HiddenHttpMethodFilter hiddenHttpMehodFilter = new HiddenHttpMethodFilter();
		return hiddenHttpMehodFilter;
	}
}
