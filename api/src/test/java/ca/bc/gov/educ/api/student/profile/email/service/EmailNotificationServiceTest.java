package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.EmailNotificationEntity;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EmailNotificationServiceTest {

  @Autowired
  EmailNotificationService emailNotificationService;

  @Autowired
  RestUtils restUtils;

  @Captor
  ArgumentCaptor<String> emailBodyCaptor;

  @Before
  public void setUp() {
    openMocks(this);
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
  }

  @Test
  public void sendEmail_givenGMP_ADDITIONAL_INFO_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("additionalInfoRequested.gmp", Map.of("loginUrl", "https://test.co"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
  }

  @Test
  public void sendEmail_givenGMP_COMPLETED_REQUEST_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("completedRequest.gmp", Map.of("loginUrl", "https://test.co", "firstName", "Jerry"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("<span>Jerry</span>");
  }

  @Test
  public void sendEmail_givenGMP_COMPLETED_REQUEST_DEMOGRAPHIC_CHANGE_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("completedRequest.demographicChange.gmp", Map.of("loginUrl", "https://test.co", "firstName", "Jerry"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("<span>Jerry</span>");
  }

  @Test
  public void sendEmail_givenGMP_NOTIFY_STALE_RETURN_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("notify.stale.return.gmp", Map.of("loginUrl", "https://test.co"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
  }

  @Test
  public void sendEmail_givenGMP_REJECTED_REQUEST_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("rejectedRequest.gmp", Map.of("loginUrl", "https://test.co", "rejectionReason", "Cannot find the student record"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("Cannot find the student record");
  }

  @Test
  public void sendEmail_givenGMP_VERIFY_EMAIL_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("verifyEmail.gmp", Map.of("identityTypeLabel", "Basic BCeID","verificationUrl", "https://test.co/verify?verificationToken", "jwtToken", "12345ABCDE"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co/verify?verificationToken=12345ABCDE\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("Basic BCeID");
  }

  @Test
  public void sendEmail_givenUMP_ADDITIONAL_INFO_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("additionalInfoRequested.ump", Map.of("loginUrl", "https://test.co"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
  }

  @Test
  public void sendEmail_givenUMP_COMPLETED_REQUEST_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("completedRequest.ump", Map.of("loginUrl", "https://test.co", "firstName", "Jerry"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("<span>Jerry</span>");
  }

  @Test
  public void sendEmail_givenUMP_NOTIFY_STALE_RETURN_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("notify.stale.return.ump", Map.of("loginUrl", "https://test.co"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
  }

  @Test
  public void sendEmail_givenUMP_REJECTED_REQUEST_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("rejectedRequest.ump", Map.of("loginUrl", "https://test.co", "rejectionReason", "Cannot find the student record"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("Cannot find the student record");
  }

  @Test
  public void sendEmail_givenUMP_VERIFY_EMAIL_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("verifyEmail.ump", Map.of("identityTypeLabel", "Basic BCeID","verificationUrl", "https://test.co/verify?verificationToken", "jwtToken", "12345ABCDE"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co/verify?verificationToken=12345ABCDE\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("Basic BCeID");
  }

  @Test
  public void sendEmail_givenMACRO_CREATE_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("macro.create", Map.of("businessUseTypeName", "PEN Registry","macroCode", "TST", "macroTypeCode", "MERGE", "macroText", "Record Merged"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("PEN Registry");
    assertThat(this.emailBodyCaptor.getValue()).contains("TST");
    assertThat(this.emailBodyCaptor.getValue()).contains("MERGE");
    assertThat(this.emailBodyCaptor.getValue()).contains("Record Merged");
  }

  @Test
  public void sendEmail_givenMACRO_UPDATE_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("macro.update", Map.of("businessUseTypeName", "PEN Registry","macroCode", "TST", "macroTypeCode", "MERGE", "macroText", "Record Merged"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("PEN Registry");
    assertThat(this.emailBodyCaptor.getValue()).contains("TST");
    assertThat(this.emailBodyCaptor.getValue()).contains("MERGE");
    assertThat(this.emailBodyCaptor.getValue()).contains("Record Merged");
  }

  @Test
  public void sendEmail_givenPEN_REQUEST_BATCH_ARCHIVE_HAS_NO_SCHOOL_CONTACT_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("penRequestBatch.archive.hasNoSchoolContact", Map.of("penCoordinatorLoginUrl", "https://test.co", "mincode", "123456", "submissionNumber", "ABC1234"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("123456");
    assertThat(this.emailBodyCaptor.getValue()).contains("ABC1234");
  }

  @Test
  public void sendEmail_givenPEN_REQUEST_BATCH_ARCHIVE_HAS_SCHOOL_CONTACT_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("penRequestBatch.archive.hasSchoolContact", Map.of("penCoordinatorLoginUrl", "https://test.co", "submissionNumber", "ABC1234"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("ABC1234");
  }

  @Test
  public void sendEmail_givenPEN_REQUEST_BATCH_ARCHIVE_HAS_SCHOOL_CONTACT_ALL_PENDING_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("penRequestBatch.archive.hasSchoolContact.pending.all", Map.of("penCoordinatorLoginUrl", "https://test.co", "submissionNumber", "ABC1234"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("ABC1234");
  }

  @Test
  public void sendEmail_givenPEN_REQUEST_BATCH_ARCHIVE_HAS_SCHOOL_CONTACT_SOME_PENDING_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("penRequestBatch.archive.hasSchoolContact.pending.some", Map.of("penCoordinatorLoginUrl", "https://test.co", "submissionNumber", "ABC1234"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("ABC1234");
  }

  @Test
  public void sendEmail_givenPEN_REQUEST_BATCH_NOTIFY_SCHOOL_INCORRECT_FORMAT_FILE_EmailNotificationEntity_shouldSendCorrectEmail() {
    final var emailNotificationEntity = this.createEmailNotificationEntity("notify.school.incorrect.format.file", Map.of("dateTime", "2021-09-01", "submissionNumber", "ABC1234", "failReason", "No legal name", "fromEmail", "jack@test.com"));
    this.emailNotificationService.sendEmail(emailNotificationEntity);
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(emailNotificationEntity.getFromEmail()), eq(emailNotificationEntity.getToEmail()), this.emailBodyCaptor.capture(), eq(emailNotificationEntity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("2021-09-01");
    assertThat(this.emailBodyCaptor.getValue()).contains("ABC1234");
    assertThat(this.emailBodyCaptor.getValue()).contains("No legal name");
    assertThat(this.emailBodyCaptor.getValue()).contains("jack@test.com");
  }

  EmailNotificationEntity createEmailNotificationEntity(String templateName, Map<String, String> emailFields) {
    return EmailNotificationEntity.builder()
      .fromEmail("test@email.co")
      .toEmail(List.of("test@email.co"))
      .subject("PEN Registry Message")
      .templateName(templateName)
      .emailFields(emailFields)
      .build();
  }
}
