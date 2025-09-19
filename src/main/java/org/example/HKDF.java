package org.example;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class HKDF {
    /**
     * Équivalent de sharedSecret.hkdfDerivedSymmetricKey() avec SHA-512
     * @param inputKeyMaterial Le secret partagé (équivalent sharedSecret)
     * @param salt Le sel (peut être vide)
     * @param info Les informations partagées (peut être vide)
     * @param outputByteCount Nombre de bytes en sortie (32 pour une clé 256-bit)
     * @return La clé symétrique dérivée
     */
    public static SecretKey hkdfDerivedSymmetricKey(byte[] inputKeyMaterial,
                                                    byte[] salt,
                                                    byte[] info,
                                                    int outputByteCount) throws Exception {

        // Étape 1: Extract - utilise SHA-512 comme dans Swift
        Mac hmac = Mac.getInstance("HmacSHA512");

        // Si pas de sel fourni, utiliser des zéros (comme CryptoKit)
        if (salt == null || salt.length == 0) {
            salt = new byte[hmac.getMacLength()]; // 64 bytes de zéros pour SHA-512
        }

        SecretKeySpec saltKey = new SecretKeySpec(salt, "HmacSHA512");
        hmac.init(new SecretKeySpec(salt, "HmacSHA512"));
        byte[] prk = hmac.doFinal(inputKeyMaterial); // Pseudo-random key

        // Étape 2: Expand
        hmac.init(new SecretKeySpec(prk, "HmacSHA512"));

        if (info == null) {
            info = new byte[0];
        }

        int hashLength = hmac.getMacLength(); // 64 pour SHA-512
        int iterations = (int) Math.ceil((double) outputByteCount / hashLength);
        byte[] okm = new byte[iterations * hashLength];
        byte[] t = new byte[0];

        for (int i = 1; i <= iterations; i++) {
            hmac.reset();
            hmac.update(t);
            hmac.update(info);
            hmac.update((byte) i);
            t = hmac.doFinal();
            System.arraycopy(t, 0, okm, (i - 1) * hashLength, t.length);
        }

        // Tronquer à la longueur désirée
        byte[] derivedKey = new byte[outputByteCount];
        System.arraycopy(okm, 0, derivedKey, 0, outputByteCount);
        String b642 = Base64.getEncoder().encodeToString(derivedKey);
        System.out.println("Derived Key Base64 = " + b642);
        return new SecretKeySpec(derivedKey, "ChaCha20");
    }
}



