import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Calculo_MAC {


    //Função para calcular o MAC de um ficheiro
    public static String calcula_MAC (String ficheiro, String algoritmo) throws NoSuchAlgorithmException, InvalidKeyException, Exception{

        Scanner scanner = null;
        String macHex = null;

        try {

            // Criando um objeto File
            File conteudo = new File(ficheiro);

            // Criando um objeto Scanner para ler o arquivo
            scanner = new Scanner(conteudo);

            // Lendo o conteúdo do arquivo linha por linha
            StringBuilder contentBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                contentBuilder.append(line).append("\n");
            }

        //Ler conteúdo do ficheiro
        String conteudo_lido = contentBuilder.toString();
        System.out.println("\nConteúdo lido do arquivo:\n" + conteudo_lido);
        System.out.print("-------------------------------------------------------------------------\n");

        // Gera a chave aleatória para o algoritmo HMAC-SHA256
        KeyGenerator keyGen = KeyGenerator.getInstance(algoritmo);
        SecretKey chave = keyGen.generateKey();

        // Cria um objeto Mac com o algoritmo HMAC-SHA256 e a chave gerada
        Mac mac = Mac.getInstance(algoritmo);
        mac.init(chave);

        // Cria um objeto Cipher com o algoritmo AES e modo de operação ECB
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        // Cria uma chave para o algoritmo AES a partir da chave HMAC gerada anteriormente
        SecretKeySpec chaveAES = new SecretKeySpec(chave.getEncoded(), "AES");

        // Inicializa o Cipher com a chave AES
        cipher.init(Cipher.ENCRYPT_MODE, chaveAES);

        // Criptografa o conteudo lido
        byte[] criptograma = cipher.doFinal(conteudo_lido.getBytes());

        System.out.print("Ficheiro criptografado:\n" + criptograma + "\n\n");
        System.out.print("-------------------------------------------------------------------------\n");

        // Calcula o MAC do criptograma
        byte[] macBytes = mac.doFinal(criptograma);

        // Converte o MAC em hexadecimal
        StringBuilder sb = new StringBuilder();
        for (byte b : macBytes) {
            sb.append(String.format("%02x", b));
        }

        macHex = sb.toString();   
            
        } catch (FileNotFoundException e) {
            System.out.println("Ficheiro não encontrado: " + e.getMessage());
        } finally {
            // Fechar o scanner no bloco finally depois de finalizar todas as tarefas
            if (scanner != null) {
                scanner.close();
            }
        }
        
        return macHex;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, Exception {

        String MAC = calcula_MAC("teste.txt", "HmacSHA256");

        // Imprime o MAC em hexadecimal
        System.out.println("MAC:\n" + MAC + "\n");
    }
}