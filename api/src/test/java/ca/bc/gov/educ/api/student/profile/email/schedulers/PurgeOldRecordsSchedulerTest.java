package ca.bc.gov.educ.api.student.profile.email.schedulers;

import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PurgeOldRecordsSchedulerTest {

  @Autowired
  StudentProfileRequestEmailEventRepository eventRepository;

  @Autowired
  PurgeOldRecordsScheduler purgeOldRecordsScheduler;

  @After
  public void after() {
    this.eventRepository.deleteAll();
  }

  @Test
  public void testPurgeOldRecords_givenOldRecordsPresent_shouldBeDeleted() {
    final var payload = " {\n" +
        "    \"createUser\": \"test\",\n" +
        "    \"updateUser\": \"test\",\n" +
        "    \"legalFirstName\": \"Jack\"\n" +
        "  }";

    final var yesterday = LocalDateTime.now().minusDays(1);

    this.eventRepository.save(this.getEvent(payload, LocalDateTime.now()));

    this.eventRepository.save(this.getEvent(payload, yesterday));

    this.purgeOldRecordsScheduler.setEventRecordStaleInDays(1);
    this.purgeOldRecordsScheduler.purgeOldRecords();

    final var servicesEvents = this.eventRepository.findAll();
    assertThat(servicesEvents).hasSize(1);
  }


  private EmailEventEntity getEvent(final String payload, final LocalDateTime createDateTime) {
    return EmailEventEntity
      .builder()
      .eventPayloadBytes(payload.getBytes())
      .eventStatus("MESSAGE_PUBLISHED")
      .eventType("UPDATE_SLD_STUDENTS")
      .sagaId(UUID.randomUUID())
      .eventOutcome("SLD_STUDENT_UPDATED")
      .replyChannel("TEST_CHANNEL")
      .createDate(createDateTime)
      .createUser("SLD_API")
      .updateUser("SLD_API")
      .updateDate(createDateTime)
      .build();
  }
}
