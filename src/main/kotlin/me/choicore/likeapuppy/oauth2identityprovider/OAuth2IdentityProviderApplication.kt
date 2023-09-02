package me.choicore.likeapuppy.oauth2identityprovider

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class OAuth2IdentityProviderApplication

fun main(args: Array<String>) {
    runApplication<OAuth2IdentityProviderApplication>(*args)
}
