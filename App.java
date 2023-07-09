/*
 * [App.java]
 * @author Alan Tang, Jaeyong Lee
 * @version Apr 24, 2022
 * The entry point of the program
 */

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class App {
    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error while setting Look and Feel");
            e.printStackTrace();
        }

        new VisualizerFrame();
    }
}
