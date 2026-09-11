import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Background{
    //List of all game objects
    List<GameObject> objects = new ArrayList<>();
    //Total score
    static int totalScore = 0;
    //Current level
    static int level = 1;
    //Target score for current level
    int goalScore = 0;
    //Timer variables
    long startTime;
    long endTime;
    //Initialize level data
    public void init(){
        objects.clear(); 
        startTime = System.currentTimeMillis();
        
        //Set difficulty parameters
        switch (level){
            case 1:
                goalScore = 2500; //Easy goal
                endTime = startTime + 40 * 1000; 
                spawnObjects(4, 3, 1, 1, 1, 0 , 0); 
                break;
            case 2:
                goalScore = 5000; //Medium goal
                endTime = startTime + 35 * 1000; 
                spawnObjects(3, 4, 2, 2, 1, 1 , 0); 
                break;
            case 3:
                goalScore = 10000; //Hard goal
                endTime = startTime + 35 * 1000;
                spawnObjects(2, 3, 3, 4, 2 , 2, 1); 
                break;
            default: //Endless mode
                goalScore = level * 4000;
                endTime = startTime + 30 * 1000;
                spawnObjects(5, 5, 5, 5, 3 , 5,  1);
                break;
        }
    }
    //Prevent object overlap
    private void placeObject(GameObject obj,int widthx, int startx){
        boolean isOverlapping;
        int attempts = 0;
        
        do{
            isOverlapping = false;
            //Random X and Y
            obj.x = (int)(Math.random() * 700);
            obj.y = (int)(Math.random() * widthx) + startx;
            
            Rectangle nextRect = obj.getRec();
            for (GameObject existingObj : objects){
                if (nextRect.intersects(existingObj.getRec())){
                    isOverlapping = true;
                    break;
                }
            }
            attempts++;
        } while (isOverlapping && attempts < 50);

        if (!isOverlapping){
            objects.add(obj);
        }
    }





    //Spawn multiple objects
    private void spawnObjects(int sGold, int mGold, int lGold, int srock, int lrock, int tnt , int dimond){
        
        for (int i = 0; i < srock; i++){
            placeObject(new Rock(0, 0, 40, 40, "imgs/small_stone.png", 60, 20), 100, 250);
        }
        for (int i = 0; i < lrock; i++){
            placeObject(new Rock(0, 0, 50, 50, "imgs/large_stone.png", 100, 40), 100, 250);
        }
        for (int i = 0; i < tnt; i++){
            placeObject(new TNT(0, 0, 40, 60, "imgs/tnt.png", 1, -500), 100, 250); 
        }

        for (int i = 0; i < sGold; i++){
            placeObject(new Gold(0, 0, 36, 36, "imgs/small_gold.png", 35, 200), 250, 300);
        }
        for (int i = 0; i < mGold; i++){
            placeObject(new Gold(0, 0, 52, 52, "imgs/median_gold.png", 50, 500), 250, 300);
        }
        for (int i = 0; i < lGold; i++){
            placeObject(new Gold(0, 0, 72, 72, "imgs/large_gold.png", 80, 1000), 250, 300);
        }

        for (int i = 0; i < dimond; i++){
            placeObject(new Dimond(0, 0, 40, 40, "imgs/dimond.png", 20, 1500), 100, 450); 
        }
    }

    //Draw all components
    public void paintSelf(Graphics g){
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        g.drawString("Level: " + level, 30, 100);
        
        //Change color to green if target reached
        if (totalScore >= goalScore){
            g.setColor(Color.GREEN);
        } else{
            g.setColor(Color.BLACK);
        }
        g.drawString("Score: " + totalScore + " / " + goalScore, 30, 130);
        
        //Draw timer
        long timeLeft = (endTime - System.currentTimeMillis()) / 1000;
        g.setColor(timeLeft < 10 ? Color.RED : Color.BLACK); 
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Time: " + (timeLeft > 0 ? timeLeft : 0), 600, 130);

        //Draw objects
        for (GameObject obj : objects){
            obj.paintSelf(g);
        }
    }

    public boolean gameTime(){
        return System.currentTimeMillis() > endTime;
    }
    
    public boolean isReachGoal(){
        return totalScore >= goalScore;
    }
}