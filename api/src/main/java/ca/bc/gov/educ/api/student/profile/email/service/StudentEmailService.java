package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.exception.InvalidParameterException;
import ca.bc.gov.educ.api.student.profile.email.exception.StudentEmailRuntimeException;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.util.JWTUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Collections;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BASIC;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;

@Service
@Slf4j
public class StudentEmailService {

  private static final String PERSONAL_EDUCATION_NUMBER_PEN_REQUEST = "Your Personal Education Number (PEN) Request";
  private static final String VERIFY_EMAIL_SUBJECT = "Activate your GetMyPEN request within 24 hours of receiving this email";
  private final ApplicationProperties props;
  private final RestUtils restUtils;
  private final JWTUtil jwtUtil;

  @Autowired
  public StudentEmailService(final ApplicationProperties props, final RestUtils restUtils, final JWTUtil jwtUtil) {
    this.props = props;
    this.restUtils = restUtils;
    this.jwtUtil = jwtUtil;
  }

  public void sendCompletedRequestEmail(RequestCompleteEmailEntity penRequest) {
    String loginUrl = getLoginUrl(penRequest);
    log.debug("Sending completed PEN email");
    sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateCompletedRequest().replace("'", "''"), penRequest.getFirstName(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Completed PEN email sent successfully");
  }


  public void sendRejectedRequestEmail(RequestRejectedEmailEntity penRequest) {
    String loginUrl = getLoginUrl(penRequest);
    log.debug("Sending rejected PEN email");
    sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateRejectedRequest().replace("'", "''"), penRequest.getRejectionReason(), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Rejected PEN email sent successfully");
  }

  public void sendAdditionalInfoEmail(RequestAdditionalInfoEmailEntity penRequest) {
    String loginUrl = getLoginUrl(penRequest);
    log.debug("Sending additional info PEN email");
    sendEmail(penRequest, MessageFormat.format(props.getEmailTemplateAdditionalInfo().replace("'", "''"), loginUrl, loginUrl, loginUrl), PERSONAL_EDUCATION_NUMBER_PEN_REQUEST);
    log.debug("Additional info PEN email sent successfully");
  }

  /**
   * This method is responsible to send verification email.
   *
   * @param emailVerificationEntity the payload containing the pen request id and email.
   */
  public void sendVerifyEmail(RequestEmailVerificationEntity emailVerificationEntity) {
    log.debug("sending verify email.");
    final String token = jwtUtil.createJWTToken(emailVerificationEntity.getPenRequestId(), emailVerificationEntity.getEmailAddress(), 0L, props.getTimeToLive());
    final String email = MessageFormat.format(props.getEmailTemplateVerifyEmail().replace("'", "''"), emailVerificationEntity.getIdentityTypeLabel(), token, emailVerificationEntity.getIdentityTypeLabel(), token, token);
    sendEmail(emailVerificationEntity, email, VERIFY_EMAIL_SUBJECT);
    log.debug("verification email sent successfully.");
  }

  public void sendEmail(BaseEmailEntity penRequest, String body, String subject) {

    RestTemplate restTemplate = restUtils.getRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>(getCHESEmailJsonObjectAsString(penRequest.getEmailAddress(), body, subject), headers);
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

  private String getLoginUrl(BaseEmailEntity baseEmailEntity) {
    if (BCSC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBcsc();
    } else if (BASIC.toString().equalsIgnoreCase(baseEmailEntity.getIdentityType())) {
      return props.getLoginBasic();
    } else {
      throw new InvalidParameterException("IdentityType provided, could not be resolved. :: "+baseEmailEntity.getIdentityType());
    }

  }
}
