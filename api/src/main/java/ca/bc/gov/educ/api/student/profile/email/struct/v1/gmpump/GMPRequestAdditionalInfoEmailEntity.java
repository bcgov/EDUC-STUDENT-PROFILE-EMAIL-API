package ca.bc.gov.educ.api.student.profile.email.struct.v1.gmpump;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true)
public class GMPRequestAdditionalInfoEmailEntity extends BaseEmailEntity {

}
