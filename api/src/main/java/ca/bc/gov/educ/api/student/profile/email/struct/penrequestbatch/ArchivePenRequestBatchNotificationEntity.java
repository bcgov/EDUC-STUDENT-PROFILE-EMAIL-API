package ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch;


import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArchivePenRequestBatchNotificationEntity extends BatchNotificationEntity {

  /**
   * The archived pen request batch's school name
   */
  private String schoolName;

  /**
   * The archived pen request batch's mincode
   */
  private String mincode;

  private PendingRecords pendingRecords;
}

