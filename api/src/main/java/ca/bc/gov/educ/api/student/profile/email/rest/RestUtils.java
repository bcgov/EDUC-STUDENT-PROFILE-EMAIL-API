package ca.bc.gov.educ.api.student.profile.email.rest;

import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
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

}
