package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.UserEntity;
import express.atc.backend.db.repository.UsersRepository;
import express.atc.backend.dto.ChangePasswordDto;
import express.atc.backend.dto.LanguageDto;
import express.atc.backend.dto.LoginDto;
import express.atc.backend.dto.UserDto;
import express.atc.backend.enums.Language;
import express.atc.backend.exception.ApiException;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.mapper.UserMapper;
import express.atc.backend.security.UserDetail;
import express.atc.backend.service.DocumentService;
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static express.atc.backend.Constants.PASSWORD_NOT_CONFIRMED;
import static express.atc.backend.Constants.USER_NOT_FOUND;
import static express.atc.backend.enums.UserRole.ROLE_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;
    private final DocumentService documentService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto findOrCreateByPhone(String phone) {
        return userMapper.toDto(getUserByPhone(phone).orElseGet(() -> createNewUser(phone)));
    }

    public UserDto findUserByPhone(String phone) {
        var user = getUserByPhone(phone).orElse(null);
        if (user == null) return null;
        return userMapper.toDto(user)
                .setDocument(documentService.findDocumentForUser(user));
    }

    private UserDetail findUserDetails(String phone) {
        return userDetailMapper.toUserDetail(userMapper.toDto(getUserByPhone(phone).orElse(null)));
    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::findUserDetails;
    }

    @Override
    public UserDto updateFullUserInfo(UserDto userInfo) {
        UserEntity entity = getUserByPhone(userInfo.getPhone())
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        entity = userMapper.toEntity(userInfo)
                .setId(entity.getId())
                .setRole(entity.getRole());
        entity.setEnable(entity.isFullInfo());
        var user = userMapper.toDto(usersRepository.save(entity));
        userInfo.getDocument().setUser(entity);
        var document = documentService.addOrUpdateDocument(userInfo.getDocument());
        user.setDocument(document);
        return user;
    }

    private UserDto returnFullUserInfo(UserEntity userEntity) {
        var document = documentService.findDocumentForUser(userEntity);
        return userMapper.toDto(userEntity).setDocument(document);
    }

    @Override
    public Set<String> getBatchUserPhone(int batchSize) {
        return usersRepository.findBatchPhone(batchSize);
    }

    @Override
    public LanguageDto getLanguage(String userPhone) {
        return new LanguageDto(usersRepository.findByPhone(userPhone)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND))
                .getLanguage());
    }

    @Override
    public LanguageDto setLanguage(String userPhone, Language language) {
        var entity = usersRepository.findByPhone(userPhone)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND))
                .setLanguage(language);
        return new LanguageDto(usersRepository.save(entity).getLanguage());
    }

    @Override
    public UserDto authenticate(LoginDto login) {
        return returnFullUserInfo(usersRepository.findByLogin(login.login())
                .filter(user -> passwordEncoder.matches(login.password(), user.getPassword()))
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND)));
    }

    @Override
    public UserDto changePassword(String userPhone, ChangePasswordDto changePassword) {
        UserEntity entity = getUserByPhone(userPhone)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (!changePassword.password().equals(changePassword.confirmed())) {
            throw new ApiException(PASSWORD_NOT_CONFIRMED, HttpStatus.BAD_REQUEST);
        }
        entity.setPassword(passwordEncoder.encode(changePassword.password()));
        return returnFullUserInfo(usersRepository.save(entity));
    }

    @Override
    public UserDto changeLogin(String userPhone, String login) {
        UserEntity entity = getUserByPhone(userPhone)
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        entity.setLogin(login);
        return returnFullUserInfo(usersRepository.save(entity));
    }

    private Optional<UserEntity> getUserByPhone(String phone) {
        return usersRepository.findByPhone(phone);
    }

    private UserEntity createNewUser(String phone) {
        UserEntity user = UserEntity.builder()
                .phone(phone)
                .role(ROLE_USER)
                .build();
        return usersRepository.save(user);
    }
}
