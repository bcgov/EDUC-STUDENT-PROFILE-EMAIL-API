package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.props.ApplicationProperties;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.*;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.ArchivePenRequestBatchNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.PenRequestBatchSchoolErrorNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.MESSAGE_PUBLISHED;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.PENDING_EMAIL_ACK;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EventHandlerDelegatorServiceTest {

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
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();
  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestCompleteChesCallFailed_shouldUpdateStatusToPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createCompletedEmailEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestComplete_shouldSendCompleteEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();

    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestCompleteAndChesCallFailed_shouldUpdateStatusToPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_COMPLETE.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());

  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestReject_shouldSendRejectEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestRejectAndChesCallFailed_shouldUpdateStatusToPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReject_shouldSendRejectEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestRejectGivenChesCallFailed_shouldUpdateStatusToPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_REJECT.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());
  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestReturn_shouldSendReturnEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestReturnAndChesCallFailed_shouldUpdateStatusToPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PEN_REQUEST_RETURN.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReturn_shouldSendReturnEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN.toString());
    assertThat(record).isPresent();

  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReturnAndChesCallFailed_shouldUpdateStatusToPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());
  }

  @Test
  public void handleEvent_givenNotifyStudentProfileRequestReturn_shouldSendReturnEmail2() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createAdditionalInfoUMPEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_STUDENT_PROFILE_REQUEST_RETURN.toString());
    assertThat(record).isPresent();
  }

  @Test
  public void handleEvent_givenNotifyPenRequestBatchArchiveHasContact_shouldSendArchiveHasContactEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_CONTACT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createArchivePenRequestBatchNotificationEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_CONTACT.toString());
    assertThat(record).isPresent();
  }

  @Test
  public void handleEvent_givenNotifyPenRequestBatchArchiveHasNoSchoolContact_shouldSendArchiveHasNoSchoolContactEmail() throws JsonProcessingException {
    final var sagaId = UUID.randomUUID();
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createArchivePenRequestBatchNotificationEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT.toString());
    assertThat(record).isPresent();
  }

  @Test
  public void handleEvent_givenNotifyPenRequestBatchArchiveHasNoSchoolContactAndChesThrowsException_shouldSetStatusPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createArchivePenRequestBatchNotificationEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.NOTIFY_PEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());
  }

  @Test
  public void handleEvent_givenNotifyPenRequestBatchFileFormatErrorChesThrowsException_shouldSetStatusPendingAck() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doThrow(WebClientResponseException.class).when(this.restUtils).sendEmail(any(), any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createErrorNotificationEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(PENDING_EMAIL_ACK.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(PENDING_EMAIL_ACK.getCode());
  }

  @Test
  public void handleEvent_givenNotifyPenRequestBatchFileFormatErrorChesReturnsSuccess_shouldSetStatusMessagePublished() throws JsonProcessingException, InterruptedException {
    final var sagaId = UUID.randomUUID();
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    this.eventHandlerService.handleEvent(Event.builder().eventType(EventType.PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR)
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createErrorNotificationEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build(), null);
    this.waitForAsyncToFinish(MESSAGE_PUBLISHED.getCode());
    final var record = this.repository.findBySagaIdAndEventType(sagaId, EventType.PEN_REQUEST_BATCH_NOTIFY_SCHOOL_FILE_FORMAT_ERROR.toString());
    assertThat(record).isPresent();
    assertThat(record.get().getEventStatus()).isEqualTo(MESSAGE_PUBLISHED.getCode());
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

  ArchivePenRequestBatchNotificationEntity createArchivePenRequestBatchNotificationEntity() {
    final var entity = new ArchivePenRequestBatchNotificationEntity();
    entity.setSubmissionNumber("000001");
    entity.setToEmail("test@email.co");
    entity.setFromEmail("test@email.co");
    entity.setMincode("123");
    entity.setSchoolName("Columneetza Secondary");
    return entity;
  }

  PenRequestBatchSchoolErrorNotificationEntity createErrorNotificationEntity() {
    final PenRequestBatchSchoolErrorNotificationEntity entity = new PenRequestBatchSchoolErrorNotificationEntity();
    entity.setFromEmail("test@test.ca");
    entity.setSubmissionNumber("test");
    entity.setToEmail("test@test.ca");
    entity.setDateTime(LocalDateTime.now().toString());
    entity.setFailReason("test");
    entity.setFileName("test");
    return entity;
  }

  private void waitForAsyncToFinish(final String status) throws InterruptedException {
    int i = 0;
    while (true) {
      if (i >= 100) {
        break; // break out after trying for 5 seconds.
      }
      val emailEventEntityList = this.repository.findByEventStatus(status);
      if (!emailEventEntityList.isEmpty()) {
        break;
      }
      TimeUnit.MILLISECONDS.sleep(50);
      i++;
    }
  }
}
