package ca.bc.gov.educ.api.student.profile.email.messaging;

import ca.bc.gov.educ.api.student.profile.email.model.Event;
import ca.bc.gov.educ.api.student.profile.email.service.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ca.bc.gov.educ.api.student.profile.email.constants.Topics.PROFILE_REQUEST_EMAIL_API_TOPIC;


@Component
@Slf4j
public class MessageSubscriber {

  private final EventHandlerDelegatorService eventHandlerDelegatorService;
  private final Executor executor = Executors.newFixedThreadPool(6);
  private final Connection connection;

  @Autowired
  public MessageSubscriber(final Connection con, EventHandlerDelegatorService eventHandlerDelegatorService) {
    this.eventHandlerDelegatorService = eventHandlerDelegatorService;
    this.connection = con;
  }

  /**
   * This subscription will makes sure the messages are required to acknowledge manually to STAN.
   * Subscribe.
   */
  @PostConstruct
  public void subscribe() {
    String queue = PROFILE_REQUEST_EMAIL_API_TOPIC.toString().replace("_", "-");
    var dispatcher = connection.createDispatcher(onMessage());
    dispatcher.subscribe(PROFILE_REQUEST_EMAIL_API_TOPIC.toString(), queue);
  }

  /**
   * On message message handler.
   *
   * @return the message handler
   */
  private MessageHandler onMessage() {
    return (Message message) -> {
      if (message != null) {
        try {
          var eventString = new String(message.getData());
          var event = JsonUtil.getJsonObjectFromString(Event.class, eventString);
          executor.execute(() -> eventHandlerDelegatorService.handleEvent(event));
        } catch (final Exception e) {
          log.error("Exception ", e);
        }
      }
    };
  }


}
