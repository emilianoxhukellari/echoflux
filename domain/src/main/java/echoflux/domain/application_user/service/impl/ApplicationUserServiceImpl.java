package echoflux.domain.application_user.service.impl;

import echoflux.domain.application_user.data.ApplicationUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import echoflux.domain.application_user.data.ApplicationUserRepository;
import echoflux.domain.application_user.service.ApplicationUserService;
import echoflux.domain.application_user.service.ChangePasswordCommand;
import echoflux.domain.application_user.service.CreateApplicationUserCommand;
import echoflux.domain.application_user.service.UpdateApplicationUserCommand;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository applicationUserRepository;

    @Override
    @Transactional
    public ApplicationUserEntity create(CreateApplicationUserCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());
        var user = new ApplicationUserEntity();
        user.setPassword(hashedPassword);
        user.setUsername(command.getUsername());
        user.setName(command.getName());
        user.setEnabled(command.getEnabled());
        user.setCountry(command.getCountry());
        user.setZoneId(command.getZoneId());
        user.setRoles(command.getRoles());

        return applicationUserRepository.save(user);
    }

    @Override
    @Transactional
    public ApplicationUserEntity update(UpdateApplicationUserCommand command) {
        var user = applicationUserRepository.getReferenceById(command.getId());
        user.setUsername(command.getUsername());
        user.setName(command.getName());
        user.setEnabled(command.getEnabled());
        user.setCountry(command.getCountry());
        user.setZoneId(command.getZoneId());
        user.setRoles(new HashSet<>(command.getRoles()));

        return applicationUserRepository.save(user);
    }

    @Override
    @Transactional
    public ApplicationUserEntity changePassword(ChangePasswordCommand command) {
        var hashedPassword = passwordEncoder.encode(command.getPassword());
        var user = applicationUserRepository.getReferenceById(command.getId());
        user.setPassword(hashedPassword);

        return applicationUserRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        applicationUserRepository.deleteById(id);
    }

}
