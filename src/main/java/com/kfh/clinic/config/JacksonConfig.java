package com.kfh.clinic.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

    // @Bean
    // public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    //     return builder -> {
    //         builder.modules(new JavaTimeModule());
    //         builder.featuresToDisable(
    //                 SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
    //         );
    //     };
    // }

//    @Bean
//    @Primary
//    public JsonMapper jsonMapper() {
//        return JsonMapper.builder()
//        .addModule(new JavaTimeModule())
//        .disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES)
//        .build();
//    }
}
