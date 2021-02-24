package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.*;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EventHandlerServiceTest {

  @Autowired
  EventHandlerDelegatorService eventHandlerService;

  @Autowired
  StudentProfileRequestEmailEventRepository repository;
  @Autowired
  RestUtils restUtils;


  @Autowired
  ApplicationProperties properties;

  @Before
  public void setUp() {
    openMocks(this);
  }

  @After
  public void tearDown() {
    this.repository.deleteAll();
  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestComplete_shouldSendCompleteEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();

    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createCompletedEmailEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestComplete_shouldSendCompleteEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();

    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestReject_shouldSendRejectEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReject_shouldSendRejectEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestReturn_shouldSendReturnEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReturn_shouldSendReturnEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReturn_shouldSendReturnEmail2() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN.toString());
    assertThat(record).isPresent();
  }

  GMPRequestCompleteEmailEntity createCompletedEmailEntity() {
    final var entity = new GMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    entity.setDemographicsChanged(false);
    return entity;
  }

  GMPRequestRejectedEmailEntity createRejectedEntity() {
    final var entity = new GMPRequestRejectedEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    entity.setRejectionReason("rejected");
    return entity;
  }

  GMPRequestAdditionalInfoEmailEntity createAdditionalInfoEntity() {
    final var entity = new GMPRequestAdditionalInfoEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }


  UMPRequestCompleteEmailEntity createUMPEntity() {
    final var entity = new UMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  UMPRequestRejectedEmailEntity createRejectedUMPEntity() {
    final var entity = new UMPRequestRejectedEmailEntity();
    entity.setRejectionReason("rejected");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }


  UMPAdditionalInfoEmailEntity createAdditionalInfoUMPEntity() {
    final var entity = new UMPAdditionalInfoEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }
}
