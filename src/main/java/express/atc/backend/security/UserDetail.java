package express.atc.backend.security;

import express.atc.backend.dto.UserDto;
import express.atc.backend.enums.UserRole;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
@Data
public class UserDetail implements UserDetails {
    private Long id;
    private String phone;
    private UserRole role;
    private boolean enable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRole().name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return getPhone();
    }
}
