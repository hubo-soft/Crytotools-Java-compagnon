package org.example;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

public class OperationManager {
    public static byte[] loadFileAsBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
    public static void saveBytesToFile(String filePath, byte[] data) throws IOException {
        Files.write(Paths.get(filePath), data);
    }


    public static void decryptFileAESGCM(String filePath, PrivateKey privateKey,
                                         PublicKey publicKey, String algorithm) throws Exception {

        // Lire le fichier à chiffrer
        byte[] fileData = Files.readAllBytes(Path.of(filePath));

        // Dériver la clé AES-256 à partir des clés EC/Ed25519/X25519
        byte[] aesKey = deriveAESKey(privateKey, publicKey, algorithm);
        SecretKey symmetricKey = HKDF.hkdfDerivedSymmetricKey(
                aesKey,   // inputKeyMaterial
                new byte[0],    // salt: Data() (vide)
                new byte[0],    // sharedInfo: Data() (vide)
                32              // outputByteCount: 32
        );

        ChaChaPolyEquivalent.SealedBox sealedBox = new ChaChaPolyEquivalent.SealedBox(fileData);

        byte[] decryptedData = ChaChaPolyEquivalent.open(sealedBox, symmetricKey);
        saveBytesToFile(filePath+".dec", decryptedData);
    }

    public static void encryptFileAESGCM(String filePath, PrivateKey privateKey,
                                         PublicKey publicKey, String algorithm) throws Exception {

        // Lire le fichier à chiffrer
        byte[] fileData = Files.readAllBytes(Path.of(filePath));

        // Dériver la clé AES-256 à partir des clés EC/Ed25519/X25519
        byte[] aesKey = deriveAESKey(privateKey, publicKey, algorithm);
        SecretKey symmetricKey = HKDF.hkdfDerivedSymmetricKey(
                aesKey,   // inputKeyMaterial
                new byte[0],    // salt: Data() (vide)
                new byte[0],    // sharedInfo: Data() (vide)
                32              // outputByteCount: 32
        );

        // Générer un IV aléatoire de 12 bytes pour GCM
        byte[] iv = new byte[12];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        byte[] message = loadFileAsBytes(filePath);
        // Chiffrer le message
        ChaChaPolyEquivalent.SealedBox sealedBox = ChaChaPolyEquivalent.seal(
                message,
                symmetricKey
        );

        // Retourner les données combinées
        byte[] result = sealedBox.getCombined();
        saveBytesToFile(filePath+".enc", result);
    }

    private static byte[] deriveAESKey(PrivateKey privateKey, PublicKey publicKey,
                                       String algorithm) throws Exception {

        switch (algorithm.toUpperCase()) {
            case "P256":
            case "SECP256R1":
            case "EC":
                return deriveECDHKey(privateKey, publicKey);

            case "ED25519":
            case "EDDSA":
                // Ed25519 est pour signature, pas pour ECDH
                throw new IllegalArgumentException("Ed25519 ne peut pas être utilisé pour la dérivation de clé");

            case "RSA":
                // Pour RSA, on peut utiliser directement les clés ou faire du RSA-OAEP
                throw new IllegalArgumentException("RSA-AES hybride non implémenté dans cet exemple");

            default:
                throw new IllegalArgumentException("Algorithme non supporté pour la dérivation: " + algorithm);
        }
    }
    private static byte[] deriveECDHKey(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(privateKey);
        ka.doPhase(publicKey, true);
        byte[] sharedSecret = ka.generateSecret();
        System.out.println("KA " + Base64.getEncoder().encodeToString(sharedSecret));
        return sharedSecret;
    }

}
