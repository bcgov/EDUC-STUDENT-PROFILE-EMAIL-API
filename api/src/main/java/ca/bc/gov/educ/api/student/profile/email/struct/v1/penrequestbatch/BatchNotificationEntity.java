package ca.bc.gov.educ.api.student.profile.email.struct.v1.penrequestbatch;

import lombok.Data;

@Data
public abstract class BatchNotificationEntity {
  String fromEmail;
  String toEmail;
  String submissionNumber;
}
