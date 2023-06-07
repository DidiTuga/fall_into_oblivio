import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

public class Util {
    /**
     * Função para gerar um salt aleatório
     @param nome - nome do arquivo a ser encriptado que vai ser usado como hash do random
     @return salt - salt gerado
     */
    public static String gerarStringRandom(String nome) {
        Random random = new Random(nome.hashCode());
        StringBuilder sb = new StringBuilder(nome);

        // Concatena uma sequência de caracteres aleatórios
        for (int i = 0; i < 10; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a');
            sb.append(randomChar);
        }

        return sb.toString();
    }

    /**
     * Função para gerar uma chave a partir de uma password e um salt
     *
     * @param password  - password para gerar a chave
     * @param salt      - salt para gerar a chave
     * @param tamChave  - tamanho da chave
     * @param algorithm - algoritmo de encriptacao
     * @return SecretKey - chave gerada
     */
    public static SecretKey getKeyFromPassword(String password, String salt, String tamChave, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = null;
        if (tamChave.equals("160")) {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } else {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA" + tamChave);
        }
        int tamanho = Integer.parseInt(tamChave);
        if(tamanho == 160){
            tamanho = 128;
        }
        if (tamanho == 384) {
            tamanho = 256;
        }

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, tamanho);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), algorithm);
    }

    /**
     * Função para gerar um IV aleatório
     *
     * @param ivSize - tamanho do iv
     * @return iv - iv gerado
     */
    public static IvParameterSpec generateIv(int ivSize) {
        byte[] iv = new byte[ivSize];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Função para assinar um hash com uma chave privada
     *
     * @param hash    - hash a ser assinado
     * @param privada - chave privada
     * @return assinatura - assinatura do hash
     * @throws NoSuchAlgorithmException - algoritmo de assinatura não existe
     * @throws InvalidKeyException      - chave privada inválida
     * @throws SignatureException       - assinatura inválida
     */
    private static byte[] assinar(byte[] hash, PrivateKey privada) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature assinatura = Signature.getInstance("SHA256withDSA");
        assinatura.initSign(privada);
        assinatura.update(hash);
        return assinatura.sign();
    }

    /**
     * Função para verificar a assinatura de um hash com uma chave pública
     *
     * @param hash       - hash a ser verificado
     * @param assinatura - assinatura do hash
     * @param publica    - chave pública
     * @return boolean - true se a assinatura for válida, false caso contrário
     * @throws NoSuchAlgorithmException - algoritmo de assinatura não existe
     * @throws InvalidKeyException      - chave pública inválida
     * @throws SignatureException       - assinatura inválida
     */
    private static boolean verificar(byte[] hash, byte[] assinatura, PublicKey publica)  {
        try {
            Signature verificacao = Signature.getInstance("SHA256withDSA");
            verificacao.initVerify(publica);
            verificacao.update(hash);
            return verificacao.verify(assinatura);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            return false;
        }
    }

    /**
     * Função para encriptar um arquivo com uma chave simétrica
     * Criando um arquivo com o hash do arquivo original e assinando o hash com uma chave privada
     * Cifras: AES, Blowfish, RC4
     * tamanhos de chave: 160, 256, 384
     *
     * @param password      - password para gerar a chave
     * @param salt          - salt para gerar a chave
     * @param inputFile     - arquivo a ser encriptado
     * @param outputFile    - arquivo encriptado
     * @param iv            - iv para cada cifra (AES, Blowfish)
     * @param algorithm     - algoritmo de encriptacao
     * @param hashAlgorithm - algoritmo de hash
     * @param tamChave      - tamanho da chave
     * @param privada       - chave privada
     */
    public static void encryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec[] iv, String algorithm, String hashAlgorithm, String tamChave, PrivateKey privada) {
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
            byte[] hash = Util.hashFile(inputFile.toPath(), hashAlgorithm);
            hash = assinar(hash, privada);
            // escreve o hash no arquivo criptografado
            String hashFileName = outputFile.getAbsolutePath().substring(0, outputFile.getAbsolutePath().lastIndexOf(".")) + "." + hashAlgorithm;
            File hashFile = new File(hashFileName);
            FileOutputStream hashOutputStream = new FileOutputStream(hashFile);
            hashOutputStream.write(hash);
            hashOutputStream.close();
            // deleta o arquivo original
            inputStream.close();
            inputFile.delete();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Função para calcular o hash de um arquivo
     * Hash: SHA-256, SHA-512, MD5
     *
     * @param inputFile - arquivo a ser calculado o hash
     * @param algorithm - algoritmo de hash
     * @return hash - hash do arquivo
     */
    public static byte[] hashFile(Path inputFile, String algorithm) {

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] byts = Files.readAllBytes(inputFile);
            byte[] hash = md.digest();
            return hash;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Função para decriptar um arquivo com uma chave simétrica e verificar a integridade do arquivo com a assinatura do hash
     *
     * @param password   - password para gerar a chave
     * @param salt       - salt para gerar a chave
     * @param inputFile  - arquivo a ser decriptado
     * @param outputFile - arquivo decriptado
     * @param iv         - iv para cada cifra (AES, Blowfish)
     * @param publica    - chave pública
     * @return boolean - true se a assinatura for válida, false caso contrário
     */

    public static boolean decryptFile(String password, String salt, File inputFile, File outputFile, IvParameterSpec[] iv, PublicKey publica)throws IOException {
        String input = inputFile.getName();
        String extension = input.substring(input.lastIndexOf("."));
        String nome = input.substring(0, input.lastIndexOf("."));
        // os 3 ultimos caracteres da extensao sao o tamanho da chave

        String tamChave = extension.substring(extension.length() - 3);
        // os outros caracteres sao o algoritmo
        String algorithm = extension.substring(1, extension.length() - 3);
        // ir buscar o hash do arquivo original
        File pasta = inputFile.getParentFile();
        File[] ficheiros = pasta.listFiles();
        File ficheiro_hash = null;
        for (File file : ficheiros) {
            if (file.getName().contains(nome) && !file.getName().contains(extension)) {
                ficheiro_hash = file;
                break;
            }
        }
        IvParameterSpec ivO = null;
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
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
            inputStream = new FileInputStream(inputFile);
            byte[] buffer = new byte[64];
            int bytesRead;
            outputStream = new FileOutputStream(outputFile);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null)
                    outputStream.write(output);
            }
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null)
                outputStream.write(outputBytes);
            // Verifica se o hash do arquivo original e igual ao hash do arquivo decriptado
            String hashAlgorithm = ficheiro_hash.getName().substring(ficheiro_hash.getName().lastIndexOf(".") + 1);
            byte[] hash_novo = hashFile(outputFile.toPath(), hashAlgorithm);

            // ler o hash do arquivo original do ficheiro hash
            byte[] hashOriginal = Files.readAllBytes(ficheiro_hash.toPath());
            ficheiro_hash.delete();

            // deleta o arquivo original
            inputStream.close();
            inputFile.delete();
            outputStream.close();
            // se os hashes forem diferentes, o arquivo foi alterado
            boolean bool = verificar(hash_novo, hashOriginal, publica);
            if (!bool) {
                outputFile.delete();
                System.out.println("O Ficheiro foi alterado");
                return false;
            }
        } catch (IllegalBlockSizeException e) {
            inputStream.close();
            inputFile.delete();
            outputStream.close();
            outputFile.delete();
            ficheiro_hash.delete();
            System.out.println("O Ficheiro foi alterado");
            return false;

        } catch (Exception e) {
            System.out.println("Erro ao decriptar o ficheiro");
            System.out.println(e);
        }
        return true;
    }

    /**
     * Função para copiar um arquivo para uma pasta
     * @param origemPath caminho do arquivo a ser copiado
     * @throws IOException pode lançar uma exceção caso o arquivo não exista ou não seja um arquivo válido
     */

    public static void copiarArquivos(Path origemPath) throws IOException {
        File pasta = new File("FALL-INTO-OBLIVION");

        Path destinoPath = pasta.toPath();

        if (Files.exists(origemPath) && Files.isRegularFile(origemPath)) {
            Path targetPath = destinoPath.resolve(origemPath.getFileName());
            Files.copy(origemPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new IOException("O ficheiro de origem não existe ou não é um arquivo válido.");
        }
    }
}