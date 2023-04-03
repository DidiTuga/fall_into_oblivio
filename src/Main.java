
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        /*
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {}

        JFrame.setDefaultLookAndFeelDecorated(true);
        hub ola = new hub();
        */

        IvParameterSpec iv = AESUtil.generateIv();
        // criar uma pasta chamada "pasta1"
        File pasta1 = new File("FALL-INTO-OBLIVION");
        pasta1.mkdir();
        File[] ficheiros_enc;
        while(true){
            File[] ficheiros = pasta1.listFiles();
            for (File ficheiro : ficheiros) {
                // se o ficheiro for um ficheiro
                if (ficheiro.isFile() && !ficheiro.getName().contains(".enc")) {
                    // mostrar o nome do ficheiro
                    try {
                        System.out.println(ficheiro.getName());
                        // ler o conteudo do ficheiro em bytes
                        String nome = ficheiro.getName();
                        // tirar a extensao do ficheiro
                        if (nome.contains(".")){
                            nome = nome.substring(0, nome.lastIndexOf("."));
                        }
                        // criar um ficheiro com o mesmo nome e a extensao .enc
                        File ficheiro_enc = new File("FALL-INTO-OBLIVION/" + nome + ".enc");
                        AESUtil.encryptFile("password", "salt", ficheiro, ficheiro_enc, iv);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            // esperar 5 segundos
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // ler os ficheiros da pasta em bytes
        //AESUtil.decryptFile("password", "salt", ficheiro, new File("FALL-INTO-OBLIVION/", "text.txt"), iv);

    }
}