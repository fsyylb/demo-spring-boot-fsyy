package com.fsyy.auth.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Test {
    private static JWTClaimsSet getJWTClaimsSet(String clientId) {

        List<String> aud = new ArrayList<>();
        aud.add("http://127.0.0.1:6004");
        aud.add("http://127.0.0.1:6004/oauth2/token");
        aud.add("http://127.0.0.1:6004/oauth2/introspect");
        aud.add("http://127.0.0.1:6004/oauth2/revoke");

        // 前四个属性是必须的（iss、sub、aud、exp），参考JwtClientAssertionDecoderFactory#defaultJwtValidatorFactory
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                // 发行者：固定clientId
                .issuer(clientId)
                // 主体：固定clientId
                .subject(clientId)
                // 授权服务器的相关地址
                .audience(aud)
                // 过期时间 24h
                .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                // 访问时间
                .issueTime(new Date())
                // 范围
                .claim("scope", new String[]{"client.create"})
                .claim("jwk-set-url", "http://127.0.0.1:8089/client/jwks")
                .build();
        return claimsSet;
    }


    public static String hmacSign(String clientSecret, JWTClaimsSet claimsSet) throws JOSEException {
        JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;


        OctetSequenceKey key = new OctetSequenceKey.Builder(clientSecret.getBytes(StandardCharsets.UTF_8))
                .keyID(UUID.randomUUID().toString())
                .build();

        // 参考：DefaultJWSSignerFactory
        JWSSigner signer = new MACSigner(key);
        // Apply JCA context
        JCAContext jcaContext = new JCAContext();
        signer.getJCAContext().setSecureRandom(jcaContext.getSecureRandom());
        signer.getJCAContext().setProvider(jcaContext.getProvider());

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet);
        signedJWT.sign(signer);
        String token = signedJWT.serialize();
        return token;
    }

    public static void main(String[] args) throws JOSEException {
        String clientSecret = "b06c75b78d1701ff470119a4114f8b90";
        String jwt = Test.hmacSign(clientSecret, getJWTClaimsSet("oidc-client-two"));
        System.out.println(jwt);
    }
}
