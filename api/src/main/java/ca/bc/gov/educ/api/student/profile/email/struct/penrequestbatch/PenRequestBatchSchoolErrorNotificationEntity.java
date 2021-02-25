package ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenRequestBatchSchoolErrorNotificationEntity extends BatchNotificationEntity {
  String subjectLine;
  String failReason;
  String dateTime;
}