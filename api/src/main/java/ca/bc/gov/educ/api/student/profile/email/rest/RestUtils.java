package ca.bc.gov.educ.api.student.profile.email.rest;

import ca.bc.gov.educ.api.student.profile.email.exception.StudentEmailRuntimeException;
import ca.bc.gov.educ.api.student.profile.email.model.CHESEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * This class is used for REST calls
 *
 * @author Marco Villeneuve
 */
@Component
@Slf4j
public class RestUtils {


  public RestUtils(@Autowired final ApplicationProperties props) {
    this.props = props;
  }

  private final ApplicationProperties props;

  public RestTemplate getRestTemplate() {
    return getCHESRestTemplate();
  }

  public RestTemplate getCHESRestTemplate() {
    log.debug("Calling get token method");
    ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
    resourceDetails.setClientId(props.getChesClientID());
    resourceDetails.setClientSecret(props.getChesClientSecret());
    resourceDetails.setAccessTokenUri(props.getChesTokenURL());
    return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());
  }

  public String getCHESEmailJsonObjectAsString(String emailAddress, String body, String subject) {
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
