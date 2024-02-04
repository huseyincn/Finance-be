package com.huseyincan.financeportfolio.config;

import com.huseyincan.financeportfolio.util.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
    Spring Bean'e dahil etmek ve bunun bir konfigürasyon olduğunu belirtmek için bu anotasyonu kullanıyoruz.
 */
@Configuration
/*
    Oto konfigürasyonu etkisiz hale getirebilmek için bu anotasyon çok önemlidir.
    Bu anotasyon springSecurityFilterChain olarak bilinen bir servlet filter'ı oluşturarak
    gelen giden isteklerin filtrelenmesini ve bunun üzerinden authetication authorization işlemlerinin yapılmasını sağlar.
    Daha fazla bilgi için: https://docs.spring.io/spring-security/site/docs/4.0.1.RELEASE/reference/html/jc.html#hello-web-security-java-configuration
*/
@EnableWebSecurity
/*
    Metod bazında güvenlik kontrolü için @EnableGlobalMethodSecurity anotasyonu kullanılabilir.
    Bu anotasyon kullanıldığında metodlara @Secured anotasyonu ile o metodu hangi role sahip kullanıcının koşturacağı belirtilebilir.
    prePostEnabled = true vererek @Secured yapılan metoddan önce ve sonra @Pre... ve @Post... anotasyonları ile bazı kontroller vb işlemlerin yapılmasına izin verilir.
*/
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private UserDetailService userDetailsService;
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    public SecurityConfig(JwtAuthorizationFilter jwtTokenFilter,
                          UserDetailService userDetailService) {
        this.jwtAuthorizationFilter = jwtTokenFilter;
        this.userDetailsService = userDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
