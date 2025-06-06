//package express.atc.backend.config;
//
//import express.atc.backend.calculate.CalcCustomsFee;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.mockito.Mockito.mock;
//
//@TestConfiguration
//public class TestSecurityConfig {
//
//    @Bean
//    @Primary
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // Отключаем всю безопасность
//        http
//                .csrf().disable()
//                .authorizeRequests(auth -> auth.anyRequest().permitAll());
//        return http.build();
//    }
//
//    @Bean
//    @Primary
//    public CalcCustomsFee calcCustomsFee() {
//        // Создаем мок для сервиса расчета
//        return mock(CalcCustomsFee.class);
//    }
//}