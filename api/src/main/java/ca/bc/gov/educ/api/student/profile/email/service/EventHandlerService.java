package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.*;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {

  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing.";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
      " just updating the db status so that it will be polled and sent back again.";
  public static final String PAYLOAD_LOG = "payload is :: {}";
  public static final String EVENT_PAYLOAD = "event is :: {}";
  @Getter(PRIVATE)
  private final EmailEventService emailEventService;

  @Getter(PRIVATE)
  private final StudentEmailService emailService;

  @Autowired
  public EventHandlerService(final StudentEmailService emailService, final EmailEventService emailEventService) {
    this.emailService = emailService;
    this.emailEventService = emailEventService;
  }

  public void handleEvent(Event event) {
    try {
      switch (event.getEventType()) {
        case PROFILE_REQUEST_EMAIL_API_EVENT_OUTBOX_PROCESSED:
          log.info("received outbox processed event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handlePenRequestEmailOutboxProcessed(event.getEventPayload());
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleNotifyStudentProfileRequestComplete(event);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_RETURN event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleNotifyStudentProfileRequestReturn(event);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_REJECT event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleNotifyStudentProfileRequestReject(event);
          break;
        default:
          log.info("silently ignoring other events.");
          break;
      }
    } catch (final Exception e) {
      log.error("Exception occurred :: ", e);
    }
  }

  private void handleNotifyStudentProfileRequestComplete(Event event) throws JsonProcessingException {
    StudentProfileReqEmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    RequestCompleteEmailEntity requestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(RequestCompleteEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getEmailService().sendCompletedRequestEmail(requestCompleteEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending complete email", ex);
      }
    }
  }

  private void handleNotifyStudentProfileRequestReturn(Event event) throws JsonProcessingException {
    StudentProfileReqEmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    RequestAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(RequestAdditionalInfoEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending return email", ex);
      }
    }
  }

  private void handleNotifyStudentProfileRequestReject(Event event) throws JsonProcessingException {
    StudentProfileReqEmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    RequestRejectedEmailEntity rejectedEmail = JsonUtil.getJsonObjectFromString(RequestRejectedEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getEmailService().sendRejectedRequestEmail(rejectedEmail);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending reject email", ex);
      }
    }
  }

  private void handlePenRequestEmailOutboxProcessed(String eventId) {
    getEmailEventService().updateEventStatus(UUID.fromString(eventId), MESSAGE_PUBLISHED.toString());
  }
}
