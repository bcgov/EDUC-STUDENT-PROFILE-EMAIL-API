package ca.bc.gov.educ.api.student.profile.email.messaging;

import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import io.nats.streaming.AckHandler;
import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@SuppressWarnings("java:S2142")
public class MessagePublisher extends MessagePubSub {

  @Autowired
  public MessagePublisher(final ApplicationProperties applicationProperties) throws IOException, InterruptedException {
    Options options = new Options.Builder()
            .natsUrl(applicationProperties.getNatsUrl())
            .clusterId(applicationProperties.getNatsClusterId())
            .connectionLostHandler(this::connectionLostHandler)
            .clientId("student-profile-email-api-publisher" + UUID.randomUUID().toString()).build();
    connectionFactory = new StreamingConnectionFactory(options);
    connection = connectionFactory.createConnection();
  }

  public void dispatchMessage(String subject, byte[] message) throws InterruptedException, TimeoutException, IOException {
    AckHandler ackHandler = getAckHandler();
    connection.publish(subject, message, ackHandler);
  }

  private AckHandler getAckHandler() {
    return new AckHandler() {
      @Override
      public void onAck(String guid, Exception err) {
        log.trace("already handled.");
      }

      @Override
      public void onAck(String guid, String subject, byte[] data, Exception ex) {

        if (ex != null) {
          executorService.execute(() -> {
            try {
              retryPublish(subject, data);
            } catch (InterruptedException | TimeoutException | IOException e) {
              log.error("Exception", e);
            }
          });

        } else {
          log.trace("acknowledgement received {}", guid);
        }
      }
    };
  }

  public void retryPublish(String subject, byte[] message) throws InterruptedException, TimeoutException, IOException {
    log.info("retrying publish...");
    connection.publish(subject, message, getAckHandler());
  }

}
