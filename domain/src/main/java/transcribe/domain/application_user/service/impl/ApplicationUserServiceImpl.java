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
@Transactional
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository repository;
    private final ApplicationUserMapper mapper;

    @Override
    public ApplicationUserEntity create(CreateApplicationUserCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());

        return repository.saveAndFlush(mapper.toEntity(command, hashedPassword));
    }

    @Override
    public ApplicationUserEntity update(UpdateApplicationUserCommand command) {
        var user = repository.getReferenceById(command.getId());

        return repository.saveAndFlush(mapper.asEntity(user, command));
    }

    @Override
    public ApplicationUserEntity changePassword(ChangePasswordCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());
        var user = repository.getReferenceById(command.getId());

        return repository.saveAndFlush(mapper.asEntity(user, hashedPassword));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
