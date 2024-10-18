package es.in2.desmos.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jca.JCASupport;
import com.nimbusds.jwt.SignedJWT;
import es.in2.desmos.domain.models.TrustedAccessNode;
import es.in2.desmos.domain.models.TrustedAccessNodesList;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    void testValidateSignedJwt() throws JOSEException {

        TrustedAccessNodesList organizations = new TrustedAccessNodesList();
        List<TrustedAccessNode> orgList = new ArrayList<>();
        TrustedAccessNode org = new TrustedAccessNode("test","0x0486573f96a9e5a0007855cba27af53d2d73d69cc143266bc336e361d2f5124f6639c813e62a1c8642132de455b72d65c620f18d69c09e30123d420fcb85de361d","origin", "");
        orgList.add(org);
        organizations.setOrganizations(orgList);

        HashMap<String, String> publicKeysByUrl = new HashMap<>();
        publicKeysByUrl.put(org.getUrl(), org.getPublicKey());


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
