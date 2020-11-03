package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.model.*;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
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
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EventHandlerServiceTest {

  @Autowired
  EventHandlerService eventHandlerService;

  @Autowired
  StudentProfileRequestEmailEventRepository repository;
  @Autowired
  RestUtils restUtils;

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  ApplicationProperties properties;

  @Before
  public void setUp() {
    initMocks(this);
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
  }

  @After
  public void tearDown() {
    repository.deleteAll();
  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestComplete_shouldSendCompleteEmail() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();

    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(createCompletedEmailEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var record = repository.findBySagaIdAndEventType(sagaId,EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();
    verify(restTemplate, atLeastOnce()).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestComplete_shouldSendCompleteEmail() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();

    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(createUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var record = repository.findBySagaIdAndEventType(sagaId,EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();
    verify(restTemplate, atLeastOnce()).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestReject_shouldSendRejectEmail() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();
    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(createRejectedEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var record = repository.findBySagaIdAndEventType(sagaId,EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT.toString());
    assertThat(record).isPresent();
    verify(restTemplate, atLeastOnce()).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReject_shouldSendRejectEmail() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();
    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(createRejectedUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var record = repository.findBySagaIdAndEventType(sagaId,EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT.toString());
    assertThat(record).isPresent();
    verify(restTemplate, atLeastOnce()).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestReturn_shouldSendReturnEmail() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();
    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(createAdditionalInfoEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var record = repository.findBySagaIdAndEventType(sagaId,EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN.toString());
    assertThat(record).isPresent();
    verify(restTemplate, atLeastOnce()).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReturn_shouldSendReturnEmail() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();
    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var record = repository.findBySagaIdAndEventType(sagaId,EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN.toString());
    assertThat(record).isPresent();
    verify(restTemplate, atLeastOnce()).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReturn_shouldSendReturnEmail2() throws JsonProcessingException {
    var invocations = mockingDetails(restTemplate).getInvocations().size();
    var sagaId = UUID.randomUUID();
    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var record = repository.findBySagaIdAndEventType(sagaId,EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN.toString());
    assertThat(record).isPresent();
    verify(restTemplate, atMost(invocations+1)).postForObject(eq(properties.getChesEndpointURL()), any(), any());
  }

  GMPRequestCompleteEmailEntity createCompletedEmailEntity() {
    var entity = new GMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    entity.setDemographicsChanged(false);
    return entity;
  }

  GMPRequestRejectedEmailEntity createRejectedEntity() {
    var entity = new GMPRequestRejectedEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    entity.setRejectionReason("rejected");
    return entity;
  }

  GMPRequestAdditionalInfoEmailEntity createAdditionalInfoEntity() {
    var entity = new GMPRequestAdditionalInfoEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }


  UMPRequestCompleteEmailEntity createUMPEntity() {
    var entity = new UMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  UMPRequestRejectedEmailEntity createRejectedUMPEntity() {
    var entity = new UMPRequestRejectedEmailEntity();
    entity.setRejectionReason("rejected");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }


  UMPAdditionalInfoEmailEntity createAdditionalInfoUMPEntity() {
    var entity = new UMPAdditionalInfoEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }
}
