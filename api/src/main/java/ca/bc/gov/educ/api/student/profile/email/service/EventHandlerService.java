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
  private final UMPEmailService umpEmailService;

  @Getter(PRIVATE)
  private final GMPEmailService gmpEmailService;

  @Autowired
  public EventHandlerService(final UMPEmailService umpEmailService, final EmailEventService emailEventService, GMPEmailService gmpEmailService) {
    this.umpEmailService = umpEmailService;
    this.emailEventService = emailEventService;
    this.gmpEmailService = gmpEmailService;
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
        case NOTIFY_STUDENT_PEN_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_COMPLETE event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleNotifyStudentPenRequestComplete(event);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_RETURN event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleNotifyStudentPenRequestReturn(event);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_REJECT event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          handleNotifyStudentPenRequestReject(event);
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
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    UMPRequestCompleteEmailEntity umpRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(UMPRequestCompleteEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getUmpEmailService().sendCompletedRequestEmail(umpRequestCompleteEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending complete email", ex);
      }
    }
  }

  private void handleNotifyStudentProfileRequestReturn(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    UMPAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(UMPAdditionalInfoEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getUmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending return email for UMP", ex);
      }
    }
  }

  private void handleNotifyStudentProfileRequestReject(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    UMPRequestRejectedEmailEntity rejectedEmail = JsonUtil.getJsonObjectFromString(UMPRequestRejectedEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getUmpEmailService().sendRejectedRequestEmail(rejectedEmail);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending reject email for UMP", ex);
      }
    }
  }

  private void handlePenRequestEmailOutboxProcessed(String eventId) {
    getEmailEventService().updateEventStatus(UUID.fromString(eventId), MESSAGE_PUBLISHED.toString());
  }
  private void handleNotifyStudentPenRequestComplete(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    GMPRequestCompleteEmailEntity penRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestCompleteEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getGmpEmailService().sendCompletedPENRequestEmail(penRequestCompleteEmailEntity, penRequestCompleteEmailEntity.getDemographicsChanged());
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending complete email for GMP", ex);
      }
    }
  }

  private void handleNotifyStudentPenRequestReturn(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    GMPRequestAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestAdditionalInfoEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getGmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending return email for GMP", ex);
      }
    }
  }
  private void handleNotifyStudentPenRequestReject(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    GMPRequestRejectedEmailEntity gmpRequestRejectedEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestRejectedEmailEntity.class, event.getEventPayload());
    if (emailEvent.getEventStatus().equalsIgnoreCase(PENDING_EMAIL_ACK.toString())) {
      try {
        getGmpEmailService().sendRejectedPENRequestEmail(gmpRequestRejectedEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), DB_COMMITTED.toString());
      } catch (Exception ex) {
        log.error("exception occurred while sending reject email for GMP", ex);
      }
    }
  }

}
