// src/main/java/org/example/poa/EcdsaSigner.java
package org.example.poa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.Signature;

@Component
@RequiredArgsConstructor
public class EcdsaSigner {

    private final PrivateKey nodePrivateKey;

    /**
     * Подписывает data алгоритмом SHA256withECDSA через провайдер BC.
     */
    public byte[] sign(byte[] data) throws Exception {
        Signature signer = Signature.getInstance("SHA256withECDSA", "BC");
        signer.initSign(nodePrivateKey);
        signer.update(data);
        return signer.sign();
    }
}
