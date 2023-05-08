import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

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
        IvParameterSpec iv = CipherUtil.generateIv();
        // gerar um pin com 4 digitos
        String pin = String.format("%04d", new Random().nextInt(10000));
        // print do pin no janela
        JOptionPane.showMessageDialog(null, "O teu pin é: " + pin);

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
        Btn_cipher.addActionListener(e -> {

            File ficheiro = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
            list_ficheiros.clearSelection();
            String novoNome = ficheiro.getName();
            // tirar a extensao do ficheiro
            if (novoNome.contains(".")) {
                novoNome = novoNome.substring(0, novoNome.lastIndexOf("."));
            }

            novoNome += ".enc";
            File ficheiro_enc = new File("FALL-INTO-OBLIVION/", novoNome);

            CipherUtil.encryptFile(pin, "salt", ficheiro, ficheiro_enc, iv);
            JOptionPane.showMessageDialog(null, "Ficheiro encriptado com sucesso!");

            atualizaLista(file);
        });

        Btn_decipher.addActionListener(e -> {
            File ficheiro = new File("FALL-INTO-OBLIVION/" + valor_selecionado[0]);
            String novoNome = ficheiro.getName();
            // tirar a extensao do ficheiro
            if (novoNome.contains(".")) {
                novoNome = novoNome.substring(0, novoNome.lastIndexOf("."));
            }
            novoNome += ".txt";
            File ficheiro_dec = new File("FALL-INTO-OBLIVION/" + novoNome);

            CipherUtil.decryptFile(pin, "salt", ficheiro, ficheiro_dec, iv);
            JOptionPane.showMessageDialog(null, "Ficheiro desencriptado com sucesso!");

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

