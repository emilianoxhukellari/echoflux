package echoflux.domain.settings.data;

import echoflux.domain.core.repository.CoreJpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SettingsRepository extends CoreJpaRepository<SettingsEntity, Long> {

    Optional<SettingsEntity> findByKey(String key);

    List<SettingsEntity> findAllByKeyIn(Collection<String> keys);

    List<SettingsEntity> findAllByKeyNotIn(Collection<String> keys);

}
