import java.awt.*;

public class Gold extends GameObject{

    public Gold(int x, int y, int width, int height, String imgPath, int mass, int score){
        super(x, y, width, height, Toolkit.getDefaultToolkit().getImage(imgPath));
        this.m = mass;
        this.score = score;
    }

    @Override
    public void playSound() {
        new Sound("Sound/ChaChing.wav").start();
    }
}