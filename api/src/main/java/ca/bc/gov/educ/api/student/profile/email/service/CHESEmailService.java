package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.exception.StudentEmailRuntimeException;
import ca.bc.gov.educ.api.student.profile.email.model.BaseEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.model.CHESEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    HttpEntity<String> request = new HttpEntity<>(getCHESEmailJsonObjectAsString(baseEmailEntity.getEmailAddress(), body, subject), headers);
    //Send the email via CHES
    restTemplate.postForObject(props.getChesEndpointURL(), request, String.class);
  }

  private String getCHESEmailJsonObjectAsString(String emailAddress, String body, String subject) {
    ObjectMapper objectMapper = new ObjectMapper();
    CHESEmailEntity chesEmail = new CHESEmailEntity();
    chesEmail.setBody(body);
    chesEmail.setBodyType("html");
    chesEmail.setDelayTS(0);
    chesEmail.setEncoding("utf-8");
    chesEmail.setFrom("noreply.getmypen@gov.bc.ca");
    chesEmail.setPriority("normal");
    chesEmail.setSubject(subject);
    chesEmail.setTag("tag");
    chesEmail.getTo().add(emailAddress);

    try {
      return objectMapper.writeValueAsString(chesEmail);
    } catch (JsonProcessingException e) {
      log.error("JsonProcessingException", e);
      throw new StudentEmailRuntimeException(e.getMessage());
    }
  }
}
