import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESUtil {
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

    public static void encryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec iv)
            throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
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
        inputStream.close();
        outputStream.close();

    }
    // Decrypt a file
    public static void decryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec iv)
            throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
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
        outputStream.close();

    }
}
