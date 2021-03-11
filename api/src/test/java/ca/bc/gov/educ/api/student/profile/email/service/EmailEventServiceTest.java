package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.GMPRequestRejectedEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.email.constants.EventStatus.PENDING_EMAIL_ACK;
import static ca.bc.gov.educ.api.student.profile.email.constants.EventType.NOTIFY_STUDENT_PEN_REQUEST_REJECT;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EmailEventServiceTest {

  @Autowired
  EmailEventService emailEventService;

  @Autowired
  StudentProfileRequestEmailEventRepository repository;

  @Test
  public void testGetPendingEmailEvents_givenNoData_shouldReturnEmptyList() {
    final var data = this.emailEventService.getPendingEmailEvents(LocalDateTime.now().minusMinutes(5));
    assertThat(data).isEmpty();
  }

  @Test
  public void testUpdateEventStatus_givenNoData_shouldNotDoAnything() {
    final UUID eventID = UUID.randomUUID();
    this.emailEventService.updateEventStatus(eventID, PENDING_EMAIL_ACK.getCode());
    assertThat(this.repository.findAll()).isEmpty();
  }

  @Test
  public void testGetEventsStuckAtProcessing_givenNoData_shouldReturnEmptyList() {
    val result = this.emailEventService.getEventsStuckAtProcessing(LocalDateTime.now().minusHours(6));
    assertThat(result).isEmpty();
  }

  @Test
  public void testGetPendingEmailEvents_givenPendingDataMoreThan5Minutes_shouldReturnNonEmptyList() throws JsonProcessingException {
    final EmailEventEntity emailEventEntity = EmailEventEntity.builder()
        .eventType(NOTIFY_STUDENT_PEN_REQUEST_REJECT.toString())
        .eventStatus(PENDING_EMAIL_ACK.getCode())
        .createDate(LocalDateTime.now().minusMinutes(6))
        .createUser("test")
        .updateDate(LocalDateTime.now().minusMinutes(6))
        .updateUser("test")
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedEntity()))
        .eventOutcome("STUDENT_NOTIFIED")
        .build();
    this.repository.save(emailEventEntity);
    final var data = this.emailEventService.getPendingEmailEvents(LocalDateTime.now().minusMinutes(2));
    assertThat(data).isNotEmpty();
  }

  @Test
  public void testGetPendingEmailEvents_givenNoPendingDataMoreThan5Minutes_shouldReturnNonEmptyList() throws JsonProcessingException {
    final EmailEventEntity emailEventEntity = EmailEventEntity.builder()
        .eventType(NOTIFY_STUDENT_PEN_REQUEST_REJECT.toString())
        .eventStatus(PENDING_EMAIL_ACK.getCode())
        .createDate(LocalDateTime.now().minusMinutes(1))
        .createUser("test")
        .updateDate(LocalDateTime.now().minusMinutes(1))
        .updateUser("test")
        .eventPayload(JsonUtil.getJsonStringFromObject(this.createRejectedEntity()))
        .eventOutcome("STUDENT_NOTIFIED")
        .build();
    this.repository.save(emailEventEntity);
    final var data = this.emailEventService.getPendingEmailEvents(LocalDateTime.now().minusMinutes(2));
    assertThat(data).isEmpty();
  }

  GMPRequestRejectedEmailEntity createRejectedEntity() {
    final var entity = new GMPRequestRejectedEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    entity.setRejectionReason("rejected");
    return entity;
  }
}
