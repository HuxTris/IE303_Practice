import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class FlappyBird {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird");
            GamePanel gamePanel = new GamePanel();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(gamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            gamePanel.requestFocusInWindow();
        });
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    static final int BOARD_WIDTH = 360;
    static final int BOARD_HEIGHT = 640;

    private static final int BIRD_START_X = 45;
    private static final int BIRD_START_Y = 320;
    private static final int BIRD_WIDTH = 34;
    private static final int BIRD_HEIGHT = 24;

    private static final int PIPE_WIDTH = 64;
    private static final int PIPE_HEIGHT = 512;
    private static final int PIPE_GAP = 150;
    private static final int PIPE_SPAWN_X = BOARD_WIDTH;

    private static final double GRAVITY = 0.42;
    private static final double JUMP_VELOCITY = -7.8;
    private static final double MAX_FALL_SPEED = 7.5;
    private static final double PIPE_VELOCITY_X = -3.5;

    private enum GameState {
        READY,
        PLAYING,
        GAME_OVER
    }

    private final Image backgroundImage;
    private final Image birdImage;
    private final Image topPipeImage;
    private final Image bottomPipeImage;

    private final Timer gameLoop;
    private final Timer pipeTimer;
    private final Random random = new Random();
    private final ArrayList<Pipe> pipes = new ArrayList<>();

    private Bird bird;
    private GameState state = GameState.READY;
    private int score;
    private int idleFrame;

    GamePanel() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleActionKey();
            }
        });

        backgroundImage = new ImageIcon("flappybirdbg.png").getImage();
        birdImage = new ImageIcon("flappybird.png").getImage();
        topPipeImage = new ImageIcon("toppipe.png").getImage();
        bottomPipeImage = new ImageIcon("bottompipe.png").getImage();

        bird = new Bird(BIRD_START_X, BIRD_START_Y, BIRD_WIDTH, BIRD_HEIGHT);

        gameLoop = new Timer(16, this);
        pipeTimer = new Timer(1500, e -> placePipes());

        gameLoop.start();
    }

    private void placePipes() {
        if (state != GameState.PLAYING) {
            return;
        }

        int randomPipeY = -PIPE_HEIGHT / 4 - random.nextInt(PIPE_HEIGHT / 2);
        Pipe topPipe = new Pipe(
                PIPE_SPAWN_X,
                randomPipeY,
                PIPE_WIDTH,
                PIPE_HEIGHT,
                topPipeImage,
                true
        );

        Pipe bottomPipe = new Pipe(
                PIPE_SPAWN_X,
                topPipe.y + PIPE_HEIGHT + PIPE_GAP,
                PIPE_WIDTH,
                PIPE_HEIGHT,
                bottomPipeImage,
                false
        );

        pipes.add(topPipe);
        pipes.add(bottomPipe);
    }

    private void move() {
        bird.velocityY += GRAVITY;
        if (bird.velocityY > MAX_FALL_SPEED) {
            bird.velocityY = MAX_FALL_SPEED;
        }
        bird.y += bird.velocityY;

        if (bird.y < 0 || bird.y + bird.height > BOARD_HEIGHT) {
            endGame();
        }

        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.x += PIPE_VELOCITY_X;

            if (pipe.x + pipe.width < 0) {
                iterator.remove();
                continue;
            }

            if (pipe.collidesWith(bird)) {
                endGame();
            }

            if (!pipe.isTopPipe && !pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score++;
            }
        }
    }

    private void endGame() {
        state = GameState.GAME_OVER;
        pipeTimer.stop();
    }

    private void resetGame() {
        bird = new Bird(BIRD_START_X, BIRD_START_Y, BIRD_WIDTH, BIRD_HEIGHT);
        pipes.clear();
        score = 0;
        idleFrame = 0;
    }

    private void startGame() {
        resetGame();
        state = GameState.PLAYING;
        bird.velocityY = JUMP_VELOCITY;

        if (!pipeTimer.isRunning()) {
            pipeTimer.start();
        } else {
            pipeTimer.restart();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.image, (int) Math.round(pipe.x), pipe.y, pipe.width, pipe.height, null);
        }

        int birdDrawY = (int) Math.round(bird.y);
        if (state == GameState.READY) {
            birdDrawY += (int) Math.round(Math.sin(idleFrame / 10.0) * 6);
        }
        g.drawImage(birdImage, bird.x, birdDrawY, bird.width, bird.height, null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString(String.valueOf(score), 20, 45);

        if (state == GameState.READY) {
            drawReadyScreen(g);
        } else if (state == GameState.GAME_OVER) {
            drawGameOver(g);
        }
    }

    private void drawReadyScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 95));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 34));
        drawCenteredString(g, "Flappy Bird", BOARD_HEIGHT / 2 - 105);

        drawPlayButton(g, "PLAY", BOARD_HEIGHT / 2 - 45);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCenteredString(g, "Press Space or Enter", BOARD_HEIGHT / 2 + 45);
        drawCenteredString(g, "Tap the bird through the pipes", BOARD_HEIGHT / 2 + 72);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 38));
        drawCenteredString(g, "Game Over", BOARD_HEIGHT / 2 - 40);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        drawCenteredString(g, "Score: " + score, BOARD_HEIGHT / 2 + 5);

        drawPlayButton(g, "PLAY AGAIN", BOARD_HEIGHT / 2 + 28);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCenteredString(g, "Press Space or Enter to Restart", BOARD_HEIGHT / 2 + 118);
    }

    private void drawPlayButton(Graphics g, String text, int y) {
        int buttonWidth = 150;
        int buttonHeight = 44;
        int x = (BOARD_WIDTH - buttonWidth) / 2;

        g.setColor(new Color(252, 186, 3));
        g.fillRoundRect(x, y, buttonWidth, buttonHeight, 8, 8);
        g.setColor(new Color(122, 84, 0));
        g.drawRoundRect(x, y, buttonWidth, buttonHeight, 8, 8);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (buttonWidth - metrics.stringWidth(text)) / 2;
        int textY = y + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, textX, textY);
    }

    private void drawCenteredString(Graphics g, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (BOARD_WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == GameState.PLAYING) {
            move();
        } else {
            idleFrame++;
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode != KeyEvent.VK_SPACE && keyCode != KeyEvent.VK_ENTER) {
            return;
        }

        handleActionKey();
    }

    private void handleActionKey() {
        requestFocusInWindow();

        if (state == GameState.READY || state == GameState.GAME_OVER) {
            startGame();
            return;
        }

        bird.velocityY = JUMP_VELOCITY;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

class Bird {
    int x;
    double y;
    int width;
    int height;
    double velocityY;

    Bird(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    Rectangle getBounds() {
        return new Rectangle(x, (int) Math.round(y), width, height);
    }
}

class Pipe {
    double x;
    int y;
    int width;
    int height;
    Image image;
    boolean isTopPipe;
    boolean passed;

    Pipe(int x, int y, int width, int height, Image image, boolean isTopPipe) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.isTopPipe = isTopPipe;
    }

    boolean collidesWith(Bird bird) {
        return getBounds().intersects(bird.getBounds());
    }

    Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), y, width, height);
    }
}
