package ca.bc.gov.educ.api.student.profile.email.schedulers;

import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.mappers.EmailEventMapper;
import ca.bc.gov.educ.api.student.profile.email.service.EmailEventService;
import ca.bc.gov.educ.api.student.profile.email.service.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.student.profile.email.service.GMPEmailService;
import ca.bc.gov.educ.api.student.profile.email.service.UMPEmailService;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.PenRequestBatchSchoolErrorNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executor;

import static ca.bc.gov.educ.api.student.profile.email.constants.Topics.PROFILE_REQUEST_EMAIL_API_TOPIC;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
public class EventTaskScheduler {
  private final Executor taskExecutor = new EnhancedQueueExecutor.Builder().setThreadFactory(new ThreadFactoryBuilder().setNameFormat("schedule-task-%d").build())
      .setCorePoolSize(1)
      .setMaximumPoolSize(2)
      .setKeepAliveTime(Duration.ofSeconds(60))
      .build();
  @Getter(PRIVATE)
  private final EmailEventService emailEventService;

  @Getter(PRIVATE)
  private final UMPEmailService umpEmailService;

  @Getter(PRIVATE)
  private final GMPEmailService gmpEmailService;

  private final EventHandlerDelegatorService eventHandlerDelegatorService;

  public EventTaskScheduler(final EmailEventService emailEventService, final UMPEmailService umpEmailService, final GMPEmailService gmpEmailService, final EventHandlerDelegatorService eventHandlerDelegatorService) {
    this.emailEventService = emailEventService;
    this.umpEmailService = umpEmailService;
    this.gmpEmailService = gmpEmailService;
    this.eventHandlerDelegatorService = eventHandlerDelegatorService;
  }

  @Scheduled(cron = "0 0/1 * * * *")
  @SchedulerLock(name = "PENDING_EMAIL_LOCK", lockAtLeastFor = "50s", lockAtMostFor = "58s")
  public void checkAndSendEmails() throws JsonProcessingException {
    LockAssert.assertLocked();
    final LocalDateTime dateTimeToCompare = LocalDateTime.now().minusMinutes(5);
    final var unsentEmailEvents = this.getEmailEventService().getPendingEmailEvents(dateTimeToCompare);
    if (!unsentEmailEvents.isEmpty()) {
      log.info("found :: {} events, for which email is still not sent, retrying...", unsentEmailEvents.size());
      this.taskExecutor.execute(() -> {
        for (final var emailEvent : unsentEmailEvents) {
          try {
            this.eventHandlerDelegatorService.handleEvent(EmailEventMapper.mapper.toEvent(emailEvent));
          } catch (final Exception ex) {
            log.error("exception occurred... ", ex);
          }
        }
      });
    }
  }

  @Scheduled(fixedRate = 60000)
  public void test() throws JsonProcessingException {
    final PenRequestBatchSchoolErrorNotificationEntity entity = new PenRequestBatchSchoolErrorNotificationEntity();
    entity.setDateTime("23 Feb");
    entity.setFailReason("blah blah");
    entity.setFromEmail("omprakashmishra3978@gmail.com");
    entity.setToEmail("omprkshmishra@gmail.com");
    entity.setSubmissionNumber("123456789");
    entity.setSubjectLine("PEN Request could not be processed for File: abcs with Submission Number : 123456789");
    final Event event = Event.builder()
        .sagaId(UUID.randomUUID())
        .eventType(EventType.PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR)
        .replyTo(PROFILE_REQUEST_EMAIL_API_TOPIC.toString())
        .eventPayload(JsonUtil.getJsonStringFromObject(entity))
        .build();
    this.taskExecutor.execute(() -> this.eventHandlerDelegatorService.handleEvent(event));
  }
}
