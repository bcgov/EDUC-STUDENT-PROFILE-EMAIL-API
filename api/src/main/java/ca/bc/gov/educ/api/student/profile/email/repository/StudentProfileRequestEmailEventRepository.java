package ca.bc.gov.educ.api.student.profile.email.repository;

import ca.bc.gov.educ.api.student.profile.email.model.StudentProfileReqEmailEventEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRequestEmailEventRepository extends CrudRepository<StudentProfileReqEmailEventEntity, UUID> {
  Optional<StudentProfileReqEmailEventEntity> findBySagaIdAndEventType(UUID sagaId, String eventType);

  List<StudentProfileReqEmailEventEntity> findByEventStatus(String toString);
}
