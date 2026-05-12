import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class yu extends JFrame {

    public yu() {
        setTitle("Yu Wei Island Air Battle");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        add(new GamePanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        new yu();
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {

    final int WIDTH = 800;
    final int HEIGHT = 650;

    Timer timer = new Timer(40, this);

    Player player;
    Enemy enemy;

    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Obstacle> obstacles = new ArrayList<>();

    int playerPlanes = 10;
    int enemyPlanes = 10;

    int playerHP = 100;
    int enemyHP = 100;

    int score = 0;

    boolean gameOver = false;
    boolean playerWin = false;

    boolean takeOff = true;
    int takeOffCounter = 0;

    boolean nightMode = false;

    boolean up, down, left, right;

    int shootDelay = 0;
    int enemyShootDelay = 0;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);

        player = new Player(380, 520);
        enemy = new Enemy(360, 80);

        obstacles.add(new Obstacle(120, 230, 2));
        obstacles.add(new Obstacle(300, 180, -2));
        obstacles.add(new Obstacle(520, 260, 2));
        obstacles.add(new Obstacle(220, 380, -2));
        obstacles.add(new Obstacle(610, 420, 2));

        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBackground(g);
        drawIsland(g);

        for (Obstacle o : obstacles) o.draw(g);
        for (Bullet b : bullets) b.draw(g);

        if (!gameOver) {
            enemy.draw(g);
            player.draw(g);
        }

        drawUI(g);

        if (takeOff && !gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("YU WEI TAKE OFF!", 260, 300);
        }

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setFont(new Font("Arial", Font.BOLD, 45));

            if (playerWin) {
                g.setColor(Color.YELLOW);
                g.drawString("YU WEI WINS!", 250, 300);
            } else {
                g.setColor(Color.RED);
                g.drawString("GAME OVER", 250, 300);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press R to Restart", 290, 350);
        }
    }

    void drawBackground(Graphics g) {
        if (nightMode) {
            g.setColor(new Color(5, 10, 35));
        } else {
            g.setColor(new Color(25, 135, 210));
        }

        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (nightMode) {
            g.setColor(Color.WHITE);

            for (int i = 0; i < 30; i++) {
                g.fillOval((i * 63) % WIDTH, (i * 91) % HEIGHT, 3, 3);
            }

            g.setColor(Color.YELLOW);
            g.fillOval(680, 80, 50, 50);

            g.setColor(Color.WHITE);
            g.drawString("NIGHT BATTLE", 650, 150);

        } else {
            g.setColor(Color.ORANGE);
            g.fillOval(680, 80, 60, 60);

            g.setColor(Color.WHITE);
            g.drawString("DAY BATTLE", 660, 155);
        }
    }

    void drawIsland(Graphics g) {
        g.setColor(new Color(40, 160, 85));
        g.fillOval(250, 520, 300, 100);

        g.setColor(Color.GRAY);
        g.fillRect(360, 540, 80, 90);

        g.setColor(Color.WHITE);
        g.drawString("YU BASE", 365, 535);
    }

    void drawUI(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 18));

        g.setColor(Color.WHITE);

        g.drawString("Pilot: YU WEI 1113405010", 20, 30);
        g.drawString("Score: " + score, 20, 60);

        g.drawString("My Planes: " + playerPlanes, 20, 90);
        g.drawString("Enemy Planes: " + enemyPlanes, 20, 120);

        g.drawString("My HP", 20, 155);

        g.setColor(Color.CYAN);
        g.fillRect(100, 140, playerHP * 2, 18);

        g.setColor(Color.WHITE);
        g.drawRect(100, 140, 200, 18);

        g.drawString("Enemy HP", 20, 190);

        g.setColor(Color.RED);
        g.fillRect(120, 175, enemyHP * 2, 18);

        g.setColor(Color.WHITE);
        g.drawRect(120, 175, 200, 18);

        g.drawString("Move: WASD / Arrow Keys", 520, 30);
        g.drawString("AUTO FIRE", 520, 55);
        g.drawString("Restart: R", 520, 80);
        g.drawString("Day / Night: N", 520, 105);
    }

    public void actionPerformed(ActionEvent e) {

        if (gameOver) {
            repaint();
            return;
        }

        moveObstacles();

        if (takeOff) {
            takeOffCounter++;
            player.y -= 2;

            if (takeOffCounter > 80) {
                takeOff = false;
            }

            repaint();
            return;
        }

        movePlayer();
        playerShoot();
        enemyMove();
        enemyShoot();
        updateBullets();
        checkBulletHit();

        repaint();
    }

    void moveObstacles() {
        for (Obstacle o : obstacles) {
            o.move();
        }
    }

    void playerShoot() {
        shootDelay++;

        if (shootDelay >= 3) {
            bullets.add(new Bullet(player.x + 18, player.y, -10, true));
            shootDelay = 0;
        }
    }

    void enemyShoot() {
        enemyShootDelay++;

        if (enemyShootDelay >= 20) {
            bullets.add(new Bullet(enemy.x + 18, enemy.y + 40, 7, false));
            enemyShootDelay = 0;
        }
    }

    void movePlayer() {
        int nextX = player.x;
        int nextY = player.y;

        if (up) nextY -= player.speed;
        if (down) nextY += player.speed;
        if (left) nextX -= player.speed;
        if (right) nextX += player.speed;

        if (nextX < 0 || nextX > WIDTH - player.size) return;
        if (nextY < 0 || nextY > HEIGHT - player.size) return;

        Rectangle nextRect = new Rectangle(nextX, nextY, player.size, player.size);

        for (Obstacle o : obstacles) {
            if (nextRect.intersects(o.getRect())) {
                return;
            }
        }

        player.x = nextX;
        player.y = nextY;
    }

    void enemyMove() {
        if (enemy.x < player.x) enemy.x += enemy.speed;
        if (enemy.x > player.x) enemy.x -= enemy.speed;

        if (enemy.y < 120) enemy.y += enemy.speed;
        if (enemy.y > 120) enemy.y -= enemy.speed;
    }

    void updateBullets() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.y += b.speed;

            if (b.y < 0 || b.y > HEIGHT) {
                bullets.remove(i);
            }
        }
    }

    void checkBulletHit() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);

            if (b.fromPlayer && b.getRect().intersects(enemy.getRect())) {
                bullets.remove(i);
                enemyHP -= 20;

                if (enemyHP <= 0) {
                    enemyPlanes--;
                    score += 100;

                    if (enemyPlanes <= 0) {
                        gameOver = true;
                        playerWin = true;
                        return;
                    }

                    enemyHP = 100;
                    enemy.x = 360;
                    enemy.y = 80;
                }

                return;
            }

            if (!b.fromPlayer && b.getRect().intersects(player.getRect())) {
                bullets.remove(i);
                playerHP -= 20;

                if (playerHP <= 0) {
                    playerPlanes--;

                    if (playerPlanes <= 0) {
                        gameOver = true;
                        playerWin = false;
                        return;
                    }

                    playerHP = 100;
                    player.x = 380;
                    player.y = 520;

                    takeOff = true;
                    takeOffCounter = 0;
                }

                return;
            }
        }
    }

    void restartGame() {
        playerPlanes = 10;
        enemyPlanes = 10;

        playerHP = 100;
        enemyHP = 100;

        score = 0;

        gameOver = false;
        playerWin = false;

        takeOff = true;
        takeOffCounter = 0;

        shootDelay = 0;
        enemyShootDelay = 0;

        bullets.clear();

        player.x = 380;
        player.y = 520;

        enemy.x = 360;
        enemy.y = 80;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) up = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) down = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = true;

        if (key == KeyEvent.VK_R) {
            restartGame();
        }

        if (key == KeyEvent.VK_N) {
            nightMode = !nightMode;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) up = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) down = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = false;
    }

    public void keyTyped(KeyEvent e) {}
}

class Player {

    int x, y;
    int size = 40;
    int speed = 6;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void draw(Graphics g) {
        g.setColor(Color.CYAN);

        int[] xs = {x + 20, x, x + 40};
        int[] ys = {y, y + 40, y + 40};

        g.fillPolygon(xs, ys, 3);

        g.setColor(Color.WHITE);
        g.drawString("YU", x + 10, y + 55);
    }

    Rectangle getRect() {
        return new Rectangle(x, y, size, size);
    }
}

class Enemy {

    int x, y;
    int size = 40;
    int speed = 6;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void draw(Graphics g) {
        g.setColor(Color.RED);

        int[] xs = {x + 20, x, x + 40};
        int[] ys = {y + 40, y, y};

        g.fillPolygon(xs, ys, 3);

        g.setColor(Color.WHITE);
        g.drawString("ENEMY", x - 5, y - 5);
    }

    Rectangle getRect() {
        return new Rectangle(x, y, size, size);
    }
}

class Bullet {

    int x, y;
    int width = 6;
    int height = 15;
    int speed;
    boolean fromPlayer;

    public Bullet(int x, int y, int speed, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.fromPlayer = fromPlayer;
    }

    void draw(Graphics g) {
        if (fromPlayer) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.PINK);
        }

        g.fillRect(x, y, width, height);
    }

    Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
}

class Obstacle {

    int x, y;
    int size = 55;
    int speedX;

    public Obstacle(int x, int y, int speedX) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
    }

    void move() {
        x += speedX;

        if (x <= 0 || x >= 800 - size) {
            speedX = -speedX;
        }
    }

    void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillOval(x, y, size, size);

        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(x, y, size, size);
    }

    Rectangle getRect() {
        return new Rectangle(x, y, size, size);
    }
}