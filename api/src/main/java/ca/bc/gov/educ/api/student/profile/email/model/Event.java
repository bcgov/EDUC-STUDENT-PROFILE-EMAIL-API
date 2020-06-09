package ca.bc.gov.educ.api.student.profile.email.model;

import ca.bc.gov.educ.api.student.profile.email.constants.EventOutcome;
import ca.bc.gov.educ.api.student.profile.email.constants.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class Event {
  private EventType eventType;
  private EventOutcome eventOutcome;
  private UUID sagaId;
  private String replyTo;
  private String eventPayload; // json string
}
