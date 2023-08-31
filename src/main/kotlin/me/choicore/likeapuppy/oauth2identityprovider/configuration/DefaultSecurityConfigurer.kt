package me.choicore.likeapuppy.oauth2identityprovider.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint


@Configuration
@EnableWebSecurity
class DefaultSecurityConfigurer {

    private companion object {
        private const val API_END_POINT_PREFIX = "/v1"
        private val ALLOWED_STATIC_RESOURCES = arrayOf(
            "/assets/**",
            "/webjars/**"
        )
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .formLogin { formLogin ->
                formLogin.loginPage("/login").permitAll()
            }
            .authorizeHttpRequests { authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers(*ALLOWED_STATIC_RESOURCES).permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
            }
            .csrf { csrf -> csrf.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
        return httpSecurity.build()
    }
}