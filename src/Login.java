import javax.swing.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Login extends JFrame {

    private JPanel loginPanel;
    private JButton Btn_login;
    private JTextField Txf_nome;
    private JPasswordField Pss_password;

    public Login() {
        setContentPane(loginPanel);
        setTitle("Login");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        // meter centrada
        setLocationRelativeTo(null);
        // quando clicar no botao de login
        Btn_login.addActionListener(e -> {
            // ler o nome e a password
            String nome = Txf_nome.getText();
            String password = new String(Pss_password.getPassword());
            // se o nome e a password forem iguais a "admin"
            if (nome.equals("admin") && password.equals("admin")) {
                // fechar a janela do login
                dispose();
                // abrir a janela do hub
                    hub hub = new hub();

            } else {
                // mostrar uma mensagem de erro
                JOptionPane.showMessageDialog(null, "Nome ou password incorretos");
            }
        });

    }


}
