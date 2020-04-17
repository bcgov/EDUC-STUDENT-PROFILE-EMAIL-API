package ca.bc.gov.educ.api.student.email.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StudentEmailMVCConfig implements WebMvcConfigurer {

    @Getter(AccessLevel.PRIVATE)
    private final StudentEmailRequestInterceptor studentEmailRequestInterceptor;

    @Autowired
    public StudentEmailMVCConfig(final StudentEmailRequestInterceptor studentEmailRequestInterceptor){
        this.studentEmailRequestInterceptor = studentEmailRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(studentEmailRequestInterceptor).addPathPatterns("/**/**/");
    }
}
