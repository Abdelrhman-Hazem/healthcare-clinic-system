package com.kfh.clinic.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public GroupedOpenApi clinicApi() {
		return GroupedOpenApi.builder()
				.group("clinic")
				.pathsToMatch("/api/**")
				.build();
	}
}

