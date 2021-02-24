package ca.bc.gov.educ.api.student.profile.email.mappers;

import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import ca.bc.gov.educ.api.student.profile.email.struct.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailEventMapper {
  EmailEventMapper mapper = Mappers.getMapper(EmailEventMapper.class);

  @Mapping(target = "replyTo", source = "replyChannel")
  Event toEvent(EmailEventEntity emailEventEntity);
}
