package org.example;
import java.io.*;


public class CommandLineArgs {
        public Operation operation;
        public String inputFile;
        public CipherMode cipherMode;
        public String publicKeyFile;
        public String privateKeyFile;
        public String symmetricKeyFile;
        public String algorithm;
        public String toString(){
            return "";
        }
        // Validation des arguments
        public void validate() throws IllegalArgumentException {
            if (operation == null) {
                throw new IllegalArgumentException("Operation (-encrypt ou -decrypt) requise");
            }

            if (algorithm == null) {
                throw new IllegalArgumentException("Operation (-algo) requise");
            }
            
            if (inputFile == null || inputFile.isEmpty()) {
                throw new IllegalArgumentException("Fichier d'entrée (-file) requis");
            }
            
            if (cipherMode == null) {
                throw new IllegalArgumentException("Mode de chiffrement (-asym ou -cipher) requis");
            }
            
            if (cipherMode == CipherMode.ASYMMETRIC) {
                if (operation == Operation.ENCRYPT && publicKeyFile == null) {
                    throw new IllegalArgumentException("Clé publique (-publicKeyDer) requise pour chiffrement asymétrique");
                }
                if (operation == Operation.DECRYPT && privateKeyFile == null) {
                    throw new IllegalArgumentException("Clé privée (-privateKeyDer) requise pour déchiffrement asymétrique");
                }
            } else if (cipherMode == CipherMode.SYMMETRIC) {
                if (symmetricKeyFile == null) {
                    throw new IllegalArgumentException("Clé symétrique (-cipherKeyDer) requise pour chiffrement symétrique");
                }
            }
            
            // Vérifier que les fichiers existent
            if (!new File(inputFile).exists()) {
                throw new IllegalArgumentException("Fichier d'entrée introuvable: " + inputFile);
            }
            
            if (publicKeyFile != null && !new File(publicKeyFile).exists()) {
                throw new IllegalArgumentException("Fichier clé publique introuvable: " + publicKeyFile);
            }
            
            if (privateKeyFile != null && !new File(privateKeyFile).exists()) {
                throw new IllegalArgumentException("Fichier clé privée introuvable: " + privateKeyFile);
            }
            
            if (symmetricKeyFile != null && !new File(symmetricKeyFile).exists()) {
                throw new IllegalArgumentException("Fichier clé symétrique introuvable: " + symmetricKeyFile);
            }
        }
    }
