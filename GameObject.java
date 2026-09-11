import java.awt.*;

public abstract class GameObject{
    int x;
    int y;
    int width;
    int height;
    Image img;
    
    //Flag for captured state
    boolean flag = false;
    //Mass of object
    int m = 10; 
    //Score value
    int score = 0;

    public abstract void playSound();
    
    public GameObject(int x, int y, int width, int height, Image img){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public void paintSelf(Graphics g){
        g.drawImage(img, x, y, width, height, null);
    }

    public Rectangle getRec(){
        return new Rectangle(x, y, width, height);
    }
}