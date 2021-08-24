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

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.PENDING_EMAIL_ACK;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.PROCESSING;
import static ca.bc.gov.educ.api.student.profile.email.service.EventHandlerService.EVENT_PAYLOAD;
import static ca.bc.gov.educ.api.student.profile.email.service.EventHandlerService.NO_RECORD_SAGA_ID_EVENT_TYPE;
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
  public EmailEventEntity createOrUpdateEventInDB(final Event event, final EventOutcome eventOutcome) {
    final var emailEventEntityOptional = this.getEmailEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    final EmailEventEntity emailEventEntity;
    if (emailEventEntityOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_PAYLOAD, event);
      event.setEventOutcome(eventOutcome);
      emailEventEntity = this.createEmailEvent(event);
      return this.getEmailEventRepository().save(emailEventEntity);
    }
    return emailEventEntityOptional.get();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  // Retry logic, if server encounters any issue such as communication failure etc..
  @Retryable(value = {Exception.class}, maxAttempts = 10, backoff = @Backoff(multiplier = 2, delay = 2000))
  public void updateEventStatus(final UUID eventId, final String eventStatus) {
    val emailEventEntity = this.getEmailEventRepository().findById(eventId);
    if (emailEventEntity.isPresent()) {
      val emailEvent = emailEventEntity.get();
      emailEvent.setEventStatus(eventStatus);
      emailEvent.setUpdateDate(LocalDateTime.now());
      this.getEmailEventRepository().save(emailEvent);
    }
  }

  private EmailEventEntity createEmailEvent(final Event event) {
    final var eventType = event.getEventType().toString();
    final var user = eventType.substring(0, Math.min(eventType.length(), 32));
    return EmailEventEntity.builder()
        .createDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now())
        .createUser(user) //need to discuss what to put here.
        .updateUser(user)
        .eventPayload(event.getEventPayload())
        .eventType(event.getEventType().toString())
        .sagaId(event.getSagaId())
        .eventStatus(PENDING_EMAIL_ACK.toString())
        .eventOutcome(event.getEventOutcome().toString())
        .replyChannel(event.getReplyTo())
        .build();
  }

  public List<EmailEventEntity> getPendingEmailEvents(final LocalDateTime dateTimeToCompare) {
    return this.emailEventRepository.findTop100ByEventStatusAndCreateDateBefore(PENDING_EMAIL_ACK.getCode(), dateTimeToCompare);
  }

  /**
   * this could happen in a scenario, where the pod died before setting it to proper status, so system needs to recover from that automagically.
   */
  public List<EmailEventEntity> getEventsStuckAtProcessing(final LocalDateTime dateTimeToCompare) {
    return this.emailEventRepository.findTop100ByEventStatusAndCreateDateBefore(PROCESSING.getCode(), dateTimeToCompare);
  }
}
