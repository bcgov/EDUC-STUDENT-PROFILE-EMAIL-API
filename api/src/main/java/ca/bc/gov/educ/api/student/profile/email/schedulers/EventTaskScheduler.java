package ca.bc.gov.educ.api.student.profile.email.schedulers;


import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.email.model.Event;
import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventType.PROFILE_REQUEST_EMAIL_API_EVENT_OUTBOX_PROCESSED;
import static ca.bc.gov.educ.api.student.profile.email.constants.Topics.PROFILE_REQUEST_EMAIL_API_TOPIC;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
public class EventTaskScheduler {

  @Getter(PRIVATE)
  private final MessagePublisher messagePubSub;
  @Getter(PRIVATE)
  private final StudentProfileRequestEmailEventRepository emailEventRepository;

  @Autowired
  public EventTaskScheduler(MessagePublisher messagePubSub, StudentProfileRequestEmailEventRepository emailEventRepository) {
    this.messagePubSub = messagePubSub;
    this.emailEventRepository = emailEventRepository;
  }

  @Scheduled(cron = "0/1 * * * * *")
  @SchedulerLock(name = "EventTablePoller",
      lockAtLeastFor = "900ms", lockAtMostFor = "950ms")
  public void pollEventTableAndPublish() throws InterruptedException, IOException, TimeoutException {
    var events = getEmailEventRepository().findByEventStatus(DB_COMMITTED.toString());
    if (!events.isEmpty()) {
      for (var event : events) {
        try {
          if (event.getReplyChannel() != null) {
            getMessagePubSub().dispatchMessage(event.getReplyChannel(), penRequestEmailAPIEventProcessed(event));
          }
          getMessagePubSub().dispatchMessage(PROFILE_REQUEST_EMAIL_API_TOPIC.toString(), createOutboxEvent(event));
        } catch (InterruptedException | TimeoutException | IOException e) {
          log.error("exception occurred", e);
          throw e;
        }
      }
    } else {
      log.trace("no unprocessed records.");
    }
  }

  private byte[] penRequestEmailAPIEventProcessed(EmailEventEntity emailEvent) throws JsonProcessingException {
    Event event = Event.builder()
        .sagaId(emailEvent.getSagaId())
        .eventType(EventType.valueOf(emailEvent.getEventType()))
        .eventOutcome(EventOutcome.valueOf(emailEvent.getEventOutcome()))
        .eventPayload(emailEvent.getEventPayload()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }

  private byte[] createOutboxEvent(EmailEventEntity emailEvent) throws JsonProcessingException {
    Event event = Event.builder().eventType(PROFILE_REQUEST_EMAIL_API_EVENT_OUTBOX_PROCESSED).eventPayload(emailEvent.getEventId().toString()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }
}

