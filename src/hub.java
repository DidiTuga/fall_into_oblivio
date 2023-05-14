import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class hub extends JFrame {
    private JPanel hubInicial;
    private JButton Btn_decipher;
    private JPanel Panel_list;
    private JList list_ficheiros;
    private JScrollPane Scl_Pane;
    private JComboBox CB_cifra;
    private JComboBox CB_tamanho;
    private JComboBox CB_hash;


    public hub() {
        // ----------------- VARIAVEIS -----------------
        String[] valor_selecionado = new String[1];
        String[] algoritmo_cifras = {"AES", "Blowfish", "RC4"};
        String[] algoritmo_hash = {"SHA-256", "SHA-512", "MD5"};
        String[] tamanho_chave = {"160", "256", "384"};
        // guarda o valor
        String[] tam_chave = new String[1];
        String[] cifra = new String[1];
        String[] hash = new String[1];
        tam_chave[0] = "256";
        cifra[0] = "AES";
        hash[0] = "SHA-256";
        // iv para cada cifra (AES, Blowfish)
        IvParameterSpec[] iv = new IvParameterSpec[2];
        iv[0] = Util.generateIv(16);
        iv[1] = Util.generateIv(8);
        // ficheiros cifrados e seus pins
        ArrayList<File> files_crifrados = new ArrayList<>();
        ArrayList<String> filesPin = new ArrayList<>();

        // ----------------- GUI -----------------
        CB_cifra.setModel(new DefaultComboBoxModel(algoritmo_cifras));
        CB_hash.setModel(new DefaultComboBoxModel(algoritmo_hash));
        CB_tamanho.setModel(new DefaultComboBoxModel(tamanho_chave));
        CB_tamanho.setSelectedIndex(1);
        setContentPane(hubInicial);
        setTitle("Hub");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // meter centrada
        setLocationRelativeTo(null);
        setVisible(true);
        // ----------------- PROGRAMA -----------------
        // criar a thread que vai cifrar os ficheiros
        // THREAD --------------------------
        new Thread(() -> {
            File pasta = new File("FALL-INTO-OBLIVION");
            if (!pasta.exists()) {
                pasta.mkdir();
            }
            atualizaLista(pasta);

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
                        if (!under.equals(".txt")) {

                        }
                        // criar o ficheiro.enc e o pin para o ficheiro
                        else {
                            String novoNome = f.getName().substring(0, f.getName().lastIndexOf("."));
                            novoNome += "." + cifra[0] + tam_chave[0];
                            File ficheiro_enc = new File("FALL-INTO-OBLIVION/", novoNome);
                            String pin = String.format("%04d", new Random().nextInt(10000));
                            System.out.println(novoNome + " PIN: " + pin);
                            filesPin.add(pin);
                            files_crifrados.add(ficheiro_enc);
                            // tirar a extensao do ficheiro
                            Util.encryptFile(pin, "salt", f, ficheiro_enc, iv, cifra[0], hash[0], tam_chave[0]);
                            atualizaLista(pasta);
                            /*
                            System.out.println("DEBBUG:");
                            System.out.println(files_crifrados.size());
                            System.out.println(filesPin.size());
                            System.out.println(files.length);
                            */
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
        list_ficheiros.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    if (list_ficheiros.getSelectedValue() == null) {
                        return;
                    }
                    valor_selecionado[0] = list_ficheiros.getSelectedValue().toString();
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
            for (File file_enc : files_crifrados) {
                if (file_enc.getName().equals(valor_selecionado[0])) {
                    pin_cor = filesPin.get(files_crifrados.indexOf(file_enc));
                }
            }
            while (count < 3) {
                String pin = JOptionPane.showInputDialog("Introduza o PIN");
                if (pin.equals(pin_cor)) {
                    JOptionPane.showMessageDialog(null, "PIN correto!");
                    String novo_nome = valor_selecionado[0].substring(0, valor_selecionado[0].lastIndexOf("."));
                    novo_nome += ".txt";
                    File ficheiro_enc = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
                    Util.decryptFile(pin, "salt", ficheiro_enc, new File("FALL-INTO-OBLIVION/" + novo_nome), iv);

                    break;
                } else {
                    // se o pin estiver errado 3 vezes elimina os ficheiros
                    if (count == 2) {
                        JOptionPane.showMessageDialog(null, "PIN incorreto! \n Ficheiro eliminado!");
                        File fich_enc = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
                        File pasta = new File("FALL-INTO-OBLIVION");
                        File[] ficheiros = pasta.listFiles();
                        for (File file : ficheiros) {
                            String nome = file.getName().substring(0, file.getName().lastIndexOf("."));
                            if (nome.equals(valor_selecionado[0].substring(0, valor_selecionado[0].lastIndexOf(".")))) {
                                file.delete();
                            }
                        }
                        // remover o ficheiro da lista e o pin
                        for (int i = 0; i < files_crifrados.size(); i++) {
                            if (files_crifrados.get(i).getName().equals(valor_selecionado[0])) {
                                files_crifrados.remove(i);
                                filesPin.remove(i);
                            }
                        }
                        fich_enc.delete();

                        break; // se o pin estiver incorreto 3 vezes o ficheiro é eliminado
                    } else {
                        count++;
                        JOptionPane.showMessageDialog(null, "PIN incorreto! Tente novamente!");
                    }
                }
            }
            File file = new File("FALL-INTO-OBLIVION");
            atualizaLista(file);
        });
        // ----------------------------------- Ir buscar o valor do combobox
        CB_cifra.addActionListener(e -> {
            cifra[0] = CB_cifra.getSelectedItem().toString();
        });
        CB_hash.addActionListener(e -> {
            hash[0] = CB_hash.getSelectedItem().toString();
        });
        CB_tamanho.addActionListener(e -> {
            tam_chave[0] = (CB_tamanho.getSelectedItem().toString());
        });
    }

    // ----------------------------------- Atualizar a lista de ficheiros
    // Vai buscar os ficheiros passa os nomes para uma lista
    // Centraliza o conteudo da lista
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

