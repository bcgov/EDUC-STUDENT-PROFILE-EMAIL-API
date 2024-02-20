package ca.bc.gov.educ.api.student.profile.email.controller;

import ca.bc.gov.educ.api.student.profile.email.controller.v2.EmailNotificationController;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.v2.EmailNotificationEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EmailNotificationControllerTest {

  /**
   * The Mock mvc.
   */
  @Autowired
  private MockMvc mockMvc;


  @Autowired
  EmailNotificationController controller;

  @Autowired
  RestUtils restUtils;

  @Captor
  ArgumentCaptor<String> emailBodyCaptor;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    doNothing().when(this.restUtils).sendEmail(any(), any(), any(), any());
  }

  @Test
  public void sendEmail_withoutScope_shouldReturnStatusForbidden() throws Exception {
    final var entity = this.createEmailNotificationEntity("verifyEmail.gmp", Map.of("identityTypeLabel", "Basic BCeID","verificationUrl", "https://test.co/verify?verificationToken", "jwtToken", "12345ABCDE"));

    this.mockMvc
        .perform(post("/api/v2/send-email")
            .with(jwt().jwt((jwt) -> jwt.claim("scope", "WRONG_SCOPE")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString((entity))))
        .andDo(print()).andExpect(status().isForbidden());
  }

  @Test
  public void sendEmail_givenValidPayloadWith_GMP_VERIFY_EMAIL_shouldSendEmail() throws Exception {
    final var entity = this.createEmailNotificationEntity("verifyEmail.gmp", Map.of("identityTypeLabel", "Basic BCeID","verificationUrl", "https://test.co/verify?verificationToken", "jwtToken", "12345ABCDE"));
    this.mockMvc.perform(post("/api/v2/send-email").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(entity.getFromEmail()), eq(entity.getToEmail()), this.emailBodyCaptor.capture(), eq(entity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).doesNotContainPattern("\\{\\d\\}");
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co/verify?verificationToken=12345ABCDE\">");
    assertThat(this.emailBodyCaptor.getValue()).contains("Basic BCeID");
  }

  @Test
  public void sendEmail_givenValidPayloadWith_GMP_NOTIFY_STALE_RETURN_shouldSendEmail() throws Exception {
    final var entity = this.createEmailNotificationEntity("notify.stale.return.gmp", Map.of("loginUrl", "https://test.co"));
    this.mockMvc.perform(post("/api/v2/send-email").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
    verify(this.restUtils, atLeastOnce()).sendEmail(eq(entity.getFromEmail()), eq(entity.getToEmail()), this.emailBodyCaptor.capture(), eq(entity.getSubject()));
    assertThat(this.emailBodyCaptor.getValue()).contains("<a href=\"https://test.co\">");
  }

  @Test
  public void sendEmail_givenNotExistingTemplate_shouldReturnError() throws Exception {
    final var entity = this.createEmailNotificationEntity("not.existing", Map.of("loginUrl", "https://test.co"));
    this.mockMvc.perform(post("/api/v2/send-email").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());
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

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
