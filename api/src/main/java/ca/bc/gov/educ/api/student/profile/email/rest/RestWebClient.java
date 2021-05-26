package ca.bc.gov.educ.api.student.profile.email.rest;

import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

/**
 * The type Rest web client.
 */
@Configuration
@Profile("!test")
public class RestWebClient {
  private final DefaultUriBuilderFactory factory;
  private final ClientHttpConnector connector;
  /**
   * The Props.
   */
  private final ApplicationProperties props;

  /**
   * Instantiates a new Rest web client.
   *
   * @param props the props
   */
  public RestWebClient(final ApplicationProperties props) {
    this.props = props;
    this.factory = new DefaultUriBuilderFactory();
    this.factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
    final HttpClient client = HttpClient.create().compress(true);
    client.warmup()
      .block();
    this.connector = new ReactorClientHttpConnector(client);
  }


  @Bean
  @Autowired
  WebClient webClient(final WebClient.Builder builder) {
    val clientRegistryRepo = new InMemoryReactiveClientRegistrationRepository(ClientRegistration
      .withRegistrationId(this.props.getChesClientID())
      .tokenUri(this.props.getChesTokenURL())
      .clientId(this.props.getChesClientID())
      .clientSecret(this.props.getChesClientSecret())
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .build());
    val clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistryRepo);
    val authorizedClientManager =
      new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistryRepo, clientService);
    val oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
    oauthFilter.setDefaultClientRegistrationId(this.props.getChesClientID());
    return builder
      .codecs(configurer -> configurer
        .defaultCodecs()
        .maxInMemorySize(100 * 1024 * 1024))
      .clientConnector(this.connector)
      .uriBuilderFactory(this.factory)
      .filter(oauthFilter)
      .build();
  }
}
