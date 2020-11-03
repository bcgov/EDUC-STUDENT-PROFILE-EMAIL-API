package ca.bc.gov.educ.api.student.profile.email.controller;

import ca.bc.gov.educ.api.student.profile.email.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestAdditionalInfoEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestCompleteEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestEmailVerificationEntity;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestRejectedEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.support.WithMockOAuth2Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BASIC;
import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GetMyPenEmailControllerTest {

  /**
   * The Mock mvc.
   */
  private MockMvc mockMvc;


  @Autowired
  GetMyPenEmailController controller;

  @Autowired
  RestUtils restUtils;

  @Autowired
  RestTemplate restTemplate;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new RestExceptionHandler()).build();
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedPENRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setDemographicsChanged(false);
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());

  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedPENRequestEmail_givenValidPayload_shouldSendEmail2() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setDemographicsChanged(true);
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=true").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());

  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedPENRequestEmail_givenValidPayload_shouldSendEmail3() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setDemographicsChanged(false);
    entity.setIdentityType(BASIC.name());
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());

  }


  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedPENRequestEmail_givenInValidPayload_shouldReturnError() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setDemographicsChanged(false);
    entity.setEmailAddress("testemail");
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());

  }


  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedPENRequestEmail_givenInValidPayload_shouldReturnError2() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setDemographicsChanged(false);
    entity.setIdentityType("error");
    this.mockMvc.perform(post("/gmp/complete?demographicsChanged=false").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());

  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedPENRequestEmail_givenInvalidPayload_shouldReturnError() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    this.mockMvc.perform(post("/gmp/complete").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(createEntity()))).andDo(print()).andExpect(status().isBadRequest());

  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendRejectedPENRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createRejectedEntity();
    this.mockMvc.perform(post("/gmp/reject").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendAdditionalInfoRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    this.mockMvc.perform(post("/gmp/info").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(createAdditionalInfoEntity()))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void verifyEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    this.mockMvc.perform(post("/gmp/verify").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(createEmailVerificationEntity()))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void notifyStudentForStaleReturnedRequests_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    this.mockMvc.perform(post("/gmp/notify-stale-return").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(createAdditionalInfoEntity()))).andDo(print()).andExpect(status().isNoContent());
  }

  GMPRequestCompleteEmailEntity createEntity() {
    var entity = new GMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  GMPRequestRejectedEmailEntity createRejectedEntity() {
    var entity = new GMPRequestRejectedEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    entity.setRejectionReason("rejected");
    return entity;
  }

  GMPRequestAdditionalInfoEmailEntity createAdditionalInfoEntity() {
    var entity = new GMPRequestAdditionalInfoEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  GMPRequestEmailVerificationEntity createEmailVerificationEntity() {
    var entity = new GMPRequestEmailVerificationEntity("1234","BC Services Card","http://localhost","token");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
