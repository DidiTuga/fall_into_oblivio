import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Handler;

import static java.lang.Thread.sleep;

public class hub extends JFrame {
    private JPanel hubInicial;
    private JButton Btn_cipher;
    private JButton Btn_decipher;
    private JTextArea Txt_area;
    private JPanel Panel_list;
    private JList list_ficheiros;
    private JScrollPane Scl_Pane;



    public hub() {
        String[] valor_selecionado = new String[1];
        // criar uma pasta chamada "pasta1"
        File file = new File("FALL-INTO-OBLIVION");
        if (!file.exists()) {
            file.mkdir();
        }


        atualizaLista(file);

        setContentPane(hubInicial);
        setTitle("Hub");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // meter centrada
        setLocationRelativeTo(null);
        setVisible(true);
        Txt_area.setEditable(false);
        Txt_area.setLineWrap(true);
        // ----------------- PROGRAMA -----------------

        IvParameterSpec iv = Util.generateIv();
        ArrayList<File> files_crifrados = new ArrayList<>();
        ArrayList<String> filesPin = new ArrayList<>();
        // criar a thread que vai cifrar os ficheiros

        // THREAD --------------------------
        new Thread(() -> {
            File pasta = new File("FALL-INTO-OBLIVION");
            if (!pasta.exists()) {
                pasta.mkdir();
            }

            while (true) {
                File[] files = pasta.listFiles();
                assert files != null;

                // se o numero de ficheiros cifrados *2 for igual ao numero de ficheiros na pasta entao todos os ficheiros estao cifrados
                if ((files_crifrados.size() * 2) == files.length) {
                } else {
                    for (File f : files) {
                        int ponto = f.getName().lastIndexOf(".");
                        int n = f.getName().length();
                        String under = f.getName().substring(ponto, n);
                        // para nao cifrar ficheiros que ja estao cifrados ou ficheiros .hash
                        if (files_crifrados.contains(f) || under.equals(".hash")) {

                        }
                        // criar o ficheiro.enc e o pin para o ficheiro
                        else {
                            String novoNome = f.getName().substring(0, f.getName().lastIndexOf("."));
                            novoNome += ".enc";
                            File ficheiro_enc = new File("FALL-INTO-OBLIVION/", novoNome);
                            String pin = String.format("%04d", new Random().nextInt(10000));
                            filesPin.add(pin);
                            files_crifrados.add(ficheiro_enc);
                            // tirar a extensao do ficheiro
                            Util.encryptFile(pin, "salt", f, ficheiro_enc, iv);
                            atualizaLista(pasta);
                        }
                    }
                }
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        // -----------------------------------

        // quando clicar num componente da lista
        list_ficheiros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                valor_selecionado[0] = list_ficheiros.getSelectedValue().toString();
                File ficheiro = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
                // colocar o conteudo do ficheiro na text area
                try {
                    String txt;
                    FileInputStream fis = new FileInputStream(ficheiro);
                    byte[] data = new byte[(int) ficheiro.length()];
                    fis.read(data);
                    fis.close();
                    txt = new String(data, StandardCharsets.UTF_8);
                    Txt_area.setText(txt);
                    // tirar o selecionado da lista

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        Btn_decipher.addActionListener(e -> {
            // AQUI É A PARTE DO LUIS SANTOS
            // Ler PIN
            // Verificar se o PIN está correto maximo de 3 tentativas
            // Se estiver correto desencriptar o ficheiro
            // Ao fim das 3 tentativas eliminar o ficheiro
            // Se o ficheiro for desencriptado com sucesso, apagar o ficheiro .enc e o .hash
            // Ao fim das 3 tentativas apagar o ficheiro .enc e o .hash
            int count = 0;
            String pin_cor = null;
            for ( File file_enc : files_crifrados) {
                if (file_enc.getName().equals(valor_selecionado[0])) {
                    pin_cor = filesPin.get(files_crifrados.indexOf(file_enc));
                    //JOptionPane.showMessageDialog(null, "PIN correto!");
                }
            }
            while (count < 3) {
                String pin = JOptionPane.showInputDialog("Introduza o PIN");
                if (pin.equals(pin_cor)) {
                    JOptionPane.showMessageDialog(null, "PIN correto!");
                    String novo_nome = valor_selecionado[0].substring(0, valor_selecionado[0].lastIndexOf("."));
                    novo_nome += ".txt";
                    Util.decryptFile(pin, "salt", new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]), new File("FALL-INTO-OBLIVION/" + novo_nome), iv);

                    break;
                } else {

                    if (count == 2){
                        JOptionPane.showMessageDialog(null, "PIN incorreto! \n Ficheiro eliminado!");
                        File fich_enc = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
                        String hashFileName = fich_enc.getAbsolutePath().substring(0, fich_enc.getAbsolutePath().lastIndexOf(".")) + ".hash";
                        File hashFile = new File(hashFileName);
                        fich_enc.delete();
                        hashFile.delete();
                        break;
                    }else {
                        count ++;
                        JOptionPane.showMessageDialog(null, "PIN incorreto! Tente novamente!");
                    }
                }
            }
            atualizaLista(file);
        });

    }

    public void atualizaLista(File file) {
        File[] ficheiros = file.listFiles();
        DefaultListModel<String> lista_ficheiros = new DefaultListModel<>();
        for (File file1 : ficheiros) {
            lista_ficheiros.addElement(file1.getName());
        }

        // adicionar o modelo a uma scroll bar

        list_ficheiros.setModel(lista_ficheiros);
        // meter o conteudo da lista centrado
        list_ficheiros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list_ficheiros.setVisibleRowCount(-1);
        // centralizar o conteudo da lista
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list_ficheiros.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

}

