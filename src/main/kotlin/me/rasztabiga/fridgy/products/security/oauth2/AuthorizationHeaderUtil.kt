package me.rasztabiga.fridgy.products.security.oauth2

import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.core.*
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.regex.Pattern
import java.util.stream.Collectors

@Component
class AuthorizationHeaderUtil(
    private val clientService: OAuth2AuthorizedClientService,
    private val restTemplateBuilder: RestTemplateBuilder
) {
    private val log = LoggerFactory.getLogger(AuthorizationHeaderUtil::class.java)
    fun getAuthorizationHeader(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is OAuth2AuthenticationToken) {
            val oauthToken = authentication
            val name = oauthToken.name
            val registrationId = oauthToken.authorizedClientRegistrationId
            val client = clientService.loadAuthorizedClient<OAuth2AuthorizedClient>(registrationId, name)
                ?: throw OAuth2AuthorizationException(OAuth2Error("access_denied", "The token is expired", null))
            val accessToken = client.accessToken
            if (accessToken != null) {
                val tokenType = accessToken.tokenType.value
                var accessTokenValue = accessToken.tokenValue
                if (isExpired(accessToken)) {
                    log.info("AccessToken expired, refreshing automatically")
                    accessTokenValue = refreshToken(client, oauthToken)
                    if (null == accessTokenValue) {
                        SecurityContextHolder.getContext().authentication = null
                        throw OAuth2AuthorizationException(
                            OAuth2Error(
                                "access_denied",
                                "The token is expired",
                                null
                            )
                        )
                    }
                }
                val authorizationHeaderValue = String.format("%s %s", tokenType, accessTokenValue)
                return authorizationHeaderValue
            }
        } else if (authentication is JwtAuthenticationToken) {
            val tokenValue = authentication.token.tokenValue
            val authorizationHeaderValue =
                String.format("%s %s", OAuth2AccessToken.TokenType.BEARER.value, tokenValue)
            return authorizationHeaderValue
        }
        return ""
    }

    private fun refreshToken(client: OAuth2AuthorizedClient, oauthToken: OAuth2AuthenticationToken): String? {
        val atr = refreshTokenClient(client)
        if (atr == null || atr.accessToken == null) {
            log.info("Failed to refresh token for user")
            return null
        }
        val refreshToken = if (atr.refreshToken != null) atr.refreshToken else client.refreshToken
        val updatedClient = OAuth2AuthorizedClient(
            client.clientRegistration,
            client.principalName,
            atr.accessToken,
            refreshToken
        )
        clientService.saveAuthorizedClient(updatedClient, oauthToken)
        return atr.accessToken.tokenValue
    }

    private fun refreshTokenClient(currentClient: OAuth2AuthorizedClient): OAuth2AccessTokenResponse {
        val formParameters: MultiValueMap<String, String> = LinkedMultiValueMap()
        formParameters.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.value)
        formParameters.add(OAuth2ParameterNames.REFRESH_TOKEN, currentClient.refreshToken.tokenValue)
        formParameters.add(OAuth2ParameterNames.CLIENT_ID, currentClient.clientRegistration.clientId)
        val requestEntity: RequestEntity<*> = RequestEntity
            .post(URI.create(currentClient.clientRegistration.providerDetails.tokenUri))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formParameters)
        return try {
            val r = restTemplate(
                currentClient.clientRegistration.clientId,
                currentClient.clientRegistration.clientSecret
            )
            val responseEntity = r.exchange(
                requestEntity,
                OAuthIdpTokenResponseDTO::class.java
            )
            toOAuth2AccessTokenResponse(responseEntity.body)
        } catch (e: OAuth2AuthorizationException) {
            log.error("Unable to refresh token", e)
            throw OAuth2AuthenticationException(e.error, e)
        }
    }

    private fun toOAuth2AccessTokenResponse(oAuthIdpResponse: OAuthIdpTokenResponseDTO): OAuth2AccessTokenResponse {
        val additionalParameters: MutableMap<String, Any?> = HashMap()
        additionalParameters["id_token"] = oAuthIdpResponse.idToken
        additionalParameters["not-before-policy"] = oAuthIdpResponse.notBefore
        additionalParameters["refresh_expires_in"] = oAuthIdpResponse.refreshExpiresIn
        additionalParameters["session_state"] = oAuthIdpResponse.sessionState
        return OAuth2AccessTokenResponse
            .withToken(oAuthIdpResponse.accessToken)
            .expiresIn(oAuthIdpResponse.expiresIn!!)
            .refreshToken(oAuthIdpResponse.refreshToken)
            .scopes(Pattern.compile("\\s").splitAsStream(oAuthIdpResponse.scope).collect(Collectors.toSet()))
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .additionalParameters(additionalParameters)
            .build()
    }

    private fun restTemplate(clientId: String, clientSecret: String): RestTemplate {
        return restTemplateBuilder
            .additionalMessageConverters(FormHttpMessageConverter(), OAuth2AccessTokenResponseHttpMessageConverter())
            .errorHandler(OAuth2ErrorResponseErrorHandler())
            .basicAuthentication(clientId, clientSecret)
            .build()
    }

    private fun isExpired(accessToken: OAuth2AccessToken): Boolean {
        val now = Instant.now()
        val expiresAt = accessToken.expiresAt
        return now.isAfter(expiresAt.minus(Duration.ofMinutes(1L)))
    }
}
