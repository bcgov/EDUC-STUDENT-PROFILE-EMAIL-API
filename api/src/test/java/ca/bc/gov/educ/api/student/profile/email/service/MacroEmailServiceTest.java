package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.props.MacroProperties;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.macro.MacroEditNotificationEntity;
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
public class MacroEmailServiceTest {

  @Autowired
  MacroEmailService macroEmailService;

  @Autowired
  CHESEmailService chesEmailService;

  @Autowired
  RestUtils restUtils;

  @Autowired
  MacroProperties properties;

  @Before
  public void setUp() {
    openMocks(this);
  }

  @Test
  public void notifyMacroEdit_givenMacroEditNotificationEntityAndNewMacro_shouldSendCorrectMacroCreateEmail() {
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    this.macroEmailService.notifyMacroEdit(this.createMacroNotificationEntity(), true);
    verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co", this.getMacroCreateBody(), this.getMacroCreateSubject());
  }

  @Test
  public void notifyMacroEdit_givenMacroEditNotificationEntityAndNotNewMacro_shouldSendCorrectMacroUpdateEmail() {
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
    this.macroEmailService.notifyMacroEdit(this.createMacroNotificationEntity(), false);
    verify(this.restUtils, atLeastOnce()).sendEmail("test@email.co", "test@email.co", this.getMacroUpdateBody(), this.getMacroUpdateSubject());
  }

  MacroEditNotificationEntity createMacroNotificationEntity() {
    final var entity = new MacroEditNotificationEntity();
    entity.setToEmail("test@email.co");
    entity.setFromEmail("test@email.co");
    entity.setAppName("PEN Registry");
    entity.setBusinessUseTypeName("GetMyPEN");
    entity.setMacroCode("!MID");
    entity.setMacroTypeCode("MOREINFO");
    entity.setMacroText("You have not declared any middle names.");
    return entity;
  }

  String getMacroCreateSubject() {
    return "INSERT macro PEN Registry in Dev, Test and UAT";
  }

  String getMacroCreateBody() {
    return "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"></head><body>New macro was created for GetMyPEN:<br><br>Code: !MID<br>Type: MOREINFO<br>Text: You have not declared any middle names.</body></html>";
  }

  String getMacroUpdateSubject() {
    return "UPDATE macro PEN Registry in Dev, Test and UAT";
  }

  String getMacroUpdateBody() {
    return "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"></head><body>GetMyPEN macro !MID was updated to:<br><br>Code: !MID<br>Type: MOREINFO<br>Text: You have not declared any middle names.</body></html>";
  }
}
