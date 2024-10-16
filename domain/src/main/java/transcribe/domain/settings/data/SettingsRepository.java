package transcribe.domain.settings.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SettingsRepository extends JpaRepository<SettingsEntity, Long>,
        JpaSpecificationExecutor<SettingsEntity> {

    Optional<SettingsEntity> findByKey(String key);

    List<SettingsEntity> findAllByKeyIn(Collection<String> keys);

    List<SettingsEntity> findAllByKeyNotIn(Collection<String> keys);

}
