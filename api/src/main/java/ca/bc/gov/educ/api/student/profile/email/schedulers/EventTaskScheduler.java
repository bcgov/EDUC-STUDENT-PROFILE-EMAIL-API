package ca.bc.gov.educ.api.student.profile.email.schedulers;

import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.service.EmailEventService;
import ca.bc.gov.educ.api.student.profile.email.service.GMPEmailService;
import ca.bc.gov.educ.api.student.profile.email.service.UMPEmailService;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
public class EventTaskScheduler {
  public static final String SEND_EMAIL_FOR_EVENT = "retrying to send email for event :: {}";
  public static final String EMAIL_SENT_SUCCESS_FOR_SAGA_ID = "email sent success for saga id :: {}";
  private final Executor taskExecutor = Executors.newSingleThreadExecutor();
  @Getter(PRIVATE)
  private final EmailEventService emailEventService;

  @Getter(PRIVATE)
  private final UMPEmailService umpEmailService;

  @Getter(PRIVATE)
  private final GMPEmailService gmpEmailService;

  public EventTaskScheduler(EmailEventService emailEventService, UMPEmailService umpEmailService, GMPEmailService gmpEmailService) {
    this.emailEventService = emailEventService;
    this.umpEmailService = umpEmailService;
    this.gmpEmailService = gmpEmailService;
  }

  @Scheduled(cron = "0 0/1 * * * *")
  @SchedulerLock(name = "PENDING_EMAIL_LOCK", lockAtLeastFor = "50s", lockAtMostFor = "58s")
  public void checkAndSendEmails() {
    LockAssert.assertLocked();
    LocalDateTime dateTimeToCompare = LocalDateTime.now().minusMinutes(2);
    var unsentEmailEvents = getEmailEventService().getPendingEmailEvents(dateTimeToCompare);
    if (!unsentEmailEvents.isEmpty()) {
      log.info("found :: {} events, for which email is still not sent, retrying...", unsentEmailEvents.size());
      taskExecutor.execute(() -> {
        for (var emailEvent : unsentEmailEvents) {
          try {
            EventType eventType = EventType.valueOf(emailEvent.getEventType());
            switch (eventType) {
              case NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE:
                log.info(SEND_EMAIL_FOR_EVENT, emailEvent);
                UMPRequestCompleteEmailEntity umpRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(UMPRequestCompleteEmailEntity.class, emailEvent.getEventPayload());
                getUmpEmailService().sendCompletedRequestEmail(umpRequestCompleteEmailEntity);
                getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
                log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
                break;
              case NOTIFY_STUDENT_PROFILE_REQUEST_RETURN:
                log.info(SEND_EMAIL_FOR_EVENT, emailEvent);
                UMPAdditionalInfoEmailEntity additionalInfoEmailEntity = JsonUtil.getJsonObjectFromString(UMPAdditionalInfoEmailEntity.class, emailEvent.getEventPayload());
                getUmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntity);
                getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
                log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
                break;
              case NOTIFY_STUDENT_PROFILE_REQUEST_REJECT:
                log.info(SEND_EMAIL_FOR_EVENT, emailEvent);
                UMPRequestRejectedEmailEntity rejectedEmail = JsonUtil.getJsonObjectFromString(UMPRequestRejectedEmailEntity.class, emailEvent.getEventPayload());
                getUmpEmailService().sendRejectedRequestEmail(rejectedEmail);
                getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
                log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
                break;
              case NOTIFY_STUDENT_PEN_REQUEST_COMPLETE:
                log.info(SEND_EMAIL_FOR_EVENT, emailEvent);
                GMPRequestCompleteEmailEntity penRequestCompleteEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestCompleteEmailEntity.class, emailEvent.getEventPayload());
                getGmpEmailService().sendCompletedPENRequestEmail(penRequestCompleteEmailEntity, penRequestCompleteEmailEntity.getDemographicsChanged());
                getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
                log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
                break;
              case NOTIFY_STUDENT_PEN_REQUEST_RETURN:
                log.info(SEND_EMAIL_FOR_EVENT, emailEvent);
                GMPRequestAdditionalInfoEmailEntity additionalInfoEmailEntityGMP = JsonUtil.getJsonObjectFromString(GMPRequestAdditionalInfoEmailEntity.class, emailEvent.getEventPayload());
                getGmpEmailService().sendAdditionalInfoEmail(additionalInfoEmailEntityGMP);
                getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
                log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
                break;
              case NOTIFY_STUDENT_PEN_REQUEST_REJECT:
                log.info(SEND_EMAIL_FOR_EVENT, emailEvent);
                GMPRequestRejectedEmailEntity gmpRequestRejectedEmailEntity = JsonUtil.getJsonObjectFromString(GMPRequestRejectedEmailEntity.class, emailEvent.getEventPayload());
                getGmpEmailService().sendRejectedPENRequestEmail(gmpRequestRejectedEmailEntity);
                getEmailEventService().updateEventStatus(emailEvent.getEventId(), MESSAGE_PUBLISHED.toString());
                log.info(EMAIL_SENT_SUCCESS_FOR_SAGA_ID, emailEvent.getSagaId());
                break;
              default:
                log.info("event type is not present :: {}", emailEvent);
                break;
            }
          } catch (final Exception ex) {
            log.error("exception occurred... ", ex);
          }
        }
      });
    }
  }
}
