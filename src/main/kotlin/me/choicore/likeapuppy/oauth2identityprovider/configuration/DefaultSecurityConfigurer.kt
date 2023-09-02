package me.choicore.likeapuppy.oauth2identityprovider.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@EnableWebSecurity
@Configuration // (proxyBeanMethods = false)
class DefaultSecurityConfigurer {

    private companion object {
        private const val API_END_POINT_PREFIX = "/v1"
        private val ALLOWED_STATIC_RESOURCES = arrayOf(
            AntPathRequestMatcher("/assets/**"),
            AntPathRequestMatcher("/webjars/**"),
            AntPathRequestMatcher("/h2-console/**"),
        )
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(
        httpSecurity: HttpSecurity,
        registeredClientRepository: RegisteredClientRepository,
        authorizationService: OAuth2AuthorizationService,
        jwtEncoder: JwtEncoder,
        authorizationServerSettings: AuthorizationServerSettings,
    ): SecurityFilterChain {
        OAuth2AuthorizationServerConfigurer()
            .apply { httpSecurity.apply(this) }
            .authorizationService(authorizationService)
            .registeredClientRepository(registeredClientRepository)
            .tokenGenerator(JwtGenerator(jwtEncoder))
            .authorizationServerSettings(authorizationServerSettings)
        httpSecurity
            .formLogin { formLogin ->
                formLogin.loginPage("/login").permitAll()
            }
            .logout { logout ->
                logout.logoutSuccessUrl("/login")
                logout.clearAuthentication(true)
                logout.deleteCookies("JSESSIONID")
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

    @Bean
    fun users(): UserDetailsService {
        val userDetails: UserDetails = User.withDefaultPasswordEncoder()
            .username("1")
            .password("1")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(userDetails)
    }
}