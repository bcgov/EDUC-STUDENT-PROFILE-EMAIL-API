package ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenRequestBatchSchoolErrorNotificationEntity extends BatchNotificationEntity {
  String fileName;
  String failReason;
  String dateTime;
}
