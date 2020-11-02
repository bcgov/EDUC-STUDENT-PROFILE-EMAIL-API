package ca.bc.gov.educ.api.student.profile.email.service;

import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import ca.bc.gov.educ.api.student.profile.email.model.Event;
import ca.bc.gov.educ.api.student.profile.email.model.GMPRequestCompleteEmailEntity;
import ca.bc.gov.educ.api.student.profile.email.repository.StudentProfileRequestEmailEventRepository;
import ca.bc.gov.educ.api.student.profile.email.rest.RestUtils;
import ca.bc.gov.educ.api.student.profile.email.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static ca.bc.gov.educ.api.student.profile.email.constants.IdentityType.BCSC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EventHandlerServiceTest {

  @Autowired
  EventHandlerService eventHandlerService;

  @Autowired
  StudentProfileRequestEmailEventRepository repository;
  @Autowired
  RestUtils restUtils;

  @Autowired
  RestTemplate restTemplate;
  @Before
  public void setUp() {
    initMocks(this);
  }

  @After
  public void tearDown() {
    repository.deleteAll();
  }

  @Test
  public void handleEvent_givenNotifyStudentPenRequestComplete_shouldSendCompleteEmail() throws JsonProcessingException {
    var sagaId = UUID.randomUUID();
    when(restUtils.getRestTemplate()).thenReturn(restTemplate);
    eventHandlerService.handleEvent(Event.builder().eventType(EventType.NOTIFY_STUDENT_PEN_REQUEST_COMPLETE)
        .eventPayload(JsonUtil.getJsonStringFromObject(createCompletedEmailEntity()))
        .replyTo("local")
        .sagaId(sagaId)
        .build());
    var records = repository.findAll();
    assertThat(records).size().isEqualTo(1);

  }
  GMPRequestCompleteEmailEntity createCompletedEmailEntity() {
    var entity = new GMPRequestCompleteEmailEntity();
    entity.setFirstName("FirstName");
    entity.setEmailAddress("test@gmail.com");
    entity.setIdentityType(BCSC.toString());
    entity.setDemographicsChanged(false);
    return entity;
  }
}
