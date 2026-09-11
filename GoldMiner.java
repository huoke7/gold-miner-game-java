import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GoldMiner extends JFrame implements KeyListener {

    public Background bg = new Background();
    Line line = new Line(this); 
    Image offScreenImage;

    // Game state: 0=Menu, 1=Playing, 2=Win, 3=Lose, 4=Ranking
    static int state = 0; 
    
    boolean isPaused = false;

    void launch() {
        this.setVisible(true);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setTitle("Gold Miner");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // Mouse listener for menu buttons
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    if (state == 0) {
                        // Start Game button area
                        if (e.getX() > 250 && e.getX() < 550 && e.getY() > 300 && e.getY() < 350) {
                            startGame();
                        }
                        // High Scores button area
                        if (e.getX() > 250 && e.getX() < 550 && e.getY() > 380 && e.getY() < 430) {
                            state = 4;
                        }
                    } else if (state == 2 || state == 3 || state == 4) {
                        if (state == 2) { 
                            // Next level
                            bg.level++;
                            bg.init();
                            state = 1;
                        } else { 
                            // Return to menu
                            state = 0;
                        }
                    }
                }
            }
        });

        this.addKeyListener(this);
        this.setFocusable(true); 

        // Main game loop
        while (true) {
            repaint();
            next();
            try {
                Thread.sleep(10); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        }
    }

    // The extracted keyPressed method
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (state == 1) {
                if (line.state == 0 && !isPaused) line.state = 1; 
                new Sound("Sound/HookSound.wav").start(); 
            } else if (state == 0) {
                startGame();
            } else if (state == 2) {
                bg.level++;
                bg.init();
                state = 1;
            } else if (state == 3 || state == 4) {
                state = 0;
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (state == 1) {
                isPaused = true;
                line.handlePause(); 
                long pauseStartTime = System.currentTimeMillis();

                int option = JOptionPane.showConfirmDialog(
                    this, 
                    "Do you want to return to Main Menu?", 
                    "Exit Game", 
                    JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    state = 0;
                    isPaused = false;
                    line.state = 0;
                } else {
                    isPaused = false;
                    long pauseDuration = System.currentTimeMillis() - pauseStartTime;
                    bg.endTime += pauseDuration;
                    
                    line.handleResume();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    
    void startGame() {
        bg.level = 1;
        Background.totalScore = 0;
        bg.init();
        state = 1;
        isPaused = false;
    }

    void next() {
        if (state == 1 && !isPaused) {
            line.logic();
            if (bg.gameTime()) {
                if (bg.isReachGoal()) {
                    state = 2; 
                } else {
                    state = 3; 
                    saveScore();
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        offScreenImage = this.createImage(800, 600);
        Graphics gImage = offScreenImage.getGraphics();
        
        // Draw background image
        Image bgImg = Toolkit.getDefaultToolkit().getImage("imgs/bg.png");
        gImage.drawImage(bgImg, 0, 0, 800, 600, null);

        if (state == 0) {
            // Menu UI
            gImage.setColor(Color.BLACK);
            gImage.setFont(new Font("Arial", Font.BOLD, 50));
            gImage.drawString("GOLD MINER", 250, 200);
            
            gImage.setColor(Color.ORANGE);
            gImage.fillRect(250, 300, 300, 50); 
            gImage.fillRect(250, 380, 300, 50); 
            
            gImage.setColor(Color.WHITE);
            gImage.setFont(new Font("Arial", Font.BOLD, 30));
            gImage.drawString("START GAME", 300, 335);
            gImage.drawString("HIGH SCORES", 300, 415);
            
            gImage.setColor(Color.BLACK);
            gImage.setFont(new Font("Arial", Font.PLAIN, 15));
            gImage.drawString("Press SPACE to Launch Hook", 300, 500);
            gImage.drawString("Try to catch the gold and avoid the TNT", 300, 530);
            
        } else if (state == 1) {
            // Game UI
            gImage.setColor(new Color(139, 69, 19));
            gImage.drawLine(0, 180, 800, 180);
            gImage.drawImage(Toolkit.getDefaultToolkit().getImage("imgs/Character.png"), 320, 50, 100, 130, null);
            bg.paintSelf(gImage);
            line.lines(gImage);
            
            if (isPaused) {
                gImage.setColor(new Color(0, 0, 0, 150));
                gImage.fillRect(0, 0, 800, 600);
                gImage.setColor(Color.WHITE);
                gImage.setFont(new Font("Arial", Font.BOLD, 50));
                gImage.drawString("PAUSED", 300, 300);
            }
            
        } else if (state == 2) {
            // Win UI
            gImage.setColor(Color.GREEN);
            gImage.setFont(new Font("Arial", Font.BOLD, 40));
            gImage.drawString("LEVEL " + bg.level + " CLEARED!", 220, 250);
            gImage.drawString("Next Goal: " + (bg.level+1)*1000, 250, 320);
            gImage.drawString("Press SPACE to Next Level", 200, 400);
            
        } else if (state == 3) {
            // Lose UI
            gImage.setColor(Color.RED);
            gImage.setFont(new Font("Arial", Font.BOLD, 50));
            gImage.drawString("GAME OVER", 250, 250);
            gImage.setColor(Color.BLACK);
            gImage.setFont(new Font("Arial", Font.BOLD, 30));
            gImage.drawString("Final Score: " + Background.totalScore, 280, 320);
            gImage.drawString("Press SPACE to Menu", 280, 400);
            
        } else if (state == 4) {
            drawHighScores(gImage);
        }

        g.drawImage(offScreenImage, 0, 0, null);//draw it together
    }
    
    // Draw high scores
    void drawHighScores(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("TOP 5 SCORES", 250, 100);
        g.setFont(new Font("Arial", Font.PLAIN, 25));
        
        File file = new File("highscores.txt");
        
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                List<Integer> scores = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(": ");
                    if(parts.length > 1) {
                        scores.add(Integer.parseInt(parts[1].trim()));
                    }
                }
                // sorting
                for (int i = 0; i < scores.size() - 1; i++) {
                    for (int j = 0; j < scores.size() - 1 - i; j++) {
                        if (scores.get(j) < scores.get(j + 1)) {
                            int temp = scores.get(j);
                            scores.set(j, scores.get(j + 1));
                            scores.set(j + 1, temp);
                        }
                    }
                }

                for (int i = 0; i < Math.min(5, scores.size()); i++) {
                    g.drawString("Rank " + (i+1) + ": " + scores.get(i), 300, 220 + i * 50);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            g.drawString("No Scores Yet!", 300, 200);
        }
        
        g.drawString("Press SPACE to Return", 280, 500);
    }
    
    // Save score
    void saveScore() {
        try (PrintWriter out = new PrintWriter(new FileWriter("highscores.txt", true))) {
            out.println("Score: " + Background.totalScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GoldMiner().launch();
    }
}