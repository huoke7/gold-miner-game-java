import java.awt.*;

public class Dimond extends GameObject{

    public Dimond(int x, int y, int width, int height, String imgPath, int mass, int score){
        super(x, y, width, height, Toolkit.getDefaultToolkit().getImage(imgPath));
        this.m = mass;
        this.score = score;
        }
    
    @Override
    public void playSound() {
        new Sound("Sound/ChaChing.wav").start();
    }
}