package ca.bc.gov.educ.api.student.profile.email.rest;

import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.CHESEmailEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * This class is used for REST calls
 *
 * @author Marco Villeneuve
 */
@Component
@Slf4j
public class RestUtils {
  private final WebClient chesWebClient;
  private final ApplicationProperties props;

  public RestUtils(@Qualifier("chesWebClient") final WebClient chesWebClient, final ApplicationProperties props) {
    this.chesWebClient = chesWebClient;
    this.props = props;
  }


  /**
   * Send email.
   *
   * @param chesEmail the ches email json object as string
   */
  public void sendEmail(final CHESEmailEntity chesEmail) {
    this.chesWebClient
        .post()
        .uri(this.props.getChesEndpointURL())
        .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(Mono.just(chesEmail), CHESEmailEntity.class)
        .retrieve().bodyToMono(String.class).subscribeOn(Schedulers.parallel()).doOnError(this::logError).doOnSuccess(this::onSendEmailSuccess).block();
  }

  private void logError(final Throwable throwable) {
    log.error("Error from CHES API call", throwable);
  }

  private void onSendEmailSuccess(final String s) {
    log.info("Email sent success :: {}", s);
  }

  public CHESEmailEntity getChesEmail(final String emailAddress, final String body, final String subject) {
    final CHESEmailEntity chesEmail = new CHESEmailEntity();
    chesEmail.setBody(body);
    chesEmail.setBodyType("html");
    chesEmail.setDelayTS(0);
    chesEmail.setEncoding("utf-8");
    chesEmail.setFrom("noreply.getmypen@gov.bc.ca");
    chesEmail.setPriority("normal");
    chesEmail.setSubject(subject);
    chesEmail.setTag("tag");
    chesEmail.getTo().add(emailAddress);
    return chesEmail;
  }

  public CHESEmailEntity getChesEmail(final String fromEmail, final String toEmail, final String body, final String subject) {
    final CHESEmailEntity chesEmail = new CHESEmailEntity();
    chesEmail.setBody(body);
    chesEmail.setBodyType("html");
    chesEmail.setDelayTS(0);
    chesEmail.setEncoding("utf-8");
    chesEmail.setFrom(fromEmail);
    chesEmail.setPriority("normal");
    chesEmail.setSubject(subject);
    chesEmail.setTag("tag");
    chesEmail.getTo().add(toEmail);
    return chesEmail;
  }

  public void sendEmail(final String emailAddress, final String body, final String subject) {
    this.sendEmail(this.getChesEmail(emailAddress, body, subject));
  }

  public void sendEmail(final String fromEmail, final String toEmail, final String body, final String subject) {
    this.sendEmail(this.getChesEmail(fromEmail, toEmail, body, subject));
  }
}
