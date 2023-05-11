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
    // Generate a 256-bit key from a password and a salt
    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static void encryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // encripta o arquivo
            cipher.init(Cipher.ENCRYPT_MODE, getKeyFromPassword(password, salt), iv);
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
            String hash = Util.hashFile(inputStream, "SHA-256");
            // escreve o hash no arquivo criptografado
            String hashFileName = outputFile.getAbsolutePath().substring(0, outputFile.getAbsolutePath().lastIndexOf(".")) + ".hash";
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

    private static String hashFile(FileInputStream inputFile, String algorithm) {
        String hash = new String();
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte messageDigest[] = md.digest(inputFile.readAllBytes());
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

    // Decrypt a file
    public static void decryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec iv) {

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKeyFromPassword(password, salt), iv);
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
            inputStream.close();
            inputFile.delete();
            outputStream.close();
        } catch (Exception e) {
            System.out.println(e);
        }


    }
}
