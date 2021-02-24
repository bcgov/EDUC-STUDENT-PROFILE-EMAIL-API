package ca.bc.gov.educ.api.student.profile.email.controller;

import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPAdditionalInfoEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPRequestCompleteEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPRequestEmailVerificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.UMPRequestRejectedEmailEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BASIC;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UpdateMyProfileEmailControllerTest {

  /**
   * The Mock mvc.
   */
  @Autowired
  private MockMvc mockMvc;


  @Autowired
  UpdateMyProfileEmailController controller;

  @Autowired
  RestUtils restUtils;


  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void sendCompletedRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    final var entity = this.createEntity();
    this.mockMvc.perform(post("/ump/complete").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  public void sendCompletedRequestEmail_givenValidPayload_shouldSendEmail2() throws Exception {
    final var entity = this.createEntity();
    entity.setIdentityType(BASIC.name());
    this.mockMvc.perform(post("/ump/complete").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  public void sendCompletedRequestEmail_givenValidPayload_shouldReturnError() throws Exception {
    final var entity = this.createEntity();
    entity.setIdentityType(BASIC.name());
    entity.setEmailAddress("a@b.c");
    this.mockMvc.perform(post("/ump/complete").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  public void sendCompletedRequestEmail_givenValidPayload_shouldReturnError2() throws Exception {
    final var entity = this.createEntity();
    entity.setIdentityType("error");
    this.mockMvc.perform(post("/ump/complete").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());
  }
  @Test
  public void sendRejectedRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    final var entity = this.createRejectedEntity();
    this.mockMvc.perform(post("/ump/reject").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  public void sendAdditionalInfoRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    final var entity = this.createAdditionalInfoEntity();
    this.mockMvc.perform(post("/ump/info").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  public void verifyEmail_givenValidPayload_shouldSendEmail() throws Exception {
    final var entity = this.createEmailVerificationEntity();
    this.mockMvc.perform(post("/ump/verify").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  public void notifyStudentForStaleReturnedRequests_givenValidPayload_shouldSendEmail() throws Exception {
    final var entity = this.createAdditionalInfoEntity();
    this.mockMvc.perform(post("/ump/notify-stale-return").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  UMPRequestCompleteEmailEntity createEntity() {
    final var entity = new UMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  UMPRequestRejectedEmailEntity createRejectedEntity() {
    final var entity = new UMPRequestRejectedEmailEntity();
    entity.setRejectionReason("rejected");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }


  UMPAdditionalInfoEmailEntity createAdditionalInfoEntity() {
    final var entity = new UMPAdditionalInfoEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  UMPRequestEmailVerificationEntity createEmailVerificationEntity() {
    final var entity = new UMPRequestEmailVerificationEntity("1234", "BC Services Card", "http://localhost", "token");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }
  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
