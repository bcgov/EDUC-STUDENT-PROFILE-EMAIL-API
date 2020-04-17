package ca.bc.gov.educ.api.student.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class StudentEmailApiResourceApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudentEmailApiResourceApplication.class, args);
  }

  @Configuration
  static
  class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
      web.ignoring().antMatchers("/v3/api-docs/**",
              "/actuator/**",
              "/swagger-ui/**", "/health");
    }
  }
}
