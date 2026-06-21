package com.neurotools.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AdminSecurityProperties::class, ClickTokenProperties::class)
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(
        properties: AdminSecurityProperties,
        passwordEncoder: PasswordEncoder
    ): UserDetailsService {
        val username = properties.adminUsername.trim()
        val password = properties.adminPassword.trim()

        val adminUser = User.withUsername(username)
            .password(passwordEncoder.encode(password))
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(adminUser)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                auth.requestMatchers(HttpMethod.GET, "/api/tools/**").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/tools/*/click").permitAll()
                auth.requestMatchers("/actuator/health", "/actuator/info").permitAll()
                auth.requestMatchers("/error").permitAll()
                auth.requestMatchers("/api/admin/**").hasRole("ADMIN")
                auth.anyRequest().denyAll()
            }
            .httpBasic(Customizer.withDefaults())
            .formLogin { it.disable() }
            .logout { it.disable() }

        return http.build()
    }
}
