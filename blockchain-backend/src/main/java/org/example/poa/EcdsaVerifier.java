// src/main/java/org/example/poa/EcdsaVerifier.java
package org.example.poa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.security.Signature;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EcdsaVerifier {

    private final List<PublicKey> trustedPublicKeys;

    /**
     * Проверяет, что хоть один из публичных ключей (trustedPublicKeys)
     * может верифицировать подпись signatureBytes для data.
     */
    public boolean verify(byte[] data, byte[] signatureBytes) {
        for (PublicKey pub : trustedPublicKeys) {
            try {
                // Можно просто Signature.getInstance("SHA256withECDSA"),
                // но чтобы гарантированно использовать BC, указываем провайдера:
                Signature sig = Signature.getInstance("SHA256withECDSA", "BC");
                sig.initVerify(pub);
                sig.update(data);
                if (sig.verify(signatureBytes)) {
                    return true;
                }
            } catch (Exception ignored) {
                // если эта пара (ключ+сигнатура) не прошла — пробуем следующий ключ
            }
        }
        return false;
    }
}
