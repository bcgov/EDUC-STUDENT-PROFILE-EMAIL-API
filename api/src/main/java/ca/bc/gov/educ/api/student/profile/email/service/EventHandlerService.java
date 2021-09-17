package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.EmailNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump.*;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.*;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService extends BaseEventHandlerService {


  public static final String EXCEPTION_FOR = "Exception for :: {}";
  @Getter(PRIVATE)
  private final EmailEventService emailEventService;

  @Getter(PRIVATE)
  private final UMPEmailService umpEmailService;

  @Getter(PRIVATE)
  private final GMPEmailService gmpEmailService;

  @Getter(PRIVATE)
  private final EmailNotificationService emailNotificationService;

  @Autowired
  public EventHandlerService(final UMPEmailService umpEmailService, final EmailEventService emailEventService, final GMPEmailService gmpEmailService, final EmailNotificationService emailNotificationService) {
    this.umpEmailService = umpEmailService;
    this.emailEventService = emailEventService;
    this.gmpEmailService = gmpEmailService;
    this.emailNotificationService = emailNotificationService;
  }


  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestComplete(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.STUDENT_NOTIFIED); // make sure the db operation is successful before sending the email.
    final UMPRequestCompleteEmailEntity umpRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(UMPRequestCompleteEmailEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getUmpEmailService().sendCompletedRequestEmail(umpRequestCompleteEmailEntity);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn(EXCEPTION_FOR, event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestReturn(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.STUDENT_NOTIFIED);// make sure the db operation is successful before sending the email.
    final UMPAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(UMPAdditionalInfoEmailEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getUmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn(EXCEPTION_FOR, event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestReject(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.STUDENT_NOTIFIED);// make sure the db operation is successful before sending the email.
    final UMPRequestRejectedEmailEntity rejectedEmail = JsonUtil.getJsonObjectFromString(UMPRequestRejectedEmailEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getUmpEmailService().sendRejectedRequestEmail(rejectedEmail);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn(EXCEPTION_FOR, event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestComplete(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.STUDENT_NOTIFIED); // make sure the db operation is successful before sending the email.
    final GMPRequestCompleteEmailEntity penRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestCompleteEmailEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getGmpEmailService().sendCompletedPENRequestEmail(penRequestCompleteEmailEntity, penRequestCompleteEmailEntity.getDemographicsChanged());
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn(EXCEPTION_FOR, event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestReturn(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.STUDENT_NOTIFIED);// make sure the db operation is successful before sending the email.
    final GMPRequestAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestAdditionalInfoEmailEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getGmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn(EXCEPTION_FOR, event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestReject(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.STUDENT_NOTIFIED);// make sure the db operation is successful before sending the email.
    final GMPRequestRejectedEmailEntity gmpRequestRejectedEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestRejectedEmailEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getGmpEmailService().sendRejectedPENRequestEmail(gmpRequestRejectedEmailEntity);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn(EXCEPTION_FOR, event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleSendEmail(final Event event) throws JsonProcessingException {
    final var emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.EMAIL_SENT);// make sure the db operation is successful before sending the email.
    final var emailNotificationEntity = JsonUtil.getJsonObjectFromString(EmailNotificationEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getEmailNotificationService().sendEmail(emailNotificationEntity);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn(EXCEPTION_FOR, event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

}
