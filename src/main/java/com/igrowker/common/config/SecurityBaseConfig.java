package com.igrowker.common.config; // Или com.igrowker.config

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Важно для @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Для отключения CSRF в новом стиле
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // Помечает класс как конфигурационный
@EnableWebSecurity // Включает базовую веб-безопасность Spring Security
@EnableMethodSecurity // !!! Ключевая аннотация для работы @PreAuthorize, @Secured и т.д. !!!
public class SecurityBaseConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Настройка правил авторизации для HTTP запросов
                .authorizeHttpRequests(authorize -> authorize
                        // ПРИМЕР: Разрешить все запросы для простоты тестов
                        // В реальном приложении здесь будут более строгие правила
                        .anyRequest().permitAll() // ПОКА РАЗРЕШИМ ВСЕ, чтобы @PreAuthorize точно проверялся
                )
                // Включаем базовую аутентификацию или другие механизмы, если нужны для тестов
                .httpBasic(withDefaults()) // Пример: Включаем HTTP Basic (может не понадобиться при @WithMockUser)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Типично для REST API

                // !!! Важно для тестов с MockMvc и POST/PUT/PATCH/DELETE !!!
                // Отключаем CSRF защиту. В REST API она часто не нужна или мешает тестам MockMvc.
                // Если CSRF нужна, тесты MockMvc должны явно её поддерживать (.with(csrf()))
                .csrf(AbstractHttpConfigurer::disable); // Отключение CSRF в новом стиле

        return http.build();
    }

    // Можно добавить бины PasswordEncoder, AuthenticationManager и т.д., если они нужны,
    // но для работы @PreAuthorize с @WithMockUser достаточно SecurityFilterChain и @EnableMethodSecurity
}
