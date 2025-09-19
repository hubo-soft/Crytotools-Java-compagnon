# JAVA_COMPAGNON

A **Java companion tool** to exchange and use cryptographic keys with the iOS app **[Crypto Tools (De/Encryption)](https://apps.apple.com/fr/app/crypto-tools-de-encryption/id1670173533)**.  
It enables **file encryption and decryption** using **Elliptic Curve Cryptography (EC / P-256)** across iOS and desktop environments (**Linux / Windows / macOS**).

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Build](#build)
- [Quick Start](#quick-start)
- [Command Reference](#command-reference)
- [End-to-End Workflow (iOS ↔ Desktop)](#end-to-end-workflow-ios--desktop)
- [Architecture Diagram (ASCII)](#architecture-diagram-ascii)
- [Project Structure](#project-structure)
- [Key & File Conventions](#key--file-conventions)
- [Troubleshooting](#troubleshooting)
- [Security Notes](#security-notes)
- [License](#license)

---

## Prerequisites

- **Java 11+**
- **Maven 3.6+**
- A P-256 key pair in **DER** format (interoperable with the iOS app)

---

## Build

Compile and package with Maven:

```bash
mvn clean package
```

The JAR will be created at:

```text
target/JAVA_COMPAGNON-1.0-SNAPSHOT.jar
```

---

## Quick Start

### Encrypt

```bash
java -cp target/JAVA_COMPAGNON-1.0-SNAPSHOT.jar org.example.Main   -encrypt   -file ../../compagnon-java/Claire.txt   -asym   -publicKeyDer ../../compagnon-java/mac-Key/P256-0D0544294B19-public.der   -privatekeyDer ../../compagnon-java/localKey/private_p256.der   -algorithm EC
```

### Decrypt

```bash
java -cp target/JAVA_COMPAGNON-1.0-SNAPSHOT.jar org.example.Main   -decrypt   -file ../../compagnon-java/Claire.txt.enc   -asym   -publicKeyDer ../../compagnon-java/mac-Key/P256-0D0544294B19-public.der   -privatekeyDer ../../compagnon-java/localKey/private_p256.der   -algorithm P256
```

> **Note:** The `-algorithm` flag accepts `EC` or `P256` (synonymous here). Keep the same curve across encryption/decryption.

---

## Command Reference

```text
Usage:
  org.example.Main [OPERATION] -file <path> -asym     -publicKeyDer <public.der>     -privatekeyDer <private.der>     -algorithm <EC|P256>

Operations:
  -encrypt            Encrypt the input file
  -decrypt            Decrypt the input file

Required flags:
  -file               Path to the input file
  -asym               Use asymmetric (EC P-256) mode
  -publicKeyDer       Path to the receiver's public key (DER)
  -privatekeyDer      Path to your private key (DER)
  -algorithm          Elliptic curve identifier: EC or P256

Outputs:
  Encryption          Produces <input>.enc beside the input file
  Decryption          Produces the original content (same name without .enc)
```

---

## End-to-End Workflow (iOS ↔ Desktop)

This example shows how to exchange encrypted files between **iOS** (Crypto Tools app) and **Desktop** (this Java companion).

### 1) Generate keys on iOS

1. Install **Crypto Tools (De/Encryption)** on iPhone/iPad:  
   👉 https://apps.apple.com/fr/app/crypto-tools-de-encryption/id1670173533
2. In the app, create an **Elliptic Curve P-256** key pair.
3. Export keys in **DER** format:
   - `ios-public.der` (public key — can be shared)
   - `ios-private.der` (private key — **keep secret on iOS**)

### 2) Share the public key to Desktop

- Transfer **`ios-public.der`** to your desktop (AirDrop, email to yourself, secure storage, etc.).
- Keep **`ios-private.der`** on iOS; do not share it.

### 3) Encrypt a file on Desktop (for iOS recipient)

Use the **recipient’s public key** (`ios-public.der`) and **your private key** on desktop:

```bash
java -cp target/JAVA_COMPAGNON-1.0-SNAPSHOT.jar org.example.Main   -encrypt   -file message.txt   -asym   -publicKeyDer ios-public.der   -privatekeyDer desktop-private.der   -algorithm P256
```

This produces:

```text
message.txt.enc
```

### 4) Send the encrypted file back to iOS

- Transfer `message.txt.enc` to the iOS device (AirDrop, Files, email, etc.).
- Open it with **Crypto Tools**.

### 5) Decrypt on iOS

- In **Crypto Tools**, use the **private key stored on iOS** (`ios-private.der`).
- Open `message.txt.enc` and decrypt it to recover `message.txt`.

> You can perform the reverse flow as well (encrypt on iOS with the desktop public key; decrypt on desktop with your desktop private key) using the same flags.

---

## Architecture Diagram (ASCII)

```text
+----------------------+                     +--------------------------+
|       iOS (App)      |                     |        Desktop (Java)    |
|  Crypto Tools (P-256)|                     |   JAVA_COMPAGNON (P-256) |
+----------+-----------+                     +------------+-------------+
           |   Generate EC P-256 key pair (DER)           |
           |----------------------------------------------|
           |                                              |
           | Export PUBLIC KEY (ios-public.der)           |
           |------------------------------------------+   |
           |                                          |   |
           v                                          |   v
   [ios-public.der] -----------------------------> [Use ios-public.der]
                                                     + desktop-private.der
                                                     (encrypt message.txt)
                                                     => message.txt.enc
           ^                                          |
           |                                          |
           |<-----------------------------------------+
           |        Transfer message.txt.enc
           |
   Open in Crypto Tools with ios-private.der (kept on device)
   => Decrypt to original message.txt
```

---

## Project Structure

The main components included in this project:

```text
src/main/java/org/example/
├─ Main.java                    # Entry point (CLI)
├─ CommandLineParser.java       # Parses CLI arguments
├─ CommandLineArgs.java         # Arguments model
├─ OperationManager.java        # Dispatches operations
├─ Operation.java               # Operation types (encrypt/decrypt)
├─ KeyManagerFactory.java       # Key loading and management (DER)
├─ HKDF.java                    # Key derivation (if needed by modes)
├─ ChaChaPolyEquivalent.java    # Symmetric cipher equivalent (internal)
├─ CipherMode.java              # Cipher mode definitions/helpers
```

---

## Key & File Conventions

- **Key Format:** DER (Distinguished Encoding Rules), EC **P-256** curve
- **Public key (DER):** shared with the sender who will encrypt data to you
- **Private key (DER):** kept secret by the owner; used to decrypt
- **Encrypted output:** `<input>.enc` (same directory as input)

---

## Troubleshooting

- **`InvalidKeyException` / `Unsupported curve`:**
  - Ensure `-algorithm` is `EC` or `P256` and both sides use **P-256**.
  - Verify the DER files are indeed EC P-256 keys.
- **`File not found`:**
  - Double-check the paths in `-file`, `-publicKeyDer`, `-privatekeyDer`.
- **Decryption fails / corrupted output:**
  - Make sure you’re using the correct **private key** that matches the recipient side.
  - Confirm the `.enc` file wasn’t truncated during transfer.
- **Java version issues:**
  - Use **Java 11+**. On systems with multiple JDKs, run `java -version` to confirm.

---

## Security Notes

- **Never share your private key.**
- Prefer **secure transfer** methods (AirDrop, encrypted channels).
- Consider using filesystem permissions or a secret manager for key storage.
- Clean up intermediate files if working on shared machines.

---

## License
  MIT

