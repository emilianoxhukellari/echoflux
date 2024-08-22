package transcribe.domain.application_user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;

import java.util.Optional;

@Service
public class ApplicationUserService {

    private final ApplicationUserRepository repository;

    public ApplicationUserService(ApplicationUserRepository repository) {
        this.repository = repository;
    }

    public Optional<ApplicationUserEntity> get(Long id) {
        return repository.findById(id);
    }

    public ApplicationUserEntity update(ApplicationUserEntity entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<ApplicationUserEntity> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ApplicationUserEntity> list(Pageable pageable, Specification<ApplicationUserEntity> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
