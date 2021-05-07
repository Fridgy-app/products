package me.rasztabiga.fridgy.products.client

import feign.RequestInterceptor
import feign.RequestTemplate
import me.rasztabiga.fridgy.products.security.oauth2.AuthorizationHeaderUtil

class TokenRelayRequestInterceptor(private val authorizationHeaderUtil: AuthorizationHeaderUtil) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        template.header(AUTHORIZATION, authorizationHeaderUtil.getAuthorizationHeader())
    }

    companion object {
        const val AUTHORIZATION: String = "Authorization"
    }
}
