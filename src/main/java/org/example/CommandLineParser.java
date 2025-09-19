package org.example;

public class CommandLineParser {
       
        public static CommandLineArgs parse(String[] args) throws IllegalArgumentException {
            CommandLineArgs cmdArgs = new CommandLineArgs();
            
            for (int i = 0; i < args.length; i++) {
                switch (args[i].toLowerCase()) {
                    case "-encrypt":
                        cmdArgs.operation = Operation.ENCRYPT;
                        break;
                        
                    case "-decrypt":
                        cmdArgs.operation = Operation.DECRYPT;
                        break;
                        
                    case "-file":
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("Nom de fichier manquant après -file");
                        }
                        cmdArgs.inputFile = args[++i];
                        break;
                        
                    case "-asym":
                        cmdArgs.cipherMode = CipherMode.ASYMMETRIC;
                        break;
                        
                    case "-cipher":
                        cmdArgs.cipherMode = CipherMode.SYMMETRIC;
                        break;
                        
                    case "-publickeyder":
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("Nom de fichier manquant après -publicKeyDer");
                        }
                        cmdArgs.publicKeyFile = args[++i];
                        break;
                        
                    case "-privatekeyder":
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("Nom de fichier manquant après -privateKeyDer");
                        }
                        cmdArgs.privateKeyFile = args[++i];
                        break;

                    case "-algorithm":
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("Nom de algorithme manquant après -algorithm");
                        }
                        cmdArgs.algorithm = args[++i];
                        break;

                    case "-cipherkeyder":
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("Nom de fichier manquant après -cipherKeyDer");
                        }
                        cmdArgs.symmetricKeyFile = args[++i];
                        break;
                        
                    case "-h":
                    case "-help":
                    case "--help":
                        printUsage();
                        System.exit(0);
                        break;
                        
                    default:
                        throw new IllegalArgumentException("Option inconnue: " + args[i]);
                }
            }
            
            cmdArgs.validate();
            return cmdArgs;
        }
        
        public static void printUsage() {
            System.out.println("\n=== CryptoManager - Outil de chiffrement/déchiffrement ===\n");
            System.out.println("Usage:");
            System.out.println("  java CryptoManager [OPTIONS]\n");
            
            System.out.println("Options obligatoires:");
            System.out.println("  -encrypt              Chiffrer le fichier");
            System.out.println("  -decrypt              Déchiffrer le fichier");
            System.out.println("  -file <filename>      Fichier d'entrée à traiter");
            System.out.println("  -asym                 Mode chiffrement asymétrique");
            System.out.println("  -cipher               Mode chiffrement symétrique\n");
            
            System.out.println("Options pour chiffrement asymétrique:");
            System.out.println("  -publicKeyDer <file>  Fichier clé publique DER (pour chiffrement)");
            System.out.println("  -privateKeyDer <file>    Fichier clé privée DER (pour déchiffrement)\n");
            
            System.out.println("Options pour chiffrement symétrique:");
            System.out.println("  -cipherKeyDer <file>  Fichier clé symétrique DER (AES)\n");
            
            System.out.println("Exemples:");
            System.out.println("  # Chiffrement asymétrique RSA");
            System.out.println("  java CryptoManager -encrypt -file document.txt -asym -publicKeyDer public.der");
            System.out.println("  java CryptoManager -decrypt -file document.txt.enc -asym -privateKeyDer private.der\n");
            
            System.out.println("  # Chiffrement symétrique AES");
            System.out.println("  java CryptoManager -encrypt -file document.txt -cipher -cipherKeyDer aes_key.der");
            System.out.println("  java CryptoManager -decrypt -file document.txt.enc -cipher -cipherKeyDer aes_key.der\n");
            
            System.out.println("  # Chiffrement asymétrique EC (courbes elliptiques)");
            System.out.println("  java CryptoManager -encrypt -file document.txt -asym -publicKeyDer ec_public.der");
            System.out.println("  java CryptoManager -decrypt -file document.txt.enc -asym -privateKeyDer ec_private.der\n");
        }
}
