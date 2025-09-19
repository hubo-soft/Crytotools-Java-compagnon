package org.example;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.security.PrivateKey;

public class KeyManagerFactory {
    public static PublicKey loadPublicKey(String filePath, String keyType) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Path.of(filePath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

        KeyFactory kf;

        switch (keyType.toUpperCase()) {
            case "P256":
            case "SECP256R1":
            case "EC":
                kf = KeyFactory.getInstance("EC");
                break;

            case "ED25519":
            case "EDDSA":
                kf = KeyFactory.getInstance("Ed25519");
                break;

            default:
                throw new IllegalArgumentException("Type de clé non supporté: " + keyType +
                        ". Types supportés: P256, ED25519");
        }

        return kf.generatePublic(spec);
    }

    public static PrivateKey loadPrivateKey(String filePath, String keyType) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Path.of(filePath));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory kf;

        switch (keyType.toUpperCase()) {
            case "P256":
            case "SECP256R1":
            case "EC":
                kf = KeyFactory.getInstance("EC");
                break;

            case "ED25519":
            case "EDDSA":
                kf = KeyFactory.getInstance("Ed25519");
                break;

            default:
                throw new IllegalArgumentException("Type de clé non supporté: " + keyType +
                        ". Types supportés: P256, ED25519");
        }

        return kf.generatePrivate(spec);
    }

}
