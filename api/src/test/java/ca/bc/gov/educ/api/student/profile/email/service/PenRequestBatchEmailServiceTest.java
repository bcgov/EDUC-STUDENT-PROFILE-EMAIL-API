package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.PenRequestBatchProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.ArchivePenRequestBatchNotificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.PendingRecords;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class PenRequestBatchEmailServiceTest {

  @Autowired
  PenRequestBatchEmailService prbEmailService;

  @Autowired
  CHESEmailService chesEmailService;

  @Autowired
  RestUtils restUtils;

  @Autowired
  PenRequestBatchProperties properties;

  @Before
  public void setUp() {
    openMocks(this);
  }

  @Test
  public void sendArchivePenRequestBatchHasSchoolContactEmail_givenArchivePenRequestBatchEmailEntity_shouldSendCorrectEmail() {
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    this.prbEmailService.sendArchivePenRequestBatchHasSchoolContactEmail(this.createArchivePenRequestBatchNotificationEntity());
    verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co", this.getArchivePenRequestBatchHasSchoolContactBody(), this.getArchivePenRequestBatchHasSchoolContactSubject());
  }

  @Test
  public void sendArchivePenRequestBatchHasSchoolContactEmail_givenArchivePenRequestBatchEmailEntityNoPending_shouldSendCorrectEmail() {
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    val entity = this.createArchivePenRequestBatchNotificationEntity();
    entity.setPendingRecords(PendingRecords.NONE);
    this.prbEmailService.sendArchivePenRequestBatchHasSchoolContactEmail(entity);
    verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co", this.getArchivePenRequestBatchHasSchoolContactBody(), this.getArchivePenRequestBatchHasSchoolContactSubject());
  }

  @Test
  public void sendArchivePenRequestBatchHasSchoolContactEmail_givenArchivePenRequestBatchEmailEntityWithPendingSome_shouldSendCorrectEmail() {
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    val entity = this.createArchivePenRequestBatchNotificationEntity();
    entity.setPendingRecords(PendingRecords.SOME);
    this.prbEmailService.sendArchivePenRequestBatchHasSchoolContactEmail(entity);
    verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co", this.emailBodyPendingSome(), this.getArchivePenRequestBatchHasSchoolContactSubject());
  }

  @Test
  public void sendArchivePenRequestBatchHasSchoolContactEmail_givenArchivePenRequestBatchEmailEntityWithPendingAll_shouldSendCorrectEmail() {
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    val entity = this.createArchivePenRequestBatchNotificationEntity();
    entity.setPendingRecords(PendingRecords.ALL);
    this.prbEmailService.sendArchivePenRequestBatchHasSchoolContactEmail(entity);
    verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co", this.emailBodyPendingAll(), this.getArchivePenRequestBatchHasSchoolContactSubject());
  }

  private String emailBodyPendingAll() {
    return "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"></head><body>Your PEN WEB Request, submission 000001, has not been processed due to errors. Please go to <a href=test url>test url</a> and logging into the PEN Web System to review the PEN Request errors; the record data will need to be updated in the school's Student Information System (MyEducation typically) and resubmitted in a new batch file to the Ministry, in order to receive the requested PENs.  If you do not understand the cause of error message, or if you are unsure how to correct the data, please contact the PENS.Coordinator@gov.bc.ca email.</html>";
  }


  @Test
  public void sendArchivePenRequestBatchHasNoSchoolContactEmail_givenArchivePenRequestBatchEmailEntity_shouldSendCorrectEmail() {
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    this.prbEmailService.sendArchivePenRequestBatchHasNoSchoolContactEmail(this.createArchivePenRequestBatchNotificationEntity());
    verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co", this.getArchivePenRequestBatchHasNoSchoolContactBody(), this.getArchivePenRequestBatchHasNoSchoolContactSubject());
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

  String getArchivePenRequestBatchHasSchoolContactBody() {
    return "test 000001 test url";
  }

  String getArchivePenRequestBatchHasSchoolContactSubject() {
    return "has school subject 000001 123 Columneetza Secondary";
  }

  String emailBodyPendingSome() {
    return "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"></head><body>Your PEN WEB Request, submission 000001, has been processed and the PEN Activity Report is available for download by going to <a href=test url>test url</a> and logging into the PEN Web System. <br> Please note that one or more pending PEN Requests require errors to be fixed by updating the data in the school's Student Information System (MyEducation typically) and resubmitting the request in a new Batch file. Alternatively, you may provide the requested additional information to the Ministry PEN Coordinator to have the file processed again.</html>";
  }

  String getArchivePenRequestBatchHasNoSchoolContactBody() {
    return "test 123 000001 test url";
  }

  String getArchivePenRequestBatchHasNoSchoolContactSubject() {
    return "has no school subject 000001 123";
  }
}
