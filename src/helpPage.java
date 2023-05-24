import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class helpPage extends JFrame {

    JLabel label;
    ImageIcon image;
    Border border;
    JScrollPane scrollPane;

    public helpPage() {
        setTitle("Help");

        image = new ImageIcon(getClass().getResource("/imagens/helpPage.png"));

        border = BorderFactory.createLineBorder(Color.GREEN, 3);

        label = new JLabel("<html><div style='text-align: center; margin-bottom: 10px;'>Queres saber como funciona a nossa aplicação?" +
                "<br>Segue os passos em baixo.</div>" +
                "<div style='text-align: left; font-size: 16px; margin-left: 5px; font-family: Times New Roman;'>" +
                "➠ Executa a nossa app, verás todo o conteúdo da pasta 'FALL-INTO-OBLIVION' no JList à esquerda e à direita o seu interior. Poderás também adicionar mais ficheiros a essa pasta através do JButton 'Introduzir Ficheiro';" +
                "<br>➠ Todos os ficheiros que estão, ou são, introduzidos nessa pasta são automaticamente cifrados com o tipo de cifra AES com comprimento de 256 bytes e o resultado do seu valor de hash (SHA-256), em ficheiros separados;" +
                "<br>" +
                "<br>➠ Podes agora," +
                "<br> Selecionar o Tipo de Cifra que pertendes:" +
                "<br> ↳ AES, Blowfish ou RC4;" +
                "<br> Selecionar o Comprimento da Chave de Cifra que pertendes:" +
                "<br> ↳ 160 bytes, 256 bytes ou 384bytes" +
                "<br> Selecionar a Função de Hash que pertendes:" +
                "<br> ↳ SHA-256, SHA-512 ou MD5;" +
                "<br> " +
                "<br>➠ NOTA: OS PROCESSOS SERÃO SEMPRE AUTOMATICOS A QUALQUER FICHEIRO INTRODUZIDO, MEDIANTE AS OPÇÕES SELECIONADAS NO MOMENTO." +
                "<br> " +
                "<br> " +
                "</div></html>");

        // Define o tamanho preferencial do label
        label.setPreferredSize(new Dimension(696, 800));

        label.setIcon(image);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.TOP);
        label.setForeground(new Color(0x00FF00));
        label.setFont(new Font("Impact", Font.PLAIN, 26));
        label.setIconTextGap(-25);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        label.setBorder(border);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);

        add(label);

        scrollPane = new JScrollPane(label);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 800);

        //setLayout(null);
        setVisible(true);
        //pack();
    }

    public static void main(String[] args) {
        helpPage page = new helpPage();
    }
}
