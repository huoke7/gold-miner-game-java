import java.awt.*;

public class TNT extends GameObject{

    public TNT(int x, int y, int width, int height, String imgPath, int mass, int score){
        super(x, y, width, height, Toolkit.getDefaultToolkit().getImage(imgPath));
        this.m = mass;
        this.score = score;
    }

    @Override
    public void playSound() {
        new Sound("Sound/Explosion.wav").start();
    }
}
