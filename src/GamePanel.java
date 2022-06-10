import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE;
    static int delay = 200;

    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 2;
    int appleEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    String restartMessage = "press Enter to restart the game.";
    String highScoreString = "The highest score ";
    Score highScoreObj = new Score();


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdaptor());
        startGame();
    }

    public void startNewTimer() {
        timer = new Timer(delay, this);
        timer.start();
    }

    public void startGame() {
        newApple();
        running = true;
        startNewTimer();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // draw grid
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            // draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // draw snake
            for (int i = 0; i < bodyParts - 1; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
//                    g.setColor(new Color(45, 180, 0));
                    g.setColor(new Color(
                            random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            // draw score
            g.setColor(Color.red);
            g.setFont(new Font("Ink free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + appleEaten,
                    (SCREEN_WIDTH - metrics.stringWidth("Score: " + appleEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            appleEaten++;
            bodyParts++;
            newApple();
            timer.stop();
            delay -= 5;
            startNewTimer();
        }
    }

    public void checkCollisions() {
        // checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        // checks if head touches the left or right border
        if ((x[0] < 0) || (x[0] > SCREEN_WIDTH)) {
            running = false;
        }
        // check if head touches the upper or down border
        if ((y[0] < 0) || (y[0] > SCREEN_HEIGHT)) {
            running = false;
        }
        if (!running) {
            timer.stop();
            delay = 200;
            repaint();
        }
    }

    public void gameOver(Graphics g) {
        highScoreObj.writeHighScore(appleEaten);
        repaint();

        // score display
        g.setColor(Color.red);
        g.setFont(new Font("Ink free", Font.BOLD, 40));
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + appleEaten,
                (SCREEN_WIDTH - scoreMetrics.stringWidth("Score: " + appleEaten)) / 2, g.getFont().getSize());

        // restart message display
        g.setColor(Color.yellow);
        g.setFont(new Font("Ink free", Font.BOLD, 20));
        FontMetrics restartMetrics = getFontMetrics(g.getFont());
        g.drawString(restartMessage,
                (SCREEN_WIDTH - restartMetrics.stringWidth(restartMessage)) / 2, g.getFont().getSize() * 4);

        // high score message display
        g.setColor(Color.blue);
        g.setFont(new Font("Ink free", Font.BOLD, 20));
        FontMetrics highScoreMetrics = getFontMetrics(g.getFont());

        // gettext
        String highScoreText = highScoreObj.readHighScore();
        g.drawString(highScoreString + highScoreText,
                (SCREEN_WIDTH - highScoreMetrics.stringWidth(highScoreString + highScoreText)) / 2,
                g.getFont().getSize() * 6);

        // Game Over display
        g.setColor(Color.red);
        g.setFont(new Font("Ink free", Font.BOLD, 75));
        FontMetrics gOverMetrics = getFontMetrics(g.getFont());
        g.drawString("Game Over",
                (SCREEN_WIDTH - gOverMetrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    // inner class
    public class MyKeyAdaptor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    restartGame();
                    new GameFrame();
            }
        }
    }

    private void restartGame() {
        Frame[] frames = GameFrame.getFrames();
        for (Frame frame : frames
        ) {
            frame.dispose();
        }
    }

}


