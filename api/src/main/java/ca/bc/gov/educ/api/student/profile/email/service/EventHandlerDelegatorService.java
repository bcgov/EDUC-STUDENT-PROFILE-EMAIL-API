package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import io.nats.client.Message;
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

  @Getter(PRIVATE)
  private final MacroEventHandlerService macroEventHandlerService;

  @Autowired
  public EventHandlerDelegatorService(final MessagePublisher messagePublisher, final EventHandlerService eventHandlerService,
                                      final PenRequestBatchEventHandlerService prbEventHandlerService,
                                      final MacroEventHandlerService macroEventHandlerService) {
    this.messagePublisher = messagePublisher;
    this.eventHandlerService = eventHandlerService;
    this.prbEventHandlerService = prbEventHandlerService;
    this.macroEventHandlerService = macroEventHandlerService;
  }

  public void handleEvent(final Event event, final Message message) {
    final byte[] response;
    try {
      switch (event.getEventType()) {
        case NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentProfileRequestComplete(event);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_RETURN event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentProfileRequestReturn(event);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_STUDENT_PROFILE_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PROFILE_REQUEST_REJECT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentProfileRequestReject(event);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_COMPLETE:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_COMPLETE event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentPenRequestComplete(event);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_RETURN:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_RETURN event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentPenRequestReturn(event);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_STUDENT_PEN_REQUEST_REJECT:
          log.info("received NOTIFY_STUDENT_PEN_REQUEST_REJECT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getEventHandlerService().handleNotifyStudentPenRequestReject(event);
          this.publishToNATS(event, message, response);
          break;
        case PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR:
          log.info("received PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR event :: {}", event);
          response = this.getPrbEventHandlerService().handlePenRequestBatchNotifySchoolFileFormatIncorrect(event);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_CONTACT:
          log.info("received NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_CONTACT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getPrbEventHandlerService().handleNotifyPenRequestBatchArchive(event, true);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT:
          log.info("received NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT event :: {}", event.getSagaId());
          log.trace(PAYLOAD_LOG, event.getEventPayload());
          response = this.getPrbEventHandlerService().handleNotifyPenRequestBatchArchive(event, false);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_MACRO_CREATE:
          log.info("received NOTIFY_MACRO_CREATE event :: {}", event);
          response = this.getMacroEventHandlerService().handleNotifyMacroCreate(event);
          this.publishToNATS(event, message, response);
          break;
        case NOTIFY_MACRO_UPDATE:
          log.info("received NOTIFY_MACRO_UPDATE event :: {}", event);
          response = this.getMacroEventHandlerService().handleNotifyMacroUpdate(event);
          this.publishToNATS(event, message, response);
          break;
        case SEND_EMAIL:
          log.info("received SEND_EMAIL event :: {}", event);
          response = this.getEventHandlerService().handleSendEmail(event);
          this.publishToNATS(event, message, response);
          break;
        default:
          log.info("silently ignoring other events :: {}", event);
          break;
      }
    } catch (final Exception e) {
      log.error("Exception occurred :: ", e);
    }
  }

  private void publishToNATS(final Event event, final Message message, final byte[] response) {
    if (message != null) {
      final String topic = message.getReplyTo() != null ? message.getReplyTo() : event.getReplyTo();
      if (topic != null) {
        log.info(RESPONSE_LOG, topic);
        this.getMessagePublisher().dispatchMessage(topic, response);
      }
    }
  }


}
