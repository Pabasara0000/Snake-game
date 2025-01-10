import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 20;
    static final int NUMBER_OF_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    final int x[] = new int[NUMBER_OF_UNITS];
    final int y[] = new int[NUMBER_OF_UNITS];
    int length = 5;
    int foodeaten;
    int foodX;
    int foodY;
    char direction = '0';
    boolean running = false;
    int level = 1;
    int scoreThreshold = 20; // Score required to level up
    Random random;
    Timer timer;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        play();
    }

    public void play() {
        resetGame();
        addFood();
        running = true;
        timer = new Timer(getDelay(), this);
        timer.start();
    }

    private void resetGame() {
        length = 5;
        foodeaten = 0;
        level = 1;
        direction = '0';
        for (int i = 0; i < length; i++) {
            x[i] = 0;
            y[i] = 0;
        }
    }

    private int getDelay() {
        return Math.max(40, 100 - (level - 1) * 10); // Reduce delay as level increases
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void move() {
        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (direction == 'L') {
            x[0] = x[0] - UNIT_SIZE;
        } else if (direction == 'R') {
            x[0] = x[0] + UNIT_SIZE;
        } else if (direction == 'U') {
            y[0] = y[0] - UNIT_SIZE;
        } else {
            y[0] = y[0] + UNIT_SIZE;
        }
    }

    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            length++;
            foodeaten++;
            addFood();

            if (foodeaten % scoreThreshold == 0) {
                levelUp();
            }
        }
    }

    private void levelUp() {
        level++;
        timer.setDelay(getDelay());
    }

    public void draw(Graphics graphics) {
        if (running) {
            graphics.setColor(new Color(210, 115, 90));
            graphics.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            graphics.setColor(Color.WHITE);
            graphics.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

            for (int i = 1; i < length; i++) {
                graphics.setColor(new Color(40, 200, 150));
                graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            graphics.setColor(new Color(0, 191, 255)); // Deep Sky Blue
            graphics.setFont(new Font("Verdana", Font.BOLD, 28)); // Bold Verdana font with larger size
            FontMetrics metrics = getFontMetrics(graphics.getFont());
            graphics.drawString("Score: " + foodeaten + "  Level: " + level, (WIDTH - metrics.stringWidth("Score: " + foodeaten + "  Level: " + level)) / 2, graphics.getFont().getSize());

        } else {
            gameOver(graphics);
        }
    }

    public void addFood() {
        foodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void checkHit() {
        for (int i = length; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics graphics) {
        // Game Over Title
        graphics.setColor(new Color(255, 69, 0)); // Bright red-orange
        graphics.setFont(new Font("Serif", Font.BOLD, 60)); // Bold and larger size
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over!", (WIDTH - metrics.stringWidth("Game Over!")) / 2, HEIGHT / 2 - 50);

        // Score and Level
        graphics.setColor(new Color(30, 144, 255)); // Dodger blue
        graphics.setFont(new Font("Courier New", Font.PLAIN, 30)); // Monospaced font for clean look
        metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " + foodeaten + "  Level: " + level,
                (WIDTH - metrics.stringWidth("Score: " + foodeaten + "  Level: " + level)) / 2, HEIGHT / 2);

        // Replay Prompt
        graphics.setColor(new Color(50, 205, 50)); // Lime green
        graphics.setFont(new Font("Comic Sans MS", Font.ITALIC, 25)); // Fun and playful font
        metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Press R to Replay",
                (WIDTH - metrics.stringWidth("Press R to Replay")) / 2, HEIGHT / 2 + 50);
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (running) {
            move();
            checkFood();
            checkHit();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R')
                        direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L')
                        direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D')
                        direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U')
                        direction = 'D';
                    break;
                case KeyEvent.VK_R:
                    if (!running) {
                        play();
                    }
                    break;
            }
        }
    }
}
