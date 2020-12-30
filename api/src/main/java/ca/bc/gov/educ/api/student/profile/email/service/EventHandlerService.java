package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.MESSAGE_PUBLISHED;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.PENDING_EMAIL_ACK;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {

  public static final String EMAIL_SENT_SUCCESS_FOR_SAGA_ID = "email sent success for saga id :: {}";
  private final Executor asyncExecutor = Executors.newWorkStealingPool(10);
  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing.";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
      " just updating the db status so that it will be polled and sent back again.";
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


  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestComplete(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    UMPRequestCompleteEmailEntity umpRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(UMPRequestCompleteEmailEntity.class, event.getEventPayload());
    asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        getUmpEmailService().sendCompletedRequestEmail(umpRequestCompleteEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
      }
    });

    return emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestReturn(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    UMPAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(UMPAdditionalInfoEmailEntity.class, event.getEventPayload());
    asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        getUmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestReject(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    UMPRequestRejectedEmailEntity rejectedEmail = JsonUtil.getJsonObjectFromString(UMPRequestRejectedEmailEntity.class, event.getEventPayload());
    asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        getUmpEmailService().sendRejectedRequestEmail(rejectedEmail);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestComplete(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    GMPRequestCompleteEmailEntity penRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestCompleteEmailEntity.class, event.getEventPayload());
    asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        getGmpEmailService().sendCompletedPENRequestEmail(penRequestCompleteEmailEntity, penRequestCompleteEmailEntity.getDemographicsChanged());
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestReturn(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    GMPRequestAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestAdditionalInfoEmailEntity.class, event.getEventPayload());
    asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        getGmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestReject(Event event) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    GMPRequestRejectedEmailEntity gmpRequestRejectedEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestRejectedEmailEntity.class, event.getEventPayload());
    asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        getGmpEmailService().sendRejectedPENRequestEmail(gmpRequestRejectedEmailEntity);
        getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return emailAPIEventProcessed(emailEvent);
  }

  private byte[] emailAPIEventProcessed(EmailEventEntity emailEvent) throws JsonProcessingException {
    Event event = Event.builder()
        .sagaId(emailEvent.getSagaId())
        .eventType(EventType.valueOf(emailEvent.getEventType()))
        .eventOutcome(EventOutcome.valueOf(emailEvent.getEventOutcome()))
        .eventPayload(emailEvent.getEventPayload()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }

}
