package org.example;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;

/*
mvn clean package
java -cp target/JAVA_COMPAGNON-1.0-SNAPSHOT.jar org.example.Main
java -cp target/JAVA_COMPAGNON-1.0-SNAPSHOT.jar org.example.Main -encrypt -file ../../compagnon-java/Claire.txt -asym -publicKeyDer ../../compagnon-java/mac-Key/P256-0D0544294B19-public.der -privatekeyDer ../../compagnon-java/localKey/private_p256.der -algorithm EC
java -cp target/JAVA_COMPAGNON-1.0-SNAPSHOT.jar org.example.Main -decrypt -file ../../compagnon-java/Claire.txt.enc -asym -publicKeyDer ../../compagnon-java/mac-Key/P256-0D0544294B19-public.der -privatekeyDer ../../compagnon-java/localKey/private_p256.der -algorithm P256
*/
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");
        CommandLineArgs cmdArgs = CommandLineParser.parse(args);
        System.out.println(cmdArgs);
        //LOAD KEYs
        PrivateKey privKey = null;
        PublicKey  pubKey  = null;
        //asym key
        if (cmdArgs.cipherMode == CipherMode.ASYMMETRIC ){
            String pubFile = cmdArgs.publicKeyFile;
            String privFile = cmdArgs.privateKeyFile;
            try {
                privKey = KeyManagerFactory.loadPrivateKey(cmdArgs.privateKeyFile, cmdArgs.algorithm);
                pubKey = KeyManagerFactory.loadPublicKey(cmdArgs.publicKeyFile, cmdArgs.algorithm);

                System.out.println("Public Key algo : " + pubKey.getAlgorithm());
                System.out.println("Public Key  : " + pubKey.getParams().toString());
                System.out.println("Private Key algo : " + privKey.getAlgorithm());
                System.out.println("Private Key  : " + privKey.getParams().toString());




            if (cmdArgs.operation == Operation.ENCRYPT) {
                OperationManager.encryptFileAESGCM(cmdArgs.inputFile, privKey, pubKey, cmdArgs.algorithm);
            }
                if (cmdArgs.operation == Operation.DECRYPT) {
                    OperationManager.decryptFileAESGCM(cmdArgs.inputFile, privKey, pubKey, cmdArgs.algorithm);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if  (cmdArgs.cipherMode == CipherMode.SYMMETRIC ) {

        }
    }
}