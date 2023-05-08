
import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {}

        JFrame.setDefaultLookAndFeelDecorated(true);
        hub ola = new hub();
    }
}