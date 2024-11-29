import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class SnakeGame extends JPanel implements ActionListener {
    private final int TILE_SIZE = 25;   // Size of each tile in the grid
    private final int WIDTH = 600;     // Width of the game board
    private final int HEIGHT = 600;    // Height of the game board
    private final int GAME_UNITS = (WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE);
    private int[] snakeX = new int[GAME_UNITS];
    private int[] snakeY = new int[GAME_UNITS];
    private int snakeLength = 2;
    private int appleX;
    private int appleY;
    private char direction = 'R';   
    private boolean running = false;
    private boolean paused = false;  
    private Timer timer;
    private int score = 0;            
    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Add key listener for controlling the snake
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (running) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (direction != 'D') direction = 'U';
                            break;
                        case KeyEvent.VK_DOWN:
                            if (direction != 'U') direction = 'D';
                            break;
                        case KeyEvent.VK_LEFT:
                            if (direction != 'R') direction = 'L';
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (direction != 'L') direction = 'R';
                            break;
                        case KeyEvent.VK_P:  // Pause or unpause the game with "P"
                            togglePause();
                            break;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();  // Restart the game when R is pressed
                }
            }
        });
        startGame();
    }

    private void startGame() {
        running = true;
        paused = false; // Ensure the game is not paused
        score = 0; // Reset the score when the game starts
        snakeLength = 3; // Reset snake length
        direction = 'R'; // Reset snake direction
        spawnApple();
        timer = new Timer(75, this);
        timer.start();
    }

    private void spawnApple() {
        appleX = (int) (Math.random() * (WIDTH / TILE_SIZE)) * TILE_SIZE;
        appleY = (int) (Math.random() * (HEIGHT / TILE_SIZE)) * TILE_SIZE;
    }

    private void move() {
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case 'U': snakeY[0] -= TILE_SIZE; break;
            case 'D': snakeY[0] += TILE_SIZE; break;
            case 'L': snakeX[0] -= TILE_SIZE; break;
            case 'R': snakeX[0] += TILE_SIZE; break;
        }
    }

    private void checkApple() {
        if (snakeX[0] == appleX && snakeY[0] == appleY) {
            snakeLength++;
            score += 10; // Increase score by 10 for each apple
            spawnApple();
        }
    }

    private void checkCollisions() {
        // Check if the snake collides with itself
        for (int i = snakeLength; i > 0; i--) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                running = false;
            }
        }
        // Check if the snake hits the wall
        if (snakeX[0] < 0 || snakeX[0] >= WIDTH || snakeY[0] < 0 || snakeY[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            // Draw the apple
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, TILE_SIZE, TILE_SIZE);

            // Draw the snake
            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(snakeX[i], snakeY[i], TILE_SIZE, TILE_SIZE);
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);
        } else {
            gameOver(g);
        }
        if (paused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Ink Free", Font.BOLD, 50));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Paused", (WIDTH - metrics.stringWidth("Game Paused")) / 2, HEIGHT / 2);
        }
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);
        g.setFont(new Font("Ink Free", Font.PLAIN, 25));
        g.drawString("Score: " + score, (WIDTH - metrics.stringWidth("Score: " + score)) / 2, HEIGHT / 2 + 50);
        g.drawString("Press R to Restart", (WIDTH - metrics.stringWidth("Press R to Restart")) / 2, HEIGHT / 2 + 100);
    }

    private void restartGame() {
        snakeLength = 3;
        direction = 'R';
        score = 0; 
        running = true; 
        paused = false;  
        spawnApple();
        timer.restart(); 
        repaint();
    }
    private void togglePause() {
        if (paused) {
            paused = false;  
            timer.start();
        } else {
            paused = true;  
            timer.stop();
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("SlitherSprint");
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

