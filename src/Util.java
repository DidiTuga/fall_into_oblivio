import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
            outputStream.close();
            // calcula o hash do arquivo original
            byte[] hash = Util.hashFile(inputFile, hashAlgorithm);
            // escreve o hash no arquivo criptografado
            String hashFileName = outputFile.getAbsolutePath().substring(0, outputFile.getAbsolutePath().lastIndexOf(".")) + "." + hashAlgorithm;
            File hashFile = new File(hashFileName);
            FileOutputStream hashOutputStream = new FileOutputStream(hashFile);
            hashOutputStream.write(bytesToHex(hash).getBytes());
            hashOutputStream.close();
            // deleta o arquivo original
            inputStream.close();
            inputFile.delete();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    // calcula o hash de um arquivo com um algoritmo especifico
    public static byte[] hashFile(File inputFile, String algorithm) {

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                md.update(buffer, 0, bytesRead);
            }
            inputStream.close();
            return md.digest();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    // Decripta um arquivo e vai buscar o metodo de encriptacao e o tamanho da chave ao nome do arquivo
    public static boolean decryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec[] iv) {
        String input = inputFile.getName();
        String extension = input.substring(input.lastIndexOf("."));
        String nome = input.substring(0, input.lastIndexOf("."));
        // os 3 ultimos caracteres da extensao sao o tamanho da chave

        String tamChave = extension.substring(extension.length() - 3);
        // os outros caracteres sao o algoritmo
        String algorithm = extension.substring(1, extension.length() - 3);
        // ir buscar o hash do arquivo original
        File pasta = inputFile.getParentFile();
        File [] ficheiros = pasta.listFiles();
        File ficheiro_hash = null;
        for (File file : ficheiros){
            if (file.getName().contains(nome) && !file.getName().contains(extension)){
                ficheiro_hash = file;
                break;
            }
        }
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
            // Verifica se o hash do arquivo original e igual ao hash do arquivo decriptado
            String hashAlgorithm = ficheiro_hash.getName().substring(ficheiro_hash.getName().lastIndexOf(".")+1);
            byte[] hash_novo = hashFile(outputFile, hashAlgorithm);
            String hash_novo_string = bytesToHex(hash_novo);
            // ler o hash do arquivo original do ficheiro hash
            BufferedReader br = new BufferedReader(new FileReader(ficheiro_hash));
            String hashOriginal = br.readLine();

            br.close();
            ficheiro_hash.delete();

            // deleta o arquivo original
            inputStream.close();
            inputFile.delete();
            outputStream.close();
            // se os hashes forem diferentes, o arquivo foi alterado
            if (!hash_novo_string.equals(hashOriginal)) {
                outputFile.delete();
                return false;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return true;
    }
    public static String bytesToHex(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
