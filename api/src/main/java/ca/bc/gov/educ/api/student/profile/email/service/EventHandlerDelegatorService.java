package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerDelegatorService {

  public static final String PAYLOAD_LOG = "payload is :: {}";
  public static final String RESPONSE_LOG = "responding back to NATS on {} ";
  @Getter(PRIVATE)
  private final MessagePublisher messagePublisher;

  @Getter(PRIVATE)
  private final EventHandlerService eventHandlerService;

  @Getter(PRIVATE)
  private final PenRequestBatchEventHandlerService prbEventHandlerService;

  @Autowired
  public EventHandlerDelegatorService(final MessagePublisher messagePublisher, final EventHandlerService eventHandlerService, final PenRequestBatchEventHandlerService prbEventHandlerService) {
    this.messagePublisher = messagePublisher;
    this.eventHandlerService = eventHandlerService;
    this.prbEventHandlerService = prbEventHandlerService;
  }

  public void handleEvent(final Event event) {
    final byte[] response;
    try {
      switch (event.getEventType()) {
        case NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentProfileRequestComplete(event);
          log.info(RESPONSE_LOG, event.getReplyTo());
          this.getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_RETURN event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentProfileRequestReturn(event);
          log.info(RESPONSE_LOG, event.getReplyTo());
          this.getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_REJECT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentProfileRequestReject(event);
          log.info(RESPONSE_LOG, event.getReplyTo());
          this.getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_COMPLETE event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentPenRequestComplete(event);
          log.info(RESPONSE_LOG, event.getReplyTo());
          this.getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_RETURN event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentPenRequestReturn(event);
          log.info(RESPONSE_LOG, event.getReplyTo());
          this.getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_REJECT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentPenRequestReject(event);
          log.info(RESPONSE_LOG, event.getReplyTo());
          this.getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR:
          log.info("received PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getPrbEventHandlerService().handlePenRequestBatchNotifySchoolFileFormatIncorrect(event);
          log.info(RESPONSE_LOG, event.getReplyTo());
          this.getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_CONTACT:
          log.info("received NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_CONTACT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getPrbEventHandlerService().handleNotifyPenRequestBatchArchive(event, true);
          log.info(RESPONSE_LOG, event.getReplyTo());
          getMessagePublisher().dispatchMessage(event.getReplyTo(), response);
          break;
        case NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT:
          log.info("received NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getPrbEventHandlerService().handleNotifyPenRequestBatchArchive(event, false);
          log.info(RESPONSE_LOG, event.getReplyTo());
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
