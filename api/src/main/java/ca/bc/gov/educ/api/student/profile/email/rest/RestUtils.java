package ca.bc.gov.educ.api.student.profile.email.rest;

import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.struct.CHESEmailEntity;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    if (this.props.getIsEmailNotificationSwitchedOn() != null && this.props.getIsEmailNotificationSwitchedOn()) {
      this.chesWebClient
          .post()
          .uri(this.props.getChesEndpointURL())
          .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .body(Mono.just(chesEmail), CHESEmailEntity.class)
          .retrieve()
          .bodyToMono(String.class)
          .doOnError(error -> this.logError(error, chesEmail))
          .doOnSuccess(success -> this.onSendEmailSuccess(success, chesEmail))
          .block();
    } else {
      log.info("email outbound to CHES is switched off");
    }
  }

  private void logError(final Throwable throwable, final CHESEmailEntity chesEmailEntity) {
    log.error("Error from CHES API call :: {} ", chesEmailEntity, throwable);
  }

  private void onSendEmailSuccess(final String s, final CHESEmailEntity chesEmailEntity) {
    log.info("Email sent success :: {} :: {}", chesEmailEntity, s);
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

  public CHESEmailEntity getChesEmail(final String fromEmail, final List<String> toEmail, final String body, final String subject) {
    final CHESEmailEntity chesEmail = new CHESEmailEntity();
    chesEmail.setBody(body);
    chesEmail.setBodyType("html");
    chesEmail.setDelayTS(0);
    chesEmail.setEncoding("utf-8");
    chesEmail.setFrom(fromEmail);
    chesEmail.setPriority("normal");
    chesEmail.setSubject(subject);
    chesEmail.setTag("tag");
    chesEmail.getTo().addAll(toEmail);
    return chesEmail;
  }

  public void sendEmail(final String emailAddress, final String body, final String subject) {
    this.sendEmail(this.getChesEmail(emailAddress, body, subject));
  }

  public void sendEmail(final String fromEmail, final List<String> toEmail, final String body, final String subject) {
    this.sendEmail(this.getChesEmail(fromEmail, toEmail, body, subject));
  }
}
