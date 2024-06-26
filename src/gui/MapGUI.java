package gui;

import model.*;
import model.Spring;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * The MapGUI class represents the graphical user interface for the game map.
 * It handles the rendering of game elements, players, and provides interaction mechanisms such as key and mouse listeners.
 */
public class MapGUI extends JPanel implements KeyListener {

    public boolean isChangingInputPipe = false;
    public boolean isChangingOutputPipe = false;
    public Pump selectedPump = null;
    public static Element selectedElement;
    public static EndOfPipe selectedEndOfPipe;

    /**
     * Gets the currently selected element on the map.
     *
     * @return the currently selected Element
     */
    public static Element getSelectedElement() {
        return selectedElement;
    }

    /**
     * Gets the currently selected end of pipe on the map.
     *
     * @return the currently selected EndOfPipe
     */
    public static EndOfPipe getSelectedEndOfPipe() {
        return selectedEndOfPipe;
    }

    private javax.swing.Timer refreshTimer;
    public Image tileImage = new ImageIcon("src\\gui\\images\\MapTiles2.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    private Game game;
    private JPanel keyMappingPanel;
    public static boolean isMoveActive = false;
    private JTextArea console;
    JPanel southPanel = new JPanel();

    /**
     * Constructs a MapGUI with the specified Game object.
     *
     * @param game the Game object representing the game model
     */
    public MapGUI(Game game) {
        this.game = game;
        game.mapGUI = this;
        setupRefreshTimer();
        setupKeyMappingPanel();
        setupConsole();
        setupUI();
    }

    /**
     * Sets up the user interface components.
     */
    private void setupUI() {
        this.setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
        southPanel.setMaximumSize(new Dimension(800, 150)); // This constrains the maximum height
        add(southPanel, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isMoveActive) { // Check if move action is active
                    Player currentPlayer = game.players[game.currentPlayerIndex];
                    if (currentPlayer != null) {
                        selectObjectAt(e.getX(), e.getY());
                        currentPlayer.move(game, selectedElement);
                    }
                    isMoveActive = false; // Reset the move action state
                }
                else if(isChangingInputPipe || isChangingOutputPipe)
                {
                    boolean correctSelection = false;
                    selectObjectAt(e.getX(), e.getY());
                    for(Pipe p : selectedPump.connectedPipes)
                    {
                        if(selectedElement == p)
                        {
                            correctSelection = true;
                        }
                    }
                    if(correctSelection)
                    {
                        if(isChangingInputPipe)
                        {
                            Pipe selectedPipe = (Pipe) selectedElement;
                            if(selectedPipe == selectedPump.outPipe)
                            {
                                System.out.println("Input Pipe and Output Pipe cannot be the same.");
                            }
                            else {
                                selectedPump.inPipe = (Pipe) selectedElement;
                                System.out.println(selectedPump.getName() + " changed the input pipe to " + selectedElement.getName());
                            }
                        }
                        else
                        {
                            Pipe selectedPipe = (Pipe) selectedElement;
                            if(selectedPipe == selectedPump.inPipe)
                            {
                                System.out.println("Input Pipe and Output Pipe cannot be the same.");
                            }
                            else {
                                selectedPump.outPipe = (Pipe) selectedElement;
                                System.out.println(selectedPump.getName() + " changed the input pipe to " + selectedElement.getName());
                            }
                        }
                        isChangingInputPipe = false;
                        isChangingOutputPipe = false;
                    }
                    else
                    {
                        System.out.println("Invalid input. Select a pipe that is connected to the Pump");
                    }
                }
                else {
                    selectObjectAt(e.getX(), e.getY());
                }
                repaint();
            }
        });
    }

    /**
     * Sets up the console for logging game events and output.
     */
    private void setupConsole() {
        console = new JTextArea(5, 50); // 5 rows, 50 columns
        console.setEditable(false);
        console.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 100));
        scrollPane.setMaximumSize(new Dimension(800, 100)); // Control the maximum size

        PrintStream printStream = new PrintStream(new CustomOutputStream(console));
        System.setOut(printStream);
        System.setErr(printStream);

        southPanel.add(scrollPane);
    }

    /**
     * Sets up the refresh timer to periodically repaint the component.
     */
    private void setupRefreshTimer() {
        refreshTimer = new javax.swing.Timer(1000, e -> repaint()); // Refresh every second
        refreshTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int iw = tileImage.getWidth(this);
        int ih = tileImage.getHeight(this);
        if (iw > 0 && ih > 0) { // Make sure the image has loaded correctly
            for (int x = 0; x < getWidth(); x += iw) {
                for (int y = 100; y < getHeight(); y += ih) {
                    g.drawImage(tileImage, x, y, iw, ih, this);
                }
            }
            drawElements(g);
            drawPlayers(g);
            drawPlayerInfo(g);
            drawTimer(g);
            drawScores(g);
        }
        if (selectedElement != null) {
            g.setColor(Color.RED); // Set highlight color
            Point pos = selectedElement.getPosition();
            for (int i = 0; i < 5; i++) { // Change '5' to the desired border thickness
                g.drawRect(pos.x - i, pos.y - i, selectedElement.width + 2 * i, selectedElement.height + 2 * i);
            }
        }
        if (selectedEndOfPipe != null) {
            g.setColor(Color.RED); // Set highlight color
            Point pos = selectedEndOfPipe.getPosition();
            for (int i = 0; i < 5; i++) { // Change '5' to the desired border thickness
                g.drawRect(pos.x - i, pos.y - i, selectedEndOfPipe.width + 2 * i, selectedEndOfPipe.height + 2 * i);
            }
        }
        g.setColor(Color.BLACK); // Reset color for other drawing
    }

    /**
     * Draws the game timer on the panel.
     *
     * @param g the Graphics context to draw on
     */
    private void drawTimer(Graphics g) {
        if (game.timer != null) {
            String time = game.timer.getCurrentTimeFormatted();
            g.setColor(Color.RED);
            g.setFont(new Font("SansSerif", Font.BOLD, 50));
            int x = getWidth() - 135; // Position from the right edge
            int y = 55; // Margin from the top
            g.drawString(time, x, y);
        }
    }

    /**
     * Draws the scores for the Plumbers and Saboteurs on the panel.
     *
     * @param g the Graphics context to draw on
     */
    private void drawScores(Graphics g) {
        int collectedWater = game.calculateCollectedWater();
        int leakedWater = game.calculateLeakedWater();

        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        int x = 10; // Margin from the left edge
        int y = 30; // Margin from the top

        g.drawString("Team Plumbers: " + collectedWater, x, y);
        g.drawString("Team Saboteurs: " + leakedWater, x, y + 30); // Slightly below the first string
    }

    /**
     * Selects the game object (element or end of pipe) at the specified coordinates.
     *
     * @param x the x-coordinate of the mouse click
     * @param y the y-coordinate of the mouse click
     */
    private void selectObjectAt(int x, int y) {
        for (EndOfPipe eop : game.endOfPipeList) {
            if (eop.contains(x, y)) {
                selectedEndOfPipe = eop;
                selectedElement = null;
                return;
            }
        }
        for (Element e : game.elementList) {
            if (e.contains(x, y)) {
                selectedElement = e;
                selectedEndOfPipe = null;
                return;
            }
        }
    }

    /**
     * Sets up the key mapping panel that displays key bindings for actions.
     */
    private void setupKeyMappingPanel() {
        keyMappingPanel = new JPanel();
        keyMappingPanel.setLayout(new GridLayout(4, 3, 5, 5));
        keyMappingPanel.setBackground(Color.BLACK);
        keyMappingPanel.setPreferredSize(new Dimension(800, 100)); // Control the size to fit within southPanel
        keyMappingPanel.setMaximumSize(new Dimension(800, 100));
        String[] actions = {"Move to an element: Q", "Change the input pipe of a pump: A", "Change the output pipe of a pump: S", "Pass turn: W",
                "End the game: E", "[Saboteur Only] Puncture a pipe: P", "[Plumber Only] Pick up a pump: D",
                "[Plumber Only] Insert pump: I", "[Plumber Only] Fix a broken pump: F", "[Plumber Only] Fix a broken pipe: O",
                "[Plumber Only] Pick up end of pipe: R", "[Plumber Only] Insert end of pipe: T"};
        Color[] colors = {Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.RED, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE};

        for (int i = 0; i < actions.length; i++) {
            JLabel actionLabel = new JLabel(actions[i], SwingConstants.CENTER);
            actionLabel.setOpaque(true);
            actionLabel.setBackground(colors[i]);
            keyMappingPanel.add(actionLabel);
        }
        southPanel.add(keyMappingPanel);
    }

    /**
     * Draws the game elements (pipes, pumps, cisterns, springs, end of pipes) on the panel.
     *
     * @param g the Graphics context to draw on
     */
    private void drawElements(Graphics g) {
        for (Pipe pipe : game.pipeList) {
            new PipeGUI(pipe).draw(g);
        }
        for (Pump pump : game.pumpList) {
            if (pump.isVisible()) { // Check the visibility flag
                new PumpGUI(pump).draw(g);
            }
        }
        for (Cistern cistern : game.cisternList) {
            new CisternGUI(cistern).draw(g);
        }
        for (Spring spring : game.springList) {
            new SpringGUI(spring).draw(g);
        }
        for (EndOfPipe endOfPipe : game.endOfPipeList) {
            if (endOfPipe.isVisible()) { // Check the visibility flag
                new EndOfPipeGUI(endOfPipe).draw(g);
            }
        }
    }

    /**
     * Draws the players (saboteurs and plumbers) on the panel.
     *
     * @param g the Graphics context to draw on
     */
    private void drawPlayers(Graphics g) {
        for (Saboteur saboteur : game.saboteurs) {
            new SaboteurGUI(saboteur).draw(g);
        }
        for (Plumber plumber : game.plumbers) {
            new PlumberGUI(plumber).draw(g);
        }
    }

    /**
     * Draws the information of the current player on the panel.
     *
     * @param g the Graphics context to draw on
     */
    private void drawPlayerInfo(Graphics g) {
        Player currentPlayer = game.players[game.currentPlayerIndex];
        String team = Arrays.asList(game.saboteurs).contains(currentPlayer) ? "Saboteurs" : "Plumbers";
        String pickedUpPump = "No";
        String pickedUpEndOfPipe = "No";

        if (currentPlayer instanceof Plumber) {
            Plumber plumber = (Plumber) currentPlayer;
            pickedUpPump = plumber.pickedUpPump != null ? "Yes" : "No";
            pickedUpEndOfPipe = plumber.pickedUpEoP != null ? "Yes" : "No";
        }

        String playerInfo = String.format("Player: %s's turn | Team: %s | Has a picked up pump: %s | Has a picked up end of pipe: %s",
                currentPlayer.playerName, team, pickedUpPump, pickedUpEndOfPipe);

        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 25));
        int x = 10; // Margin from the left edge
        int y = 90; // Margin from the top

        g.drawString(playerInfo, x, y);
    }

    /**
     * Custom output stream to redirect system output to the console.
     */
    static class CustomOutputStream extends java.io.OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            SwingUtilities.invokeLater(() -> {
                textArea.append(String.valueOf((char) b));
                // Make sure the last part of the text is always shown
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        game.setCurrentAction(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
