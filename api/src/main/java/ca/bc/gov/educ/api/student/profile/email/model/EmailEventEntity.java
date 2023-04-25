package ca.bc.gov.educ.api.student.profile.email.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "STUDENT_PROFILE_EMAIL_EVENT")
@Data
@DynamicUpdate
public class EmailEventEntity {


  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
      @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "EVENT_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  private UUID eventId;

  @NotNull(message = "eventPayload cannot be null")
  @Lob
  @Column(name = "EVENT_PAYLOAD")
  private byte[] eventPayloadBytes;

  @NotNull(message = "eventStatus cannot be null")
  @Column(name = "EVENT_STATUS")
  private String eventStatus;

  @NotNull(message = "eventType cannot be null")
  @Column(name = "EVENT_TYPE")
  private String eventType;

  @Column(name = "CREATE_USER", updatable = false)
  String createUser;

  @Column(name = "CREATE_DATE", updatable = false)
  @PastOrPresent
  LocalDateTime createDate;

  @Column(name = "UPDATE_USER")
  String updateUser;

  @Column(name = "UPDATE_DATE")
  @PastOrPresent
  LocalDateTime updateDate;

  @Column(name = "SAGA_ID", updatable = false)
  private UUID sagaId;

  @NotNull(message = "eventOutcome cannot be null.")
  @Column(name = "EVENT_OUTCOME")
  private String eventOutcome;

  @Column(name = "REPLY_CHANNEL")
  private String replyChannel;

  public String getEventPayload() {
    return new String(getEventPayloadBytes(), StandardCharsets.UTF_8);
  }

  public void setEventPayload(String eventPayload) {
    setEventPayloadBytes(eventPayload.getBytes(StandardCharsets.UTF_8));
  }

  public static class EmailEventEntityBuilder {
    byte[] eventPayloadBytes;

    public EmailEventEntity.EmailEventEntityBuilder eventPayload(String eventPayload) {
      this.eventPayloadBytes = eventPayload.getBytes(StandardCharsets.UTF_8);
      return this;
    }
  }
}

