package com.fourstory.fourstory_api.security;

import com.fourstory.fourstory_api.utils.KeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class JwtKeyConfig {

    private final JwtProps jwtProps;

    @Bean
    PublicKey publicKey() throws Exception {
        return KeyUtil.loadPublicKey(Path.of(jwtProps.getPublicKeyPath()));
    }

    @Bean
    PrivateKey privateKey() throws Exception {
        return KeyUtil.loadPrivateKey(Path.of(jwtProps.getPrivateKeyPath()));
    }
}
