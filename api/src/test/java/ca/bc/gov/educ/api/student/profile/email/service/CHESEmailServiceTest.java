package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.exception.StudentEmailRuntimeException;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestEmailVerificationEntity;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class CHESEmailServiceTest {


  @Autowired
  CHESEmailService emailService;

  @Autowired
  RestUtils restUtils;

  @Autowired
  ApplicationProperties properties;

  @Autowired
  RestTemplate restTemplate;

  @Before
  public void setUp() {
    initMocks(this);
  }


  @Test
  public void sendEmail() {
    var payload = GMPRequestEmailVerificationEntity.builder().build();
    var args = new Object[]{"test@gov.bc.ca", "hello", "Request Completed"};
    payload.setEmailAddress("test@gov.bc.ca");
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    when(restUtils.getCHESEmailJsonObjectAsString("test@gov.bc.ca", "hello", "Request Completed")).thenReturn("Email");
    emailService.sendEmail(payload, "hello", "Request Completed");
    verify(restUtils, atLeastOnce()).getCHESEmailJsonObjectAsString("test@gov.bc.ca", "hello", "Request Completed");
    verify(restTemplate, atLeastOnce()).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }
}
