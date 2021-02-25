package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.PenRequestBatchProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch.ArchivePenRequestBatchNotificationEntity;
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
        verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co",this.getArchivePenRequestBatchHasSchoolContactBody(), this.getArchivePenRequestBatchHasSchoolContactSubject());
    }

    @Test
    public void sendArchivePenRequestBatchHasNoSchoolContactEmail_givenArchivePenRequestBatchEmailEntity_shouldSendCorrectEmail() {
        doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
        this.prbEmailService.sendArchivePenRequestBatchHasNoSchoolContactEmail(this.createArchivePenRequestBatchNotificationEntity());
        verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co",this.getArchivePenRequestBatchHasNoSchoolContactBody(), this.getArchivePenRequestBatchHasNoSchoolContactSubject());
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
        return "has school subject Columneetza Secondary";
    }

    String getArchivePenRequestBatchHasNoSchoolContactBody() {
        return "test 123 000001 test url";
    }

    String getArchivePenRequestBatchHasNoSchoolContactSubject() {
        return "has no school subject 123";
    }
}
