package me.rasztabiga.fridgy.products.security.oauth2

import me.rasztabiga.fridgy.products.security.extractAuthorityFromClaims
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class JwtGrantedAuthorityConverter : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt) = extractAuthorityFromClaims(jwt.claims)
}
