package transcribe.domain.application_user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;
import transcribe.domain.application_user.service.ApplicationUserMapper;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.application_user.service.ChangePasswordCommand;
import transcribe.domain.application_user.service.CreateApplicationUserCommand;
import transcribe.domain.application_user.service.UpdateApplicationUserCommand;

@Service
@RequiredArgsConstructor
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository repository;
    private final ApplicationUserMapper mapper;

    @Override
    @Transactional
    public ApplicationUserEntity create(CreateApplicationUserCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());
        var user = mapper.toEntity(command, hashedPassword);

        return repository.save(user);
    }

    @Override
    @Transactional
    public ApplicationUserEntity update(UpdateApplicationUserCommand command) {
        var user = repository.getReferenceById(command.getId());
        var patched = mapper.patch(user, command);

        return repository.save(patched);
    }

    @Override
    @Transactional
    public ApplicationUserEntity changePassword(ChangePasswordCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());
        var user = repository.getReferenceById(command.getId());
        var patched = mapper.patch(user, hashedPassword);

        return repository.save(patched);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
