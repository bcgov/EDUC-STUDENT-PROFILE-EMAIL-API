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

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.*;
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
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getPrbEmailService().notifySchoolFileFormatIncorrect(errorNotificationEntity);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.getCode());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }

  public byte[] handleNotifyPenRequestBatchArchive(final Event event, final boolean hasSchoolContact) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event);// make sure the db operation is successful before sending the email.
    final ArchivePenRequestBatchNotificationEntity archivePenRequestBatchNotificationEntity = JsonUtil.getJsonObjectFromString(ArchivePenRequestBatchNotificationEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode()); // mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          if (hasSchoolContact) {
            this.getPrbEmailService().sendArchivePenRequestBatchHasSchoolContactEmail(archivePenRequestBatchNotificationEntity);
          } else {
            this.getPrbEmailService().sendArchivePenRequestBatchHasNoSchoolContactEmail(archivePenRequestBatchNotificationEntity);
          }
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, event.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }
    return this.emailAPIEventProcessed(emailEvent);
  }


}
