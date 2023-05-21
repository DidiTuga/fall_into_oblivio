import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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
    private JTextArea Txt_area;
    // ----------------- VARIAVEIS ----------------- //
    private String[] algoritmo_cifras = {"AES", "Blowfish", "RC4"};
    private String[] algoritmo_hash = {"SHA-256", "SHA-512", "MD5"};
    private String[] tamanho_chave = {"160", "256", "384"};

    private final KeyPair kp;

    private int flag_delay = 0;

    public hub() {
        // ----------------- VARIAVEIS -----------------
        // guarda o valor
        String[] valor_selecionado = new String[1];
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
        // Gerar par de chaves RSA
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
            kpg.initialize(2048); // 2048 bits -> 256 bytes
            kp = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // ficheiros cifrados e seus pins
        ArrayList<File> files_crifrados = new ArrayList<>();
        ArrayList<String> filesPin = new ArrayList<>();

        // ----------------- GUI -----------------
        Btn_decipher.setEnabled(false);
        CB_cifra.setModel(new DefaultComboBoxModel(algoritmo_cifras));
        CB_hash.setModel(new DefaultComboBoxModel(algoritmo_hash));
        CB_tamanho.setModel(new DefaultComboBoxModel(tamanho_chave));
        CB_tamanho.setSelectedIndex(1);
        setContentPane(hubInicial);
        setTitle("Hub");
        setSize(500, 500);
        Txt_area.setEditable(false);
        Txt_area.setLineWrap(true);
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
                File[] ficheiros = pasta.listFiles();
                assert ficheiros != null;

                // se o numero de ficheiros cifrados *2 for igual ao numero de ficheiros na pasta entao todos os ficheiros estao cifrados
                int numero_ficheiros = ficheiros.length;
                int n_cifrados = files_crifrados.size();
                if (flag_delay== 1){
                    try {
                        sleep(15000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    flag_delay = 0;
                }
                if ((((n_cifrados * 2))== numero_ficheiros) && (numero_ficheiros != 0)){
                } else {
                    for (File f : ficheiros) {
                        int ponto = f.getName().lastIndexOf(".");
                        int n = f.getName().length();
                        String under = f.getName().substring(ponto, n);
                        // para nao cifrar ficheiros que ja estao cifrados ou se são extensoes de ficheiros hash
                        if (verificaFicheiro(under)) {
                        }
                        // criar o ficheiro.enc e o pin para o ficheiro
                        else {
                            String novoNome = f.getName() + "." + cifra[0] + tam_chave[0];
                            File ficheiro_enc = new File("FALL-INTO-OBLIVION/", novoNome);
                            String pin = String.format("%04d", new Random().nextInt(10000));
                            System.out.println(novoNome + " PIN: " + pin);
                            filesPin.add(pin);
                            files_crifrados.add(ficheiro_enc);
                            // tirar a extensao do ficheiro
                            Util.encryptFile(pin, "salt", f, ficheiro_enc, iv, cifra[0], hash[0], tam_chave[0], kp.getPrivate());
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
        list_ficheiros.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    if (list_ficheiros.getSelectedValue() == null) {
                        Btn_decipher.setEnabled(false);
                        return;
                    }
                    Btn_decipher.setEnabled(true);
                    valor_selecionado[0] = list_ficheiros.getSelectedValue().toString();
                    // ir buscar o conteudo do ficheiro
                    File ficheiro = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
                    try {
                        FileInputStream input = new FileInputStream(ficheiro);
                        byte[] data = new byte[(int) ficheiro.length()];
                        input.read(data);
                        input.close();
                        String conteudo = new String(data, StandardCharsets.UTF_8);
                        Txt_area.setText(conteudo);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        Btn_decipher.addActionListener(e -> {
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
                    File ficheiro_enc = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
                    if (Util.decryptFile(pin, "salt", ficheiro_enc, new File("FALL-INTO-OBLIVION/" + novo_nome), iv, kp.getPublic())){
                        // remover o ficheiro da lista e o pin
                        flag_delay = 1;
                        for (int i = 0; i < files_crifrados.size(); i++) {
                            if (files_crifrados.get(i).getName().equals(valor_selecionado[0])) {
                                files_crifrados.remove(i);
                                filesPin.remove(i);
                            }
                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Ficheiro corrompido!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
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
        // Ir buscar o valor do combobox
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

    /**
     * Função para atualizar a lista de ficheiros apresentados na interface
     * @param file - Pasta onde estão os ficheiros
     */
    public void atualizaLista(File file) {
        File[] ficheiros = file.listFiles();
        DefaultListModel<String> lista_ficheiros = new DefaultListModel<>();
        for (File file1 : ficheiros) {
            lista_ficheiros.addElement(file1.getName());
        }
        list_ficheiros.setModel(lista_ficheiros);
        list_ficheiros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list_ficheiros.setVisibleRowCount(-1);

        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list_ficheiros.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Função para verificar se o ficheiro é ou não para ser cifrado
     * @param extension - extensão do ficheiro
     * @return - false se for para cifrar e true se não for para cifrar
     */
    public boolean verificaFicheiro(String extension) {
        for (String ext : algoritmo_cifras) {
            if (extension.contains(ext)) {
                return true;
            }
        }
        for (String ext : algoritmo_hash) {
            if (extension.contains(ext)) {
                return true;
            }
        }
        return false;
    }

}