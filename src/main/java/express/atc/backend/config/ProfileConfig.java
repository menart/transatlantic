package express.atc.backend.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "profile.cookie")
public class ProfileConfig {
    @Getter
    private static String sameSite = "Strict"; // значение по умолчанию

    public void setSameSite(String sameSite) {
        ProfileConfig.sameSite = sameSite;
    }
}
