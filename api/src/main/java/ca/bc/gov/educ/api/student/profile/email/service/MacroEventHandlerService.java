package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.struct.v1.macro.MacroEditNotificationEntity;
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
public class MacroEventHandlerService extends BaseEventHandlerService {
  @Getter(PRIVATE)
  private final EmailEventService emailEventService;

  @Getter(PRIVATE)
  private final MacroEmailService macroEmailService;

  @Autowired
  public MacroEventHandlerService(final EmailEventService emailEventService, final MacroEmailService macroEmailService) {
    this.emailEventService = emailEventService;
    this.macroEmailService = macroEmailService;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyMacroCreate(final Event event) throws JsonProcessingException {
    return this.handleNotifyMacroEdit(event, true);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleNotifyMacroUpdate(final Event event) throws JsonProcessingException {
    return this.handleNotifyMacroEdit(event, false);
  }

  private byte[] handleNotifyMacroEdit(final Event event, final Boolean newMacro) throws JsonProcessingException {
    final EmailEventEntity emailEvent = this.getEmailEventService().createOrUpdateEventInDB(event, EventOutcome.NOTIFIED); // make sure the db operation is successful before sending the email.
    final MacroEditNotificationEntity macroEditNotificationEntity = JsonUtil.getJsonObjectFromString(MacroEditNotificationEntity.class, event.getEventPayload());
    if (StringUtils.equals(PENDING_EMAIL_ACK.getCode(), emailEvent.getEventStatus())) {
      this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PROCESSING.getCode());// mark it processing so that scheduler does not pick it up again until it has failed.
      this.asyncExecutor.execute(() -> {
        try {
          this.getMacroEmailService().notifyMacroEdit(macroEditNotificationEntity, newMacro);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
          log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
        } catch (final Exception exception) { // put it back to pending, so that it will be picked up by the scheduler again.
          log.warn("Exception for :: {}", event, exception);
          this.getEmailEventService().updateEventStatus(emailEvent.getEventId(), PENDING_EMAIL_ACK.getCode());
        }
      });
    }

    return this.emailAPIEventProcessed(emailEvent);
  }


}
