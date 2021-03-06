package hanghae99.clonecoding.airbnb.security;

import hanghae99.clonecoding.airbnb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtFilter;
    private final OAuthLoginProvider oauthProvider;
    private final OAuth2AuthSuccessHanler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity
                .cors()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oauthProvider)
                .and()
                .successHandler(successHandler);
        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

}
