package ca.bc.gov.educ.api.student.profile.email.schedulers;

import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.messaging.MessagePublisher;
import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.student.profile.email.constants.Topics.PROFILE_REQUEST_EMAIL_API_TOPIC;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EventTaskSchedulerTest {

  @Autowired
  ApplicationProperties properties;

  @Autowired
  EventTaskScheduler scheduler;

  @Autowired
  MessagePublisher messagePublisher;

  @MockBean
  StudentProfileRequestEmailEventRepository repository;

  @Autowired
  RestUtils restUtils;

  @Autowired
  RestTemplate restTemplate;

  @Before
  public void setUp() {
    initMocks(this);
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
  }

  @After
  public void tearDown() {
  }

  @Test
  public void pollEventTableAndPublish_givenNoRecordsInDB_shouldNotSendAnyMessage() throws InterruptedException, IOException, TimeoutException {
    scheduler.pollEventTableAndPublish();
    verify(messagePublisher, never()).dispatchMessage(eq(PROFILE_REQUEST_EMAIL_API_TOPIC.toString()), any());
  }

  @Test
  public void pollEventTableAndPublish_givenNoRecordsInDB_shouldNotSendAnyMessage2() throws InterruptedException, IOException, TimeoutException {
    scheduler.findAndUpdateUnprocessedRecords();
    verify(messagePublisher, never()).dispatchMessage(eq(PROFILE_REQUEST_EMAIL_API_TOPIC.toString()), any());
  }

  @Test
  public void pollEventTableAndPublish_givenARecordsInDB_shouldSendTwoMessage() throws InterruptedException, IOException, TimeoutException {
    when(repository.findByEventStatus(DB_COMMITTED.toString())).thenReturn(createEventList());
    scheduler.findAndUpdateUnprocessedRecords();
    verify(messagePublisher, atLeast(2)).dispatchMessage(any(), any());
  }

  private List<EmailEventEntity> createEventList() {
    EmailEventEntity emailEventEntity = EmailEventEntity.builder()
        .eventId(UUID.randomUUID())
        .eventOutcome(EventOutcome.STUDENT_NOTIFIED.toString())
        .eventPayload("payload")
        .replyChannel("localhost")
        .eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE.toString())
        .sagaId(UUID.randomUUID())
        .eventStatus(DB_COMMITTED.toString())
        .build();
    var emailEvents = new ArrayList<EmailEventEntity>();
    emailEvents.add(emailEventEntity);
    return emailEvents;
  }
}
