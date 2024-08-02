package es.in2.desmos.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jca.JCASupport;
import es.in2.desmos.infrastructure.security.JwtTokenProvider;
import es.in2.desmos.infrastructure.security.SecurityProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private final String resourceURI = "https://demos.dome-marketplace-lcl.org/api/v1/entities/12345678";
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SecurityProperties securityProperties;

    @BeforeEach
    void setUp() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        MockitoAnnotations.openMocks(this);
        when(securityProperties.privateKey())
                .thenReturn("0x1aff50dca1ac463a5af99a858c2eef7517b8e46d3bf84723ff6dcfead7dc8db6");
        jwtTokenProvider = new JwtTokenProvider(securityProperties);
    }

    @Test
    void testSecurityProvider() {
        Provider bc = BouncyCastleProviderSingleton.getInstance();
        Assertions.assertTrue(JCASupport.isSupported(JWSAlgorithm.ES256K, bc));
    }

    @Test
    void testGenerateToken() throws JOSEException {
        String token = jwtTokenProvider.generateToken(resourceURI);
        Assertions.assertNotNull(token);
    }

    @Test
    void testValidateSignedJwt() throws JOSEException, ParseException {
        String jwtString = jwtTokenProvider.generateToken(resourceURI);
        System.out.println(jwtString);
        boolean result = jwtTokenProvider.validateSignedJwt(jwtString);
        Assertions.assertTrue(result);
    }

    @Test
    void testInvalidJwt() {
        String invalidJwt = "invalid.jwt.token";
        assertThrows(ParseException.class, () -> jwtTokenProvider.validateSignedJwt(invalidJwt));
    }

}
