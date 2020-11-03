package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.model.BaseEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@Slf4j
public class CHESEmailService {
  private final RestUtils restUtils;
  private final ApplicationProperties props;

  @Autowired
  public CHESEmailService(RestUtils restUtils, ApplicationProperties props) {
    this.restUtils = restUtils;
    this.props = props;
  }

  // Retry logic, if server encounters any issue such as communication failure etc..
  @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(multiplier = 2, delay = 2000))
  public void sendEmail(BaseEmailEntity baseEmailEntity, String body, String subject) {

    RestTemplate restTemplate = restUtils.getRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>(restUtils.getCHESEmailJsonObjectAsString(baseEmailEntity.getEmailAddress(), body, subject), headers);
    //Send the email via CHES
    restTemplate.postForObject(props.getChesEndpointURL(), request, String.class);
  }


}
