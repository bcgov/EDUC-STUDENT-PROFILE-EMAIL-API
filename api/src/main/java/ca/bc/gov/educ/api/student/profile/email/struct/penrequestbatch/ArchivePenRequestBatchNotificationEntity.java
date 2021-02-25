package ca.bc.gov.educ.api.student.profile.email.struct.penrequestbatch;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
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
}

