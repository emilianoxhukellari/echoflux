package echoflux.domain.application_user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.application_user.data.ApplicationUserProjection;
import echoflux.domain.application_user.data.ApplicationUserRepository;
import echoflux.domain.application_user.service.ApplicationUserMapper;
import echoflux.domain.application_user.service.ApplicationUserService;
import echoflux.domain.application_user.service.ChangePasswordCommand;
import echoflux.domain.application_user.service.CreateApplicationUserCommand;
import echoflux.domain.application_user.service.UpdateApplicationUserCommand;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserMapper applicationUserMapper;

    @Override
    @Transactional
    public ApplicationUserProjection create(CreateApplicationUserCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());
        var user = applicationUserMapper.toEntity(command, hashedPassword);
        var saved = applicationUserRepository.save(user);

        return applicationUserMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public ApplicationUserProjection patch(UpdateApplicationUserCommand command) {
        var user = applicationUserRepository.getReferenceById(command.getId());
        var patched = applicationUserMapper.patch(user, command);
        var saved = applicationUserRepository.save(patched);

        return applicationUserMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public ApplicationUserProjection changePassword(ChangePasswordCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());
        var user = applicationUserRepository.getReferenceById(command.getId());
        var patched = applicationUserMapper.patch(user, hashedPassword);
        var saved = applicationUserRepository.save(patched);

        return applicationUserMapper.toProjection(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        applicationUserRepository.deleteById(id);
    }

}
