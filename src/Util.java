import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Util {
    // vai gerar uma chave a partir de uma password e um salt SHA : 160, 256, 384
    public static SecretKey getKeyFromPassword(String password, String salt, String tamChave, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = null;
        if (tamChave.equals("160")) {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } else {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA" + tamChave);
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, Integer.parseInt(tamChave));
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), algorithm);
    }
    // gera um salt aleatorio
    public static IvParameterSpec generateIv(int ivSize) {
        byte[] iv = new byte[ivSize];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // encripta um ficheiro
    public static void encryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec[] iv, String algorithm, String hashAlgorithm, String tamChave) {
        try {
            SecretKey key = getKeyFromPassword(password, salt, tamChave, algorithm);
            IvParameterSpec ivO = null;
            // escolhe o iv para cada cifra (AES, Blowfish)
            switch (algorithm) {
                case "AES":
                    algorithm = "AES/CBC/PKCS5Padding";
                    ivO = iv[0];
                    break;
                case "Blowfish":
                    algorithm = "Blowfish/CBC/PKCS5Padding";
                    ivO = iv[1];
                    break;
                case "RC4":
                    algorithm = "RC4";
                    break;
            }
            Cipher cipher = Cipher.getInstance(algorithm);
            // encripta o arquivo sem IV (RC4) ou com IV (AES, Blowfish)
            if (algorithm.equals("RC4")) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key, ivO);
            }
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] buffer = new byte[64];
            int bytesRead;
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null)
                    outputStream.write(output);
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null)
                outputStream.write(outputBytes);
            // calcula o hash do arquivo original
            String hash = Util.hashFile(inputStream, hashAlgorithm);
            // escreve o hash no arquivo criptografado
            String hashFileName = outputFile.getAbsolutePath().substring(0, outputFile.getAbsolutePath().lastIndexOf(".")) + "." + hashAlgorithm;
            File hashFile = new File(hashFileName);
            FileOutputStream hashOutputStream = new FileOutputStream(hashFile);
            hashOutputStream.write(hash.getBytes());
            hashOutputStream.close();
            // deleta o arquivo original
            inputStream.close();
            inputFile.delete();
            outputStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
    // calcula o hash de um arquivo com um algoritmo especifico
    private static String hashFile(FileInputStream inputFile, String algorithm) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] messageDigest = md.digest(inputFile.readAllBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            hash = sb.toString();
        } catch (Exception e) {
            System.out.println(e);
        }

        return hash;
    }

    // Decripta um arquivo e vai buscar o metodo de encriptacao e o tamanho da chave ao nome do arquivo
    public static void decryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec[] iv) {
        String input = inputFile.getName();
        String extension = input.substring(input.lastIndexOf("."));
        // os 3 ultimos caracteres da extensao sao o tamanho da chave

        String tamChave = extension.substring(extension.length() - 3);
        // os outros caracteres sao o algoritmo
        String algorithm = extension.substring(1, extension.length() - 3);
        System.out.println(extension + " " + algorithm + " " + tamChave);
        IvParameterSpec ivO = null;
        try {
            SecretKey key = getKeyFromPassword(password, salt, tamChave, algorithm);
            switch (algorithm) {
                case "AES":
                    algorithm = "AES/CBC/PKCS5Padding";
                    ivO = iv[0];
                    break;
                case "Blowfish":
                    algorithm = "Blowfish/CBC/PKCS5Padding";
                    ivO = iv[1];
                    break;
                case "RC4":
                    algorithm = "RC4";
                    break;
            }

            Cipher cipher = Cipher.getInstance(algorithm);
            // decripta o arquivo
            if (algorithm.equals("RC4")) {
                cipher.init(Cipher.DECRYPT_MODE, key);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key, ivO);
            }
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] buffer = new byte[64];
            int bytesRead;
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null)
                    outputStream.write(output);
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null)
                outputStream.write(outputBytes);
            // deleta o arquivo original
            // deleta o ficheiro .hash
            String hashFileName = outputFile.getAbsolutePath().substring(0, outputFile.getAbsolutePath().lastIndexOf(".")) + ".hash";
            File hashFile = new File(hashFileName);
            hashFile.delete();
            inputStream.close();
            inputFile.delete();
            outputStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }


    }
}
