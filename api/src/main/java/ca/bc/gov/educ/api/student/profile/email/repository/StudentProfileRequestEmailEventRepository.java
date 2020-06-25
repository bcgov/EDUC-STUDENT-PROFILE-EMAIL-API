package ca.bc.gov.educ.api.student.profile.email.repository;

import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRequestEmailEventRepository extends CrudRepository<EmailEventEntity, UUID> {
  Optional<EmailEventEntity> findBySagaIdAndEventType(UUID sagaId, String eventType);

  List<EmailEventEntity> findByEventStatus(String toString);
}
