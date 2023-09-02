package me.choicore.likeapuppy.oauth2identityprovider

import com.nimbusds.jose.util.Base64
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class AuthorizationGrantTypeTests(
    val mockMvc: MockMvc,
) {

    companion object {
        private const val REDIRECT_URI = "http://127.0.0.1:8080/v1/oauth2/authorize"

        private val AUTHORIZATION_REQUEST: String = UriComponentsBuilder
            .fromPath("/oauth2/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", "like-a-puppy")
            .queryParam("client_secret", "like-a-puppy")
            .queryParam("scope", "USER")
            .queryParam("state", "some-state")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString()
    }

    @Test
    @DisplayName("grant_type=authorization_code 을 통해 사용자에게 인증 토큰을 발급한다.")
    @WithMockUser(username = "1", password = "1", roles = ["USER"])
    fun whenUserRequestAuthorizationThenGetAccessTokenWithGrantedAuthorizationCode() {
        val mvcResult: MvcResult = mockMvc.get(AUTHORIZATION_REQUEST)
            .andExpect {
                status { is3xxRedirection() }
            }.andReturn()

        val redirectedUrl: String = checkNotNull(mvcResult.response.redirectedUrl)

        assertThat(redirectedUrl).startsWith(REDIRECT_URI)

        val queryParams: MultiValueMap<String, String> = UriComponentsBuilder.fromUriString(redirectedUrl).build().queryParams
        val code: String = checkNotNull(queryParams.getFirst("code"))

        val authorizationUri = UriComponentsBuilder
            .fromPath("/v1/oauth2/token")
            .queryParam("grant_type", "authorization_code")
            .queryParam("code", code)
            .queryParam("client_id", "like-a-puppy")
            .queryParam("client_secret", "like-a-puppy")
            .queryParam("scope", "USER")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString()

        mockMvc.post(authorizationUri)
            .andExpect {
                status { isOk() }
                content {
                    contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                }
            }
            .andDo { print() }
    }

    @Test
    @DisplayName("grant_type=client_credentials 을 통해 사용자에게 인증 토큰을 발급한다.")
    @WithMockUser(username = "1", password = "1", roles = ["USER"])
    fun whenUserRequestAuthorizationThenGetAccessTokenWithGrantedClientCredentials() {

        val authorizationUri = UriComponentsBuilder
            .fromPath("/v1/oauth2/token")
            .queryParam("grant_type", "client_credentials")
            .queryParam("client_id", "like-a-puppy")
            .queryParam("client_secret", "like-a-puppy")
            .queryParam("scope", "USER")
            .toUriString()

        mockMvc.post(authorizationUri) {
            header(HttpHeaders.AUTHORIZATION, "Basic ${Base64.encode("like-a-puppy:like-a-puppy".toByteArray())}")
        }.andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
            }
        }.andDo { print() }
    }
}