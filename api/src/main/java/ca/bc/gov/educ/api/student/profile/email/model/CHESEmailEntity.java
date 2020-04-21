package ca.bc.gov.educ.api.student.profile.email.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CHESEmailEntity {

  private String bodyType;
  private String body;
  private Integer delayTS;
  private String encoding;
  private String from;
  private String subject;
  private String priority;
  private List<String> to;
  private String tag;

  public List<String> getTo() {
    if (to == null) {
      to = new ArrayList<>();
    }
    return to;
  }
}
