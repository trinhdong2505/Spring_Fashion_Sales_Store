package Spring_AdamStore.config;


import Spring_AdamStore.constants.TokenType;
import Spring_AdamStore.exception.AppException;
import Spring_AdamStore.service.TokenService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@RequiredArgsConstructor
@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final TokenService tokenService;
    private final NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // check token (signer key, blacklist, ...)
            tokenService.verifyToken(token, TokenType.ACCESS_TOKEN);
        } catch (JOSEException | ParseException e) {
            throw new BadJwtException(e.getMessage());
        } catch (AppException ex){
            throw new BadJwtException("Token không hợp lệ");
        }
        return nimbusJwtDecoder.decode(token);
    }

}

