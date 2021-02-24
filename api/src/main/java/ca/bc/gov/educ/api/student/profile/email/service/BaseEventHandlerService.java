package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jboss.threads.EnhancedQueueExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;

public abstract class BaseEventHandlerService {
  protected static final String EMAIL_SENT_SUCCESS_FOR_SAGA_ID = "email sent success for saga id :: {}";
  protected final Executor asyncExecutor = new EnhancedQueueExecutor.Builder().setThreadFactory(new ThreadFactoryBuilder().setNameFormat("async-event-handler-%d").build())
      .setCorePoolSize(2)
      .setMaximumPoolSize(4)
      .setKeepAliveTime(Duration.ofSeconds(60))
      .build();
  protected static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing.";
  protected static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
      " just updating the db status so that it will be polled and sent back again.";
  protected static final String EVENT_PAYLOAD = "event is :: {}";

  protected byte[] emailAPIEventProcessed(final EmailEventEntity emailEvent) throws JsonProcessingException {
    final Event event = Event.builder()
        .sagaId(emailEvent.getSagaId())
        .eventType(EventType.valueOf(emailEvent.getEventType()))
        .eventOutcome(EventOutcome.valueOf(emailEvent.getEventOutcome()))
        .eventPayload(emailEvent.getEventPayload()).build();
    return JsonUtil.getJsonStringFromObject(event).getBytes();
  }
}
