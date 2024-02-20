package ca.bc.gov.educ.api.student.profile.email.struct.v1.penrequestbatch;

import java.util.*;
import lombok.Data;

@Data
public abstract class BatchNotificationEntity {
  String fromEmail;
  List<String> toEmail;
  String submissionNumber;
}
