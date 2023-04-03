
import javax.crypto.spec.IvParameterSpec;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        /*
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {}

        JFrame.setDefaultLookAndFeelDecorated(true);
        hubWindow ola = new hubWindow();
        */
        IvParameterSpec iv = AESUtil.generateIv();
        // criar uma pasta chamada "pasta1"
        File pasta1 = new File("FALL-INTO-OBLIVION");
        pasta1.mkdir();


        // ler os ficheiros da pasta em bytes
        File[] ficheiros = pasta1.listFiles();
        for (File ficheiro : ficheiros) {
            // se o ficheiro for um ficheiro
            if (ficheiro.isFile()) {
                // mostrar o nome do ficheiro
                try {
                    System.out.println(ficheiro.getName());
                    // ler o conteudo do ficheiro em bytes
                    AESUtil.encryptFile("password", "salt", ficheiro, new File("FALL-INTO-OBLIVION/", "text.enc"), iv);

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        File[] ficheiros2 = pasta1.listFiles();
        for (File ficheiro : ficheiros2) {
            // se o ficheiro for um ficheiro
            if (ficheiro.getName().contains(".enc")) {
                // mostrar o nome do ficheiro
                try {
                    System.out.println(ficheiro.getName());
                    // ler o conteudo do ficheiro em bytes
                    AESUtil.decryptFile("password", "salt", ficheiro, new File("FALL-INTO-OBLIVION/", "text.txt"), iv);

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


}