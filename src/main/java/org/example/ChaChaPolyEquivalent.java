package org.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class ChaChaPolyEquivalent {
    // Classe pour ChaCha20-Poly1305 (équivalent ChaChaPoly de CryptoKit)

        /**
         * Classe pour représenter une SealedBox (équivalent ChaChaPoly.SealedBox)
         */
        public static class SealedBox {
            public final byte[] nonce;      // 12 bytes
            public final byte[] ciphertext;
            public final byte[] tag;        // 16 bytes

            public SealedBox(byte[] nonce, byte[] ciphertext, byte[] tag) {
                this.nonce = nonce;
                this.ciphertext = ciphertext;
                this.tag = tag;
            }

            /**
             * Constructeur depuis données combinées (équivalent ChaChaPoly.SealedBox(combined:))
             * Format: nonce (12) + ciphertext + tag (16)
             */
            public SealedBox(byte[] combinedData) throws Exception {
                if (combinedData.length < 28) {
                    throw new IllegalArgumentException("Données combinées trop courtes");
                }

                this.nonce = new byte[12];
                System.arraycopy(combinedData, 0, this.nonce, 0, 12);

                this.tag = new byte[16];
                System.arraycopy(combinedData, combinedData.length - 16, this.tag, 0, 16);

                this.ciphertext = new byte[combinedData.length - 28];
                System.arraycopy(combinedData, 12, this.ciphertext, 0, this.ciphertext.length);
            }

            public byte[] getCombined() {
                byte[] combined = new byte[nonce.length + ciphertext.length + tag.length];
                System.arraycopy(nonce, 0, combined, 0, nonce.length);
                System.arraycopy(ciphertext, 0, combined, nonce.length, ciphertext.length);
                System.arraycopy(tag, 0, combined, nonce.length + ciphertext.length, tag.length);
                return combined;
            }
        }

        /**
         * Équivalent de ChaChaPoly.open() pour déchiffrer
         * @param sealedBox La boîte scellée contenant les données chiffrées
         * @param symmetricKey La clé symétrique
         * @return Les données déchiffrées
         */
        public static byte[] open(SealedBox sealedBox, SecretKey symmetricKey) throws Exception {
            // Utiliser ChaCha20-Poly1305
            Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");

            // Créer les paramètres avec le nonce
            AlgorithmParameterSpec params = new IvParameterSpec(sealedBox.nonce);
            cipher.init(Cipher.DECRYPT_MODE, symmetricKey, params);

            // Combiner ciphertext + tag pour le déchiffrement
            byte[] ciphertextWithTag = new byte[sealedBox.ciphertext.length + sealedBox.tag.length];
            System.arraycopy(sealedBox.ciphertext, 0, ciphertextWithTag, 0, sealedBox.ciphertext.length);
            System.arraycopy(sealedBox.tag, 0, ciphertextWithTag, sealedBox.ciphertext.length, sealedBox.tag.length);

            return cipher.doFinal(ciphertextWithTag);
        }

        /**
         * Équivalent de ChaChaPoly.seal() pour chiffrer (bonus)
         * @param plaintext Les données à chiffrer
         * @param symmetricKey La clé symétrique
         * @return Une SealedBox avec les données chiffrées
         */
        public static SealedBox seal(byte[] plaintext, SecretKey symmetricKey) throws Exception {
            // Générer un nonce aléatoire de 12 bytes
            byte[] nonce = new byte[12];
            SecureRandom.getInstanceStrong().nextBytes(nonce);

            // Utiliser ChaCha20-Poly1305
            Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
            AlgorithmParameterSpec params = new IvParameterSpec(nonce);
            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, params);

            byte[] ciphertextWithTag = cipher.doFinal(plaintext);

            // Séparer ciphertext et tag
            byte[] ciphertext = new byte[ciphertextWithTag.length - 16];
            byte[] tag = new byte[16];
            System.arraycopy(ciphertextWithTag, 0, ciphertext, 0, ciphertext.length);
            System.arraycopy(ciphertextWithTag, ciphertext.length, tag, 0, 16);

            return new SealedBox(nonce, ciphertext, tag);
        }
}
