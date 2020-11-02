package ca.bc.gov.educ.api.student.profile.email.support;
import ca.bc.gov.educ.api.student.profile.email.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.email.messaging.MessageSubscriber;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.schedulers.EventTaskScheduler;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * The type Mock configuration.
 */
@Profile("test")
@Configuration
public class MockConfiguration {
  /**
   * Message publisher message publisher.
   *
   * @return the message publisher
   */
  @Bean
  @Primary
  public MessagePublisher messagePublisher() {
    return Mockito.mock(MessagePublisher.class);
  }

  /**
   * Message subscriber message subscriber.
   *
   * @return the message subscriber
   */
  @Bean
  @Primary
  public MessageSubscriber messageSubscriber() {
    return Mockito.mock(MessageSubscriber.class);
  }

  /**
   * Event task scheduler event task scheduler.
   *
   * @return the event task scheduler
   */
  @Bean
  @Primary
  public EventTaskScheduler eventTaskScheduler() {
    return Mockito.mock(EventTaskScheduler.class);
  }

  @Bean
  @Primary
  public RestUtils restUtils() {
    return Mockito.mock(RestUtils.class);
  }

  @Bean
  @Primary
  RestTemplate restTemplate() {
    return Mockito.mock(RestTemplate.class);
  }

}
