package com.ltlogic;

import com.ltlogic.service.scheduler.SchedulerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


@PropertySources({
    @PropertySource( value = "classpath:local-application.properties" ),
    @PropertySource( value = "classpath:${envTarget}-application.properties", ignoreResourceNotFound = true)
})
@SpringBootApplication
@EnableScheduling
public class XmsApplication {

	public static void main(String[] args) {
            SpringApplication.run(XmsApplication.class, args);
            
            SpringApplication.run(SchedulerApplication.class);
	}
}
