package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump.GMPRequestEmailVerificationEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

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


  @Before
  public void setUp() {
    openMocks(this);
  }


  @Test
  public void sendEmail() {
    final var payload = GMPRequestEmailVerificationEntity.builder().build();
    payload.setEmailAddress("test@gov.bc.ca");
    doNothing().when(this.restUtils).sendEmail(any(), any(), any());
    this.emailService.sendEmail(payload, "hello", "Request Completed");
    verify(this.restUtils, atLeastOnce()).sendEmail("test@gov.bc.ca", "hello", "Request Completed");
  }
}
