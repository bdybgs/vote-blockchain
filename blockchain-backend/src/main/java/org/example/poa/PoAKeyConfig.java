// src/main/java/org/example/poa/PoAKeyConfig.java
package org.example.poa;

import lombok.Getter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.security.Security;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Getter
public class PoAKeyConfig {

    /** Инъекция приватного PEM-файла из classpath */
    @Value("${poa.privateKeyPath}")
    private Resource privateKeyResource;

    /** Список публичных PEM-файлов (classpath) */
    @Value("${poa.publicKeys}")
    private List<Resource> publicKeyResources;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Bean
    public PrivateKey nodePrivateKey() throws Exception {
        try (
                Reader reader = new InputStreamReader(privateKeyResource.getInputStream());
                PEMParser parser = new PEMParser(reader)
        ) {
            Object obj = parser.readObject();
            if (obj instanceof PEMKeyPair keyPair) {
                // формат “BEGIN EC PRIVATE KEY”
                PrivateKeyInfo pkInfo = keyPair.getPrivateKeyInfo();
                byte[] privBytes = pkInfo.getEncoded();
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privBytes);
                return KeyFactory.getInstance("EC", "BC").generatePrivate(spec);
            }
            else if (obj instanceof PrivateKeyInfo pkInfo) {
                // формат “BEGIN PRIVATE KEY” (PKCS#8)
                byte[] privBytes = pkInfo.getEncoded();
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privBytes);
                return KeyFactory.getInstance("EC", "BC").generatePrivate(spec);
            } else {
                throw new IllegalArgumentException("Invalid PEM for private key");
            }
        }
    }

    @Bean
    public List<PublicKey> trustedPublicKeys() throws Exception {
        List<PublicKey> keys = new ArrayList<>();
        for (Resource pubRes : publicKeyResources) {
            try (
                    Reader reader = new InputStreamReader(pubRes.getInputStream());
                    PEMParser parser = new PEMParser(reader)
            ) {
                Object obj = parser.readObject();
                if (obj instanceof SubjectPublicKeyInfo spki) {
                    byte[] pubBytes = spki.getEncoded();
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(pubBytes);
                    PublicKey pk = KeyFactory.getInstance("EC").generatePublic(spec);
                    keys.add(pk);
                } else {
                    throw new IllegalArgumentException("Invalid PEM for public key");
                }
            }
        }
        return keys;
    }
}
