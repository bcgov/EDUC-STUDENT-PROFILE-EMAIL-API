package ca.bc.gov.educ.api.student.profile.email.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StudentProfileEmailMVCConfig implements WebMvcConfigurer {

    @Getter(AccessLevel.PRIVATE)
    private final StudentProfileEmailRequestInterceptor studentProfileEmailRequestInterceptor;

    @Autowired
    public StudentProfileEmailMVCConfig(final StudentProfileEmailRequestInterceptor studentProfileEmailRequestInterceptor){
        this.studentProfileEmailRequestInterceptor = studentProfileEmailRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(studentProfileEmailRequestInterceptor).addPathPatterns("/**/**/");
    }
}
