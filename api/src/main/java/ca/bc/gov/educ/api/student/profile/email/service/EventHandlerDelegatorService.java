package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerDelegatorService {

  public static final String PAYLOAD_LOG = "payload is :: {}";
  @Getter(PRIVATE)
  private final MessagePublisher messagePublisher;

  @Getter(PRIVATE)
  private final EventHandlerService eventHandlerService;


  @Autowired
  public EventHandlerDelegatorService(MessagePublisher messagePublisher, EventHandlerService eventHandlerService){
    this.messagePublisher = messagePublisher;
    this.eventHandlerService = eventHandlerService;
  }

  public void handleEvent(final Event event) {
    byte[] response;
    try {
      switch (event.getEventType()) {
        case NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = getEventHandlerService().handleNotifyStudentProfileRequestComplete(event);
          getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_RETURN event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = getEventHandlerService().handleNotifyStudentProfileRequestReturn(event);
          getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_REJECT event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = getEventHandlerService().handleNotifyStudentProfileRequestReject(event);
          getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_COMPLETE event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = getEventHandlerService().handleNotifyStudentPenRequestComplete(event);
          getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_RETURN event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = getEventHandlerService().handleNotifyStudentPenRequestReturn(event);
          getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_REJECT event :: ");
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = getEventHandlerService().handleNotifyStudentPenRequestReject(event);
          getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        default:
          log.info("silently ignoring other events :: {}", event);
          break;
      }
    } catch (final Exception e) {
      log.error("Exception occurred :: ", e);
    }
  }


}
