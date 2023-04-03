import javax.swing.*;

public class hub extends JFrame {

    private JPanel Primeiro;
    private JTextArea textArea1;

    public hub() {
        textArea1 = new JTextArea();
        textArea1.setText("ola");
        setContentPane(Primeiro);
        setTitle("ola");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }
}
