package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.ArchivePenRequestBatchNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.PenRequestBatchSchoolErrorNotificationEntity;
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
public class PenRequestBatchEventHandlerService extends BaseEventHandlerService {
  @Getter(PRIVATE)
  private final EmailEventService emailEventService;

  @Getter(PRIVATE)
  private final PenRequestBatchEmailService prbEmailService;

  @Autowired
  public PenRequestBatchEventHandlerService(final EmailEventService emailEventService, final PenRequestBatchEmailService prbEmailService) {
    this.emailEventService = emailEventService;
    this.prbEmailService = prbEmailService;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handlePenRequestBatchNotifySchoolFileFormatIncorrect(final Event event) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event); // make sure the db operation is successful before sending the email.
    final PenRequestBatchSchoolErrorNotificationEntity errorNotificationEntity = JsonUtil.getJsonObjectFromString(PenRequestBatchSchoolErrorNotificationEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        this.getPrbEmailService().notifySchoolFileFormatIncorrect(errorNotificationEntity);
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
      }
    });

    return this.emailAPIEventProcessed(emailEvent);
  }

  public byte[] handleNotifyPenRequestBatchArchiveHasSchoolContact(Event event, boolean hasSchoolContact) throws JsonProcessingException {
    EmailEventEntity emailEvent = getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    ArchivePenRequestBatchNotificationEntity archivePenRequestBatchNotificationEntity = JsonUtil.getJsonObjectFromString(ArchivePenRequestBatchNotificationEntity.class, event.getEventPayload());
    this.asyncExecutor.execute(() -> {
      if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
        if(hasSchoolContact) {
          this.getPrbEmailService().sendArchivePenRequestBatchHasSchoolContactEmail(archivePenRequestBatchNotificationEntity);
        } else {
          this.getPrbEmailService().sendArchivePenRequestBatchHasNoSchoolContactEmail(archivePenRequestBatchNotificationEntity);
        }
        this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
        log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
      }
    });
    return this.emailAPIEventProcessed(emailEvent);
  }

  
}
