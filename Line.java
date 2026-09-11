import java.awt.*;
import java.awt.geom.AffineTransform;

public class Line{
    //Start coordinates
    int x = 380;
    int y = 180;
    //End coordinates
    int endx = 500;
    int endy = 500;

    //Hook properties
    double length = 50;
    double min_length = 50;  
    double max_length = 750;
    
    double n = 0;
    int dir = 1;
    
    //State: 0=Swing, 1=Extend, 2=Retract, 3=Pulling
    int state = 0;
    
    //Hook speed
    private double HOOK_SPEED = 5; 

    GameObject capturedObj = null;
    GoldMiner frame;
    //Sound
    Sound ropeSound = new Sound("Sound/Rope.wav");

    public Line(GoldMiner frame){
        this.frame = frame;
    }

    void logic(){
        //Swing logic
        if (state == 0){
            if (n > 0.9){ dir = -1; }
            if (n < 0.1){ dir = 1; }
            n = n + 0.005 * dir;
            endx = (int) (x + length * Math.cos(n * Math.PI));
            endy = (int) (y + length * Math.sin(n * Math.PI));
        } 
        //Extend logic
        else if (state == 1){
            length += HOOK_SPEED;
            endx = (int) (x + length * Math.cos(n * Math.PI));
            endy = (int) (y + length * Math.sin(n * Math.PI));

            if (length > max_length || endx < 0 || endx > 800 || endy > 600){
                state = 2;
            }
            
            //Collision detection
            for (GameObject obj : frame.bg.objects){
                if (obj.getRec().contains(endx, endy) && !obj.flag){
                    state = 3;
                    capturedObj = obj;
                    obj.flag = true;
                    ropeSound.loop();
                    break;
                }
            }
        } 
        //Retract logic (empty)
        else if (state == 2){
            length -= HOOK_SPEED;
            if (length <= min_length){
                length = min_length;
                state = 0;
            }
        } 
        //Pull logic (with object)
        else if (state == 3){
            double speed = 100.0 / capturedObj.m; 
            if (speed < 1){
                speed = 1; 
            }
            if (speed > HOOK_SPEED){
            speed = HOOK_SPEED;
            }
            length -= speed;
            capturedObj.x = endx - capturedObj.width / 2;
            capturedObj.y = endy - capturedObj.height / 2;

            if (length <= min_length){
                length = min_length;
                // play sound
                ropeSound.stop();
                ropeSound.setFramePosition(0);
                capturedObj.playSound();
                //
                Background.totalScore += capturedObj.score;
                frame.bg.objects.remove(capturedObj);
                capturedObj = null;
                state = 0;
            }
        }
        
        if (state != 0){ // time when nothing happend
            endx = (int) (x + length * Math.cos(n * Math.PI));
            endy = (int) (y + length * Math.sin(n * Math.PI));
        }
    }

    void lines(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(60, 40, 20));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y, endx, endy);
        drawHook(g2d);
    }

    private void drawHook(Graphics2D g2d){
        AffineTransform old = g2d.getTransform();
        g2d.translate(endx, endy);
        g2d.rotate(n * Math.PI - Math.PI / 2);//let the hook rotate
        g2d.setColor(Color.DARK_GRAY);
        int[] shaftX ={-4, 4, 4, -4};
        int[] shaftY ={0, 0, 30, 30};
        g2d.fillPolygon(shaftX, shaftY, 4);
        if (state == 3){ 
            g2d.fillPolygon(new int[]{-4, -12, -8, -4}, new int[]{30, 45, 25, 20}, 4);
            g2d.fillPolygon(new int[]{4, 12, 8, 4}, new int[]{30, 45, 25, 20}, 4);
        } else{
            g2d.fillPolygon(new int[]{-4, -22, -14, -4}, new int[]{30, 42, 22, 18}, 4);
            g2d.fillPolygon(new int[]{4, 22, 14, 4}, new int[]{30, 42, 22, 18}, 4);
        }
        g2d.setTransform(old);
    }

    public void handlePause() {
        ropeSound.stop(); 
    }

    public void handleResume() {
        if (state == 3) {
            ropeSound.loop();
        }
    }
}