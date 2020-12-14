package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.model.Event;
import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
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
import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome.STUDENT_NOTIFIED;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.*;
import static ca.bc.gov.educ.api.student.profile.email.service.EventHandlerService.*;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EmailEventService {
  @Getter(PRIVATE)
  private final StudentProfileRequestEmailEventRepository emailEventRepository;

  @Autowired
  public EmailEventService(StudentProfileRequestEmailEventRepository emailEventRepository) {
    this.emailEventRepository = emailEventRepository;
  }

  /**
   * must use new transaction, so that data is committed, user must not be notified if db transaction fails.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public EmailEventEntity createOrUpdateEventInDB(Event event) {
    var emailEventEntityOptional = getEmailEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    EmailEventEntity penRequestEvent;
    if (emailEventEntityOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      event.setEventOutcome(STUDENT_NOTIFIED);
      penRequestEvent = createPenRequestEmailEvent(event);
      return getEmailEventRepository().save(penRequestEvent);
    } else if (!PENDING_EMAIL_ACK.toString().equalsIgnoreCase(emailEventEntityOptional.get().getEventStatus())) {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      penRequestEvent = emailEventEntityOptional.get();
      penRequestEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
      return getEmailEventRepository().save(penRequestEvent);
    }
    return emailEventEntityOptional.get();
  }

  @Transactional
  // Retry logic, if server encounters any issue such as communication failure etc..
  @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(multiplier = 2, delay = 2000))
  public void updateEventStatus(UUID eventId, String eventStatus) {
    val emailEventEntity = getEmailEventRepository().findById(eventId);
    if (emailEventEntity.isPresent()) {
      val emailEvent = emailEventEntity.get();
      emailEvent.setEventStatus(eventStatus);
      getEmailEventRepository().save(emailEvent);
    }
  }

  private EmailEventEntity createPenRequestEmailEvent(Event event) {
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
}
