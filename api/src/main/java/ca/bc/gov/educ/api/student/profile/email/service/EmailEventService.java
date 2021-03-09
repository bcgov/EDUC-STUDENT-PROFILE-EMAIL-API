package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.MESSAGE_PUBLISHED;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.PENDING_EMAIL_ACK;
import static ca.bc.gov.educ.api.student.profile.email.service.EventHandlerService.*;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EmailEventService {
  @Getter(PRIVATE)
  private final StudentProfileRequestEmailEventRepository emailEventRepository;

  @Autowired
  public EmailEventService(final StudentProfileRequestEmailEventRepository emailEventRepository) {
    this.emailEventRepository = emailEventRepository;
  }

  /**
   * must use new transaction, so that data is committed, user must not be notified if db transaction fails.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public EmailEventEntity createOrUpdateEventInDB(final Event event, EventOutcome eventOutcome) {
    final var emailEventEntityOptional = this.getEmailEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    final EmailEventEntity emailEventEntity;
    if (emailEventEntityOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      event.setEventOutcome(eventOutcome);
      emailEventEntity = this.createEmailEvent(event);
      return this.getEmailEventRepository().save(emailEventEntity);
    } else if (!PENDING_EMAIL_ACK.toString().equalsIgnoreCase(emailEventEntityOptional.get().getEventStatus())) {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      emailEventEntity = emailEventEntityOptional.get();
      emailEventEntity.setEventStatus(MESSAGE_PUBLISHED.toString());
      return this.getEmailEventRepository().save(emailEventEntity);
    }
    return emailEventEntityOptional.get();
  }

  @Transactional
  // Retry logic, if server encounters any issue such as communication failure etc..
  @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(multiplier = 2, delay = 2000))
  public void updateEventStatus(final UUID eventId, final String eventStatus) {
    val emailEventEntity = this.getEmailEventRepository().findById(eventId);
    if (emailEventEntity.isPresent()) {
      val emailEvent = emailEventEntity.get();
      emailEvent.setEventStatus(eventStatus);
      this.getEmailEventRepository().save(emailEvent);
    }
  }

  private EmailEventEntity createEmailEvent(final Event event) {
    return EmailEventEntity.builder()
        .createDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now())
        .createUser(event.getEventType().toString().substring(0, 32)) //need to discuss what to put here.
        .updateUser(event.getEventType().toString().substring(0, 32)) //.substring(0, 32)
        .eventPayload(event.getEventPayload())
        .eventType(event.getEventType().toString())
        .sagaId(event.getSagaId())
        .eventStatus(PENDING_EMAIL_ACK.toString())
        .eventOutcome(event.getEventOutcome().toString())
        .replyChannel(event.getReplyTo())
        .build();
  }

  public List<EmailEventEntity> getPendingEmailEvents(final LocalDateTime dateTimeToCompare) {
    return this.emailEventRepository.findByEventStatusAndCreateDateBefore(PENDING_EMAIL_ACK.getCode(), dateTimeToCompare);
  }
}
