/**
 * @file Main.java
 * @brief Aqui é onde a interface gráfica é executada.
 * @version 1.0.0
 * @date 03/04/2023
 * @bugs No known bugs.
 * @author Diogo Santos, a45842
 * @author Luís Sá, a46753
 * @author Luís Santos, a30646
 * @author Tiago Barreiros, a46118
 * @author Xavier Tacanho, a45930
 */

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
        hub ola = new hub();
    }
}