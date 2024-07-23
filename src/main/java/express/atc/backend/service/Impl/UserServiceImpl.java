package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.UserEntity;
import express.atc.backend.db.repository.UsersRepository;
import express.atc.backend.dto.UserDto;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.mapper.UserMapper;
import express.atc.backend.security.UserDetail;
import express.atc.backend.service.DocumentService;
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static express.atc.backend.enums.UserRole.ROLE_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;
    private final DocumentService documentService;

    @Override
    public UserDto findOrCreateByPhone(String phone) {
        return userMapper.toDto(getUserByPhone(phone).orElseGet(() -> createNewUser(phone)));
    }

    public UserDto findUserByPhone(String phone) {
        return userMapper.toDto(getUserByPhone(phone).orElse(null));
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
                .orElseThrow();
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
