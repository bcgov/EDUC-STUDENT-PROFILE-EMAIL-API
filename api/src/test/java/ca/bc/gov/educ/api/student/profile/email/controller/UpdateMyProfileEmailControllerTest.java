package ca.bc.gov.educ.api.student.profile.email.controller;

import ca.bc.gov.educ.api.student.profile.email.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.student.profile.email.model.*;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UpdateMyProfileEmailControllerTest {

  /**
   * The Mock mvc.
   */
  private MockMvc mockMvc;


  @Autowired
  UpdateMyProfileEmailController controller;

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
  public void sendCompletedRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    this.mockMvc.perform(post("/ump/complete").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedRequestEmail_givenValidPayload_shouldSendEmail2() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setIdentityType(BASIC.name());
    this.mockMvc.perform(post("/ump/complete").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedRequestEmail_givenValidPayload_shouldReturnError() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setIdentityType(BASIC.name());
    entity.setEmailAddress("invalidemail");
    this.mockMvc.perform(post("/ump/complete").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendCompletedRequestEmail_givenValidPayload_shouldReturnError2() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEntity();
    entity.setIdentityType("error");
    entity.setEmailAddress("invalidemail");
    this.mockMvc.perform(post("/ump/complete").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isBadRequest());
  }
  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendRejectedRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createRejectedEntity();
    this.mockMvc.perform(post("/ump/reject").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void sendAdditionalInfoRequestEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createAdditionalInfoEntity();
    this.mockMvc.perform(post("/ump/info").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void verifyEmail_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createEmailVerificationEntity();
    this.mockMvc.perform(post("/ump/verify").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithMockOAuth2Scope(scope = "SEND_STUDENT_PROFILE_EMAIL")
  public void notifyStudentForStaleReturnedRequests_givenValidPayload_shouldSendEmail() throws Exception {
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    var entity = createAdditionalInfoEntity();
    this.mockMvc.perform(post("/ump/notify-stale-return").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(asJsonString(entity))).andDo(print()).andExpect(status().isNoContent());
  }

  UMPRequestCompleteEmailEntity createEntity() {
    var entity = new UMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  UMPRequestRejectedEmailEntity createRejectedEntity() {
    var entity = new UMPRequestRejectedEmailEntity();
    entity.setRejectionReason("rejected");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }


  UMPAdditionalInfoEmailEntity createAdditionalInfoEntity() {
    var entity = new UMPAdditionalInfoEmailEntity();
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    return entity;
  }

  UMPRequestEmailVerificationEntity createEmailVerificationEntity() {
    var entity = new UMPRequestEmailVerificationEntity("1234","BC Services Card","http://localhost","token");
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
