package ca.bc.gov.educ.api.student.profile.email.repository;

import ca.bc.gov.educ.api.student.profile.email.model.EmailEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentProfileRequestEmailEventRepository extends JpaRepository<EmailEventEntity, UUID> {
  Optional<EmailEventEntity> findBySagaIdAndEventType(UUID sagaId, String eventType);

  List<EmailEventEntity> findByEventStatus(String toString);

  List<EmailEventEntity> findTop100ByEventStatusAndCreateDateBefore(String eventStatus, LocalDateTime createDateToCompare);

  @Transactional
  @Modifying
  @Query("delete from EmailEventEntity where createDate <= :createDate")
  void deleteByCreateDateBefore(LocalDateTime createDate);
}
