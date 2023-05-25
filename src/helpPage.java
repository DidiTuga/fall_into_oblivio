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
                "<br>➠ Todos os ficheiros que estão, ou são, introduzidos nesta pasta são automaticamente cifrados com o tipo de cifra AES com comprimento de 256 bytes e o resultado do seu valor de hash (SHA-256), em ficheiros separados;" +
                "<br>" +
                "<br>➠ OS FICHEIROS DE HASH ESTÃO REGISTADOS COM ASSINATURAS DIGITAIS PARA AUMENTAR A SEGURANÇA E INTEGRIDADE DOS DADOS." +
                "<br>" +
                "<br>➠ Podes agora," +
                "<br> Selecionar o Tipo de Cifra que pertendes:" +
                "<br> ↳ AES, Blowfish ou RC4;" +
                "<br> Selecionar o Comprimento da Chave de Cifra que pertendes:" +
                "<br> ↳ 160 bytes, 256 bytes ou 384bytes" +
                "<br> Selecionar a Função de Hash que pertendes:" +
                "<br> ↳ SHA-256, SHA-512 ou MD5;" +
                "<br> " +
                "<br>➠ NOTA: OS PROCESSOS SERÃO SEMPRE AUTOMÁTICOS A QUALQUER FICHEIRO INTRODUZIDO, MEDIANTE AS OPÇÕES SELECIONADAS NO MOMENTO." +
                "<br> " +
                "<br>➠ Para cada ficheiro encriptado foi gerado um PIN numérico aleatório de quatro dígitos! Vais precisar dele para desencriptar;" +
                "<br>➠ Para desencriptar basta selecionar o ficheiro pretendido e clicar no JButton 'Decifrar';" +
                "<br>" +
                "<br>➠ Será pedido para introduzires um PIN, dispões de TRÊS TENTATIVAS, se errares o ficheiro é ELIMINADO PARA SEMPRE;" +
                "<br>" +
                "<br>➠ Se o PIN estiver correto dispenderás, por segurança, de 15s para visualizar e mover o ficheiro decifrado, após esse tempo o ficheiro será encriptado novamente;" +
                "<br>" +
                "<br>NOTA: NESTA NOVA ENCRIPTAÇÃO, O FICHEIRO SERÁ INCRIPTADO RESPEITANDO AS OPÇÕES SELECIONADAS PELO UTILIZADOR E SERÁ GERADO UM NOVO PIN (i.e, se o utilizador tiver selecionado o Tipo de Cifra 'RC4', será essa a cifra a ser implementada);</div></html>");

        // Define o tamanho preferencial do label
        label.setPreferredSize(new Dimension(690, 1300));

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
