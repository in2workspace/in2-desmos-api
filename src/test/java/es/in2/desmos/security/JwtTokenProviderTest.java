package es.in2.desmos.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jca.JCASupport;
import com.nimbusds.jwt.SignedJWT;
import es.in2.desmos.infrastructure.security.JwtTokenProvider;
import es.in2.desmos.infrastructure.security.SecurityProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private final String resourceURI = "https://demos.dome-marketplace-lcl.org/api/v1/entities/12345678";
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SecurityProperties securityProperties;

    @BeforeEach
    void setUp() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        MockitoAnnotations.openMocks(this);
        when(securityProperties.privateKey())
                .thenReturn("0x7c8afd9cb9c67cdbe2961e380b52c4206fca5a81d05e4d426975822ff28605db");
        jwtTokenProvider = new JwtTokenProvider(securityProperties);
    }

    @Test
    void testSecurityProvider() {
        Provider bc = BouncyCastleProviderSingleton.getInstance();
        Assertions.assertTrue(JCASupport.isSupported(JWSAlgorithm.ES256, bc));
    }

    @Test
    void testGenerateToken() throws JOSEException {
        String token = jwtTokenProvider.generateToken(resourceURI);
        Assertions.assertNotNull(token);
    }

    @Test
    void testValidateSignedJwt() throws JOSEException {

        HashMap<String, String> publicKeysByUrl = new HashMap<>();
        publicKeysByUrl.put("origin", "0x041f813b948021acb2d0f7fbe1247f925f9491db60f3ae33be4f0da89739e10d98928552055281119906705b54805cf41be10a855d673b0f0305c028adb55a9450");


        String jwtString = jwtTokenProvider.generateToken(resourceURI);
        System.out.println(jwtString);
        SignedJWT result = jwtTokenProvider.validateSignedJwt(jwtString,"origin", publicKeysByUrl).block();
        assert result != null;
        Assertions.assertEquals(jwtString, result.serialize());
    }

    @Test
    void testInvalidJwt() {
        String invalidJwt = "invalid.jwt.token";
        assertThrows(Exception.class, () -> jwtTokenProvider.validateSignedJwt(invalidJwt,"origin", new HashMap<>()).block());

    }

}
