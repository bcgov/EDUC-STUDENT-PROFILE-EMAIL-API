package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.*;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.MESSAGE_PUBLISHED;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.PENDING_EMAIL_ACK;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService extends BaseEventHandlerService {


  @Getter(PRIVATE)
  private final EmailEventService emailEventService;

  @Getter(PRIVATE)
  private final UMPEmailService umpEmailService;

  @Getter(PRIVATE)
  private final GMPEmailService gmpEmailService;

  @Autowired
  public EventHandlerService(final UMPEmailService umpEmailService, final EmailEventService emailEventService, final GMPEmailService gmpEmailService) {
    this.umpEmailService = umpEmailService;
    this.emailEventService = emailEventService;
    this.gmpEmailService = gmpEmailService;
  }


  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestComplete(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    final UMPRequestCompleteEmailEntity umpRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(UMPRequestCompleteEmailEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        this.getUmpEmailService().sendCompletedRequestEmail(umpRequestCompleteEmailEntity);
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
      }
    });

    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestReturn(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    final UMPAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(UMPAdditionalInfoEmailEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        this.getUmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentProfileRequestReject(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    final UMPRequestRejectedEmailEntity rejectedEmail = JsonUtil.getJsonObjectFromString(UMPRequestRejectedEmailEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        this.getUmpEmailService().sendRejectedRequestEmail(rejectedEmail);
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestComplete(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    final GMPRequestCompleteEmailEntity penRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestCompleteEmailEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        this.getGmpEmailService().sendCompletedPENRequestEmail(penRequestCompleteEmailEntity, penRequestCompleteEmailEntity.getDemographicsChanged());
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestReturn(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    final GMPRequestAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestAdditionalInfoEmailEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        this.getGmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return this.emailAPIEventProcessed(emailEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyStudentPenRequestReject(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    final GMPRequestRejectedEmailEntity gmpRequestRejectedEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestRejectedEmailEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        this.getGmpEmailService().sendRejectedPENRequestEmail(gmpRequestRejectedEmailEntity);
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return this.emailAPIEventProcessed(emailEvent);
  }

}
