/*
 * [VisualizerPanel.java]
 * @author Alan Tang, Jaeyong Lee
 * @version Apr 24, 2022
 * The panel class of the visualizer
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class VisualizerPanel extends JPanel implements MouseListener, MouseMotionListener {

    private static final int CIRCLE_RADIUS = 50;
    private static final Color SOLVE_BUTTON_COLOR = new Color(144, 238, 144);
    private static final Color EXIT_BUTTON_COLOR = new Color(255, 127, 127);
    private static final Color FIRE_STATION_COLOR = new Color(231, 31, 31);

    private final int PANEL_WIDTH; 
    private final int PANEL_HEIGHT; 

    private FireStationSolver solver;
    private Set<Town> fireStations = new HashSet<>();
    private Map<Town, Set<Town>> mainMap = new HashMap<>();

    private boolean edgeMode = false;
    private Town edgeOriginTown, edgeDestinationTown; // destination town will become a neighbor of the origin town 
    private Point cursorPoint = new Point();

    public VisualizerPanel(int screenWidth, int screenHeight) {
        this.setLayout(null);
        this.PANEL_WIDTH = screenWidth; 
        this.PANEL_HEIGHT = screenHeight; 

        // solve button
        JButton solveButton = new JButton("Solve");
        solveButton.setBounds(7, 25, 140, 30);
        solveButton.setBackground(SOLVE_BUTTON_COLOR);
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solver = new FireStationSolver(mainMap);
                fireStations = solver.solve();

                // set fire stations to have the correct property
                for (Town town : mainMap.keySet()) {
                    if (fireStations.contains(town)) {
                        town.setHasFireStation(true);
                    } else {
                        town.setHasFireStation(false);
                    }
                }
                repaint();
            }
        });

        // clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(7, 55, 140, 30);
        clearButton.setBackground(Color.WHITE);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!mainMap.isEmpty()) {
                    mainMap.clear();
                }
                if (!fireStations.isEmpty()) {
                    fireStations.clear();
                }
                repaint();
            }
        });

        // save to file button
        JButton saveButton = new JButton("Save to file");
        saveButton.setBounds(7, 85, 140, 30);
        saveButton.setBackground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    TownMapSerializer.writeMapToFile(mainMap, selectedFile);
                }
            }
        });

        // open from file button
        JButton openFileButton = new JButton("Open from file");
        openFileButton.setBounds(7, 115, 140, 30);
        openFileButton.setBackground(Color.WHITE);
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    mainMap = TownMapSerializer.readMapFromFile(selectedFile);
                }
                repaint();
            }
        });

        // exit button
        JButton exitButton = new JButton("Quit");
        exitButton.setBounds(7, 145, 140, 30);
        exitButton.setBackground(EXIT_BUTTON_COLOR);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // add buttons to panel
        this.add(solveButton);
        this.add(clearButton);
        this.add(saveButton);
        this.add(openFileButton);
        this.add(exitButton);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setVisible(true);
    }

    private void drawTown(Graphics2D g2d, Town town) {
        g2d.setStroke(new BasicStroke(4.0f)); // makes circles thicker 

        // apply an offset to align by center rather than top-left
        int drawX = (int) town.getOrigin().getX() - CIRCLE_RADIUS / 2;
        int drawY = (int) town.getOrigin().getY() - CIRCLE_RADIUS / 2;

        // Fill background
        if (town.hasFireStation()) {
            g2d.setColor(FIRE_STATION_COLOR);
            g2d.fillOval(drawX, drawY, CIRCLE_RADIUS, CIRCLE_RADIUS);
        } else {
            g2d.setColor(this.getBackground());
            g2d.fillOval(drawX, drawY, CIRCLE_RADIUS, CIRCLE_RADIUS);
        }

        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawOval(drawX, drawY, CIRCLE_RADIUS, CIRCLE_RADIUS);

        // Draw the name string centered in the circle
        FontMetrics fm = g2d.getFontMetrics();
        int stringWidth = fm.stringWidth(town.getName()) / 2;
        int stringHeight = fm.getMaxDescent() / 2;
        g2d.drawString(
                town.getName(),
                drawX + CIRCLE_RADIUS / 2 - stringWidth,
                drawY + CIRCLE_RADIUS / 2 + stringHeight);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);

        /* BEGIN RENDERING */
        // Display currently selected town in edge mode
        if (edgeOriginTown == null) {
            g2d.drawString("Town Selected: None", 10, 20);
        } else {
            g2d.drawString("Town Selected: " + edgeOriginTown.getName(), 10, 20);
        }

        // Render edges
        for (Map.Entry<Town, Set<Town>> entry : mainMap.entrySet()) {
            Town currentTown = entry.getKey();
            Set<Town> neighbours = entry.getValue();

            for (Town neighbour : neighbours) {
                g2d.drawLine((int) currentTown.getOrigin().getX(), (int) currentTown.getOrigin().getY(),
                        (int) neighbour.getOrigin().getX(), (int) neighbour.getOrigin().getY());
            }
        }

        // Draw a hint line from a selected town to cursor position
        if (edgeMode) {
            int startX = (int) edgeOriginTown.getOrigin().getX();
            int startY = (int) edgeOriginTown.getOrigin().getY();
            int endX = (int) cursorPoint.getX();
            int endY = (int) cursorPoint.getY();
            g2d.drawLine(startX, startY, endX, endY);
        }

        // Render towns
        for (Town town : mainMap.keySet()) {
            drawTown(g2d, town);
        }

        /* FINISH RENDERING */
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        cursorPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point clickLocation = new Point(e.getX(), e.getY());
        Town townUnderCursor = getTownUnderCursor(clickLocation);
        if (townUnderCursor == null) {
            if (edgeMode) {
                // when we are clicking a blank space in edge mode, do nothing
                resetEdgeMode();
            } else {
                if (isInBounds(clickLocation)) {
                    String id = String.valueOf(mainMap.size() + 1);
                    Town town = new Town(id, clickLocation);
                    mainMap.put(town, new HashSet<>());
                }
            }
        } else {
            // get ready to add edges
            edgeMode = true;
            if (edgeOriginTown == null) {
                // first click - set the first town
                edgeOriginTown = townUnderCursor;
            } else {
                // second click - we are going to connect the two towns
                edgeDestinationTown = townUnderCursor;
                connectTowns(edgeOriginTown, edgeDestinationTown);
                resetEdgeMode(); // reset when we are done
            }
        }
        repaint();
    }

    private boolean isInBounds (Point clickLocation) {
        int drawX = (int) clickLocation.getX() - CIRCLE_RADIUS / 2;
        int drawY = (int) clickLocation.getY() - CIRCLE_RADIUS / 2;

        // check if x and y drawing positions will cause circle to be cut off by the frame 
        if (drawX < 0 || (drawX + CIRCLE_RADIUS) > PANEL_WIDTH) {
            return false;   
        } else if (drawY < 0 || (drawY + CIRCLE_RADIUS) > (PANEL_HEIGHT-30)) { // subtract 30 because screen height includes the frame header 
            return false;  
        } 

        return true; 
    }

    private void resetEdgeMode() {
        edgeMode = false;
        edgeOriginTown = null;
        edgeDestinationTown = null;
        repaint();
    }

    private void connectTowns(Town town1, Town town2) {
        if (town1.equals(town2)) {
            return;
        }

        if (mainMap.containsKey(town1)) {
            mainMap.get(town1).add(town2);
        } else {
            Set<Town> newSet = new HashSet<>();
            newSet.add(town2);
            mainMap.put(town1, newSet);
        }

        if (mainMap.containsKey(town2)) {
            mainMap.get(town2).add(town1);
        } else {
            Set<Town> newSet = new HashSet<>();
            newSet.add(town1);
            mainMap.put(town2, newSet);
        }
    }

    public Town getTownUnderCursor(Point cursorLocation) {
        for (Town curTown : mainMap.keySet()) {
            // use equation of circle to check if a click was inside a town
            double distanceX = Math.pow(cursorLocation.getX() - curTown.getOrigin().getX(), 2);
            double distanceY = Math.pow(cursorLocation.getY() - curTown.getOrigin().getY(), 2);
            if (distanceX + distanceY <= Math.pow(CIRCLE_RADIUS, 2)) {
                // then our point is inside this town's radius
                return curTown;
            }
        }
        return null;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        return;

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        return;

    }

    @Override
    public void mouseExited(MouseEvent e) {
        return;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        return;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        return;
    }
}