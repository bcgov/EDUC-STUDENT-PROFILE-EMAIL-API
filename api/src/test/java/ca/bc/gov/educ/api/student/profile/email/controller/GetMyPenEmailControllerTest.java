package ca.bc.gov.educ.api.student.profile.email.controller;

import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.GMPRequestAdditionalInfoEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.GMPRequestCompleteEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.GMPRequestEmailVerificationEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.gmpump.GMPRequestRejectedEmailEntity;
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
public class GetMyPenEmailControllerTest {

  @Autowired
  private MockMvc mockMvc;


  @Autowired
  GetMyPenEmailController controller;

  @Autowired
  RestUtils restUtils;


  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test

  public void sendCompletedPENRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    final var entity = this.createEntity();
    entity.setDemographicsChanged(false);
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());

  }

  @Test

  public void sendCompletedPENRequestEmail_givenValidPayload_shouldSendEmail2() throws Exception {
    final var entity = this.createEntity();
    entity.setDemographicsChanged(true);
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=true").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());

  }

  @Test

  public void sendCompletedPENRequestEmail_givenValidPayload_shouldSendEmail3() throws Exception {
    final var entity = this.createEntity();
    entity.setDemographicsChanged(false);
    entity.setIdentityType(BASIC.name());
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());

  }


  @Test

  public void sendCompletedPENRequestEmail_givenInValidPayload_shouldReturnError() throws Exception {

    final var entity = this.createEntity();
    entity.setDemographicsChanged(false);
    entity.setEmailAddress("a@b.c");
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());

  }


  @Test

  public void sendCompletedPENRequestEmail_givenInValidPayload_shouldReturnError2() throws Exception {

    final var entity = this.createEntity();
    entity.setDemographicsChanged(false);
    entity.setIdentityType("error");
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());

  }

  @Test

  public void sendCompletedPENRequestEmail_givenInvalidPayload_shouldReturnError() throws Exception {

    this.mockMvc.perform(post("/gmp/complete").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(this.createEntity()))).andDo(print()).andExpect(status().isBadRequest());

  }

  @Test

  public void sendRejectedPENRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {

    final var entity = this.createRejectedEntity();
    this.mockMvc.perform(post("/gmp/reject").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test

  public void sendAdditionalInfoRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {

    this.mockMvc.perform(post("/gmp/info").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(this.createAdditionalInfoEntity()))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test

  public void verifyEmail_givenValidPayload_shouldSendEmail() throws Exception {

    this.mockMvc.perform(post("/gmp/verify").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(this.createEmailVerificationEntity()))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test

  public void notifyStudentForStaleReturnedRequests_givenValidPayload_shouldSendEmail() throws Exception {

    this.mockMvc.perform(post("/gmp/notify-stale-return").with(jwt().jwt((jwt) -> jwt.claim("scope", "SEND_STUDENT_PROFILE_EMAIL"))).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(this.createAdditionalInfoEntity()))).andDo(print()).andExpect(status().isNoContent());
  }

  GMPRequestCompleteEmailEntity createEntity() {
    final var entity = new GMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
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

  GMPRequestEmailVerificationEntity createEmailVerificationEntity() {
    final var entity = new GMPRequestEmailVerificationEntity("1234", "BC Services Card", "http://localhost", "token");
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
