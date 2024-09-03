package transcribe.domain.application_user.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transcribe.domain.application_user.data.ApplicationUserEntity;
import transcribe.domain.application_user.data.ApplicationUserRepository;
import transcribe.domain.application_user.service.ApplicationUserMapper;
import transcribe.domain.application_user.service.ApplicationUserService;
import transcribe.domain.application_user.service.CreateApplicationUserCommand;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository repository;
    private final ApplicationUserMapper mapper;

    public ApplicationUserEntity create(CreateApplicationUserCommand command) {
        Validate.isTrue(
                StringUtils.equals(command.getPassword(), command.getPasswordConfirmation()),
                "Password and password confirmation do not match"
        );
        var hashedPassword = passwordEncoder.encode(command.getPassword());

        return repository.saveAndFlush(mapper.map(command, hashedPassword));
    }


}
