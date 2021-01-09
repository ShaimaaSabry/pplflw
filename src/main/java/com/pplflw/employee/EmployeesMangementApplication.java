package com.pplflw.employee;

import java.util.Collections;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class EmployeesMangementApplication {
    public static void main(String[] args) {
	SpringApplication.run(EmployeesMangementApplication.class, args);
    }

    @Bean
    public Docket Swagger() {
	ApiInfo apiInfo = new ApiInfo(
		"PeopleFlow: Employees Management API",
		"A RESTful API service for retrieving, creating, and updating employees.",
		"1.0",
		null,
		null,
		null,
		null,
		Collections.emptyList()
		);
	
	return new Docket(DocumentationType.SWAGGER_2)
		.select()
		.apis(RequestHandlerSelectors.basePackage("com.pplflw.employee"))
		.build()
		.apiInfo(apiInfo);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
