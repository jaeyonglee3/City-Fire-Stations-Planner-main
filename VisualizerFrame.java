/**
 * Visualizer.java
 * @author Jaeyong Lee, Alan Tang
 * @version 1.0, April 22, 2022
 * The frame class of the visualizer
*/

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class VisualizerFrame extends JFrame {
    private VisualizerPanel panel;
    private final int WIDTH = (int) (getToolkit().getScreenSize().getWidth() / 1.2);
    private final int HEIGHT = (int) (getToolkit().getScreenSize().getHeight() / 1.2);

    /**
     * Visualizer constructor
     */
    public VisualizerFrame()  {
        this.panel = new VisualizerPanel(WIDTH, HEIGHT);
        this.setTitle("Community Fire Station Planner");
        this.getContentPane().add(BorderLayout.CENTER, panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}