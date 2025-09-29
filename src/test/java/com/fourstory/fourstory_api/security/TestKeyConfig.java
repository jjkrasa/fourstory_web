package com.fourstory.fourstory_api.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.security.*;

@TestConfiguration
public class TestKeyConfig {

    @Bean
    public KeyPair testKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }

    @Bean
    @Primary
    public PrivateKey privateKey(KeyPair testKeyPair) {
        return testKeyPair.getPrivate();
    }

    @Bean
    @Primary
    public PublicKey publicKey(KeyPair testKeyPair) {
        return testKeyPair.getPublic();
    }
}
