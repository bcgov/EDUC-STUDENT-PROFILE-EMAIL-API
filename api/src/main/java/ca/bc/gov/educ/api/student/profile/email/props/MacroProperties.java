package ca.bc.gov.educ.api.student.profile.email.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class MacroProperties {
  @Value("${email.subject.macro.create}")
  private String macroCreateEmailSubject;

  @Value("${email.subject.macro.update}")
  private String macroUpdateEmailSubject;
}
