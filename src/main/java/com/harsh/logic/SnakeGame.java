/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.harsh.logic;

import com.harsh.logic.levels.*;
import com.harsh.entities.*;
import com.harsh.entities.enemies.*;
import com.harsh.entities.fruit.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author Harsh Noise
 */
public class SnakeGame {
    
    Level currentLevel = new LevelDebug();
    ArrayList<Entity> entities = new ArrayList();
    boolean isOver = false;
    boolean isWin = false;
    Random rand = new Random();
    SnakeHead player;
    int points = 0;
    int timer = 1;
    double startTime;
    
    public BufferedImage snakehead, snakebody, snaketail, snakeL, snakeR, cherry, patty, bomb, lizhead, tadhead, tadtail, carL, carR, warning, blank, error;
    
    public SnakeGame() {
        initImages();
        player = new SnakeHead(0, 0, rand, new BufferedImage[]{snakehead});
    }
    
    public void initialize(){
        addSnake();
        if(currentLevel.isLiz()){
            addEntity(11, currentLevel.getSize());
        }
        if(currentLevel.isTad()){
            addEntity(12, currentLevel.getSize());
        }
        addEntity(1, currentLevel.getSize());
        points = 0;
        timer = 1;
        startTime = System.currentTimeMillis();
    }
    
    public void fullTurn(){
        //System.out.println("Master list size " + entities.size());
        updateEntities();
        
        for(int x = 0; x < entities.size(); x++) {
            if (entities.get(x).getxCoord() > currentLevel.getSize()-1 || 0 > entities.get(x).getxCoord() || entities.get(x).getyCoord() > currentLevel.getSize()-1 || 0 > entities.get(x).getyCoord()) {
                entities.get(x).setToDelete(true);
            }
        }
        //System.out.println("updated");
        if(timer%currentLevel.getCarRate() == 0){
            addEntity(10, currentLevel.getSize());
        }
        /*
        if(timer%8 == 0){
            debugEntSpawner();
        }
*/
        pruneEntities();
        //System.out.println("pruned");
        Entity[] collisions = getCollisions();
        //System.out.println("Entities: " + entities.size() + " Snake: " + player.getLength() + " Collisions: " + collisions.length);
        
        for(int x = 0; x < collisions.length; x++){
            int colCode;
            if(x%2 == 0){
                colCode = collisions[x].collision(collisions[x+1]);
            }else{
                colCode = collisions[x].collision(collisions[x-1]);
            }
            switch(colCode){
                case 0: //do nothing
                    break;
                case 1:
                    //player.grow();
                    points += (100 + player.getBonus());
                    player.grow();
                    cherryCollect();
                    break;
                case 2:
                    points += 50;
                    burgerCollect();
                    if (currentLevel.isLiz()) {
                        addEntity(11, currentLevel.getSize());
                    }
                    if (currentLevel.isTad()) {
                        addEntity(12, currentLevel.getSize());
                    }
                    break;
                case 3: //shorten snake
                    ((SnakeBody)collisions[x]).getParent().shrinkBy(collisions[x].getLifespan());
                    break;
                case 4: //rivalCherry
                    //rival.grow();
                    points -= (100 + ((Liz)collisions[x]).getBonus());
                    ((Liz)collisions[x]).grow();
                    cherryCollect();
                    break;
                case 5: //tad cherry
                    points -= 50;
                    cherryCollect();
                    break;
                case 6: //rival burger
                    points -= 50;
                    burgerCollect();
                    break;
                case 7: //Liz shrink
                    ((LizBody)collisions[x]).getParent().shrinkBy(collisions[x].getLifespan());
                    break;
            }
        }
        
        pruneEntities();
        
        if(player.isToDelete()){
            isOver = true;
        }
        
        if(System.currentTimeMillis() - startTime >= currentLevel.getLength()){
            isOver = true;
            isWin = true;
        }
        
        timer++;
    }
    
    public void addSnake(){
        player = new SnakeHead(currentLevel.getSize()/2, currentLevel.getSize()/2, rand, new BufferedImage[]{snakehead});
        player.setDirection(3);
        entities.add(player);
    }
    
    public void cherryCollect(){
        //System.out.println("Collided with cherry!");

        addEntity(1, currentLevel.getSize());
        for(int x = 0; x < currentLevel.getBombRate(); x++){
            addEntity(0, currentLevel.getSize());
        }        
        if (rand.nextDouble() < currentLevel.getCherryRate()) {  //chance to add additional cherry
            addEntity(1, currentLevel.getSize());
        }
        if (rand.nextDouble() < currentLevel.getBurgerRate()) {  //chance to spawn burger
            addEntity(2, currentLevel.getSize());
        }
    }
    
    public void burgerCollect(){
        for (int x = 0; x < entities.size(); x++) {
            if (entities.get(x).getClass() == Bomb.class) {
                if (rand.nextDouble() < 0.5) {
                    entities.remove(x);
                }
            }
        }
    }
       
    public void addEntity(int entity, int size){
        int x = 0;
        int y = 0;
        if(entity != 10){    //if not a car
            if(entities.size() >= size*size){
                return;
            }
            int count;

            boolean unique = true;
            eternalLoop:
            for(count = 0; count < 100; count++){
                unique = true;
                x = rand.nextInt(size);
                y = rand.nextInt(size);
                for (Entity c : entities) {
                    if(x == c.getxCoord() && y == c.getyCoord()){
                        unique = false;
                    }
                    if(Math.abs(x - player.getxCoord()) < 3 && Math.abs(y - player.getyCoord()) < 3){
                        unique = false;
                    }
                }
                if(unique){
                    break;
                }
            }

            if(!unique){
                System.out.println("[!!!] Could not spawn entity!");
                return;
            }
        }
        switch(entity){
            case 0: //bomb
                entities.add(new Bomb(x, y, rand, new BufferedImage[]{bomb}));
                break;
            case 1: //cherry
                entities.add(new Cherry(x, y, rand, new BufferedImage[]{cherry}));
                break;
            case 2:
                entities.add(new Patty(x, y, rand, new BufferedImage[]{patty}));
                break;
            case 10:
                entities.add(new CarSpawner(size-1, rand.nextInt(size), rand));
                break;
            case 11:
                //rival = new Liz(8, 0, rand);
                entities.add(new Liz(x, y, rand, this, new BufferedImage[]{lizhead}));
                break; 
            case 12:
                entities.add(new Tad(x, y, rand, this, new BufferedImage[]{tadhead}));
                break; 
        }
        //System.out.println("Adding ID" + entity + '{' + "xCoord=" + x + ", yCoord=" + x + '}');
    }
    
    public Entity getEntity(int x, int y){
        for (Entity c : entities) {
            if(c.getxCoord() == x){
                if (c.getyCoord() == y){
                    return c;
                }
            }
        }
        if(x == -1 || y == -1 || x == currentLevel.getSize() || y == currentLevel.getSize()){
            return(new Wall(x, y, rand, new BufferedImage[]{blank}));
        }
        return null;
    }
    
    public int[] rankDirections(Liz rival){
        int[] returnArray = new int[]{0, 0, 0, 0};  //cast ray in cardinal directions
        for(int a = rival.getyCoord(); a > -1; a--){   //up
            Entity tempEnt = getEntity(rival.getxCoord(), a);
            if(tempEnt == null){
            }else if(tempEnt.isLethal()){
                returnArray[0]++;
            }else if(tempEnt.getClass() == Cherry.class){
                returnArray[0] -= 3;
            }
        }
        for(int a = rival.getyCoord(); a < currentLevel.getSize(); a++){   //down
            Entity tempEnt = getEntity(rival.getxCoord(), a);
            if(tempEnt == null){
            }else if(tempEnt.isLethal()){
                returnArray[2]++;
            }else if(tempEnt.getClass() == Cherry.class){
                returnArray[2] -= 3;
            }
        }
        for (int a = rival.getxCoord(); a > -1; a--) {   //left
            Entity tempEnt = getEntity(a, rival.getyCoord());
            if (tempEnt == null) {
            } else if (tempEnt.isLethal()) {
                returnArray[3]++;
            } else if (tempEnt.getClass() == Cherry.class) {
                returnArray[3] -= 3;
            }
        }
        for (int a = rival.getxCoord(); a < currentLevel.getSize(); a++) {   //right
            Entity tempEnt = getEntity(a, rival.getyCoord());
            if (tempEnt == null) {
            } else if (tempEnt.isLethal()) {
                returnArray[1]++;
            } else if (tempEnt.getClass() == Cherry.class) {
                returnArray[1] -= 3;
            }
        }
        return returnArray;
    }
    
    public int getRivalMove(Liz rival){
        //System.out.println("Gunna rank these moves");
        int[] moves = rankDirections(rival);
        //System.out.println("Ranked em " + moves[0] + ", " + moves[1] + ", " + moves[2] + ", " + moves[3]);
        
        //invalidate backwards
        moves[(rival.getDirection()+2)%4] = Integer.MAX_VALUE; //I should add a thing here so that liz is forced to collect an adjacent cherry
        //invalidate immediate obstacles
        if (getEntity(rival.getxCoord(), rival.getyCoord()-1)!=null) {
            if (getEntity(rival.getxCoord(), rival.getyCoord() - 1).isLethal()) { //up
                moves[0] = Integer.MAX_VALUE;
            }
        }
        if (getEntity(rival.getxCoord()+1, rival.getyCoord())!=null) {
            if (getEntity(rival.getxCoord() + 1, rival.getyCoord()).isLethal()) { //right
                moves[1] = Integer.MAX_VALUE;
            }
        }
        if (getEntity(rival.getxCoord(), rival.getyCoord()+1)!=null) {
            if (getEntity(rival.getxCoord(), rival.getyCoord() + 1).isLethal()) { //down
                moves[2] = Integer.MAX_VALUE;
            }
        }
        if (getEntity(rival.getxCoord()-1, rival.getyCoord())!=null) {
            if (getEntity(rival.getxCoord() - 1, rival.getyCoord()).isLethal()) { //left
                moves[3] = Integer.MAX_VALUE;
            }
        }
        //System.out.println("Invalidated some " + moves[0] + ", " + moves[1] + ", " + moves[2] + ", " + moves[3]);
        
        //return min
        //randomize rotation bias
        int min = rival.getDirection();
        if(rand.nextBoolean()){
            for(int x = rival.getDirection(); x < rival.getDirection() + 4; x++){
                if(moves[x%4] < moves[min]){
                    min = x%4;
                }
            }   
        }else{
           for(int x = rival.getDirection() + 4; x > rival.getDirection(); x--){
                if(moves[x%4] < moves[min]){
                    min = x%4;
                }
            }  
        }
        
        //System.out.println("Best move is " + min);
        return(min);
    }
    
    public boolean isValidDirection(int direction) {
        return (player.isValidDirection(direction));
    }
    
    public void setDirection(int direction) {
        player.changeDirection(direction);
    }
    
    public void updateEntities() {
        for (int x = 0; x < entities.size(); x++) {
            switch(entities.get(x).update()){
                case 0:{
                    break;  //do nothing
                }
                case 1:{     //Snakehead move
                    SnakeHead tempHead = (SnakeHead) entities.get(x);
                    SnakeBody temp = new SnakeBody(tempHead.getxCoord(), tempHead.getyCoord(), tempHead.getDirection(), tempHead.getLifespan(), tempHead.getNext(), tempHead, rand, new BufferedImage[]{snakehead, snakebody, snaketail, snakeL, snakeR});
                    tempHead.move();
                    tempHead.setNext(temp);
                    entities.add(x+1, temp);
                    x++;
                    break;
                }
                case 2:{     //SnakeHead move w/o new next
                    ((SnakeHead) entities.get(x)).move();
                    break;
                }
                case 3:{     //Spawn tad
                    TadBody temp = (TadBody) entities.get(x);
                    Tad newTad = new Tad(temp.getxCoord(), temp.getyCoord(), rand, this, new BufferedImage[]{tadhead});
                    newTad.setDirection(temp.getDirection());
                    //TadBody newBody = new TadBody(newTad.getxCoord(), newTad.getyCoord(), newTad.getDirection(), newTad.getLifespan(), newTad.getNext(), newTad, rand,  new BufferedImage[]{snakehead, snakebody, tadtail, snakeL, snakeR});
                    //newTad.setNext(newBody);
                    entities.add(x+1, newTad);
                    //entities.add(x+1, newBody);
                    //newTad.move();
                    x+=1;
                    break;
                }
                case 4:{     //Spawn car
                    CarSpawner temp = (CarSpawner) entities.get(x);
                    entities.add(x+1, new Car(temp.getxCoord(), temp.getyCoord(), rand, new BufferedImage[]{warning, carL, carR}));
                    temp.move();
                    break;
                }
                case 5:{    //tad move
                    SnakeHead tempHead = (SnakeHead) entities.get(x);
                    TadBody temp = new TadBody(tempHead.getxCoord(), tempHead.getyCoord(), tempHead.getDirection(), tempHead.getLifespan(), tempHead.getNext(), tempHead, rand, new BufferedImage[]{snakehead, snakebody, tadtail, snakeL, snakeR});
                    tempHead.move();
                    tempHead.setNext(temp);
                    entities.add(x+1, temp);
                    x++;
                    break;
                }
                default:
                    System.out.println("[???] Entity triggered default return on update " + entities.get(x).toString());
            }
        }
    }
    
    public void pruneEntities(){
        //System.out.println("Checking for deleted entities");
        //player.prune();
        //int count = 0;
        for (int x = 0; x < entities.size(); x++) {
            entities.get(x).prune();
            if(entities.get(x).isToDelete()){
                //count++;
                //System.out.println("Removing an entity");
                entities.remove(x);
                x--;
            }
        }
        //System.out.println("Pruned " + count + " ents");
    }
    
    public Sprite[] getSprites(){
        Sprite[] returnArray = new Sprite[entities.size()];
        for (int x = 0; x < entities.size(); x++) {
            returnArray[x] = entities.get(x).getSprite();
        }
        return(returnArray);
    }

    public Entity[] getCollisions(){
        ArrayList<Entity> returnList = new ArrayList<>();
        for(int x = 0; x < entities.size(); x++){
            for(int y = x; y < entities.size(); y++){
                if(y == x){
                    continue;
                }
                if(entities.get(x).getxCoord() != entities.get(y).getxCoord()){
                    continue;
                }
                if(entities.get(x).getyCoord() != entities.get(y).getyCoord()){
                    continue;
                }
                returnList.add(entities.get(x));
                returnList.add(entities.get(y));
            }
        }
        if(returnList.isEmpty()){
            return(new Entity[0]);
        }
        return(returnList.toArray(Entity[]::new));
    }
    
    public void endGame(){
        entities.clear();
        player.deleteAll();
        player = null;
        player = new SnakeHead(0, 0, rand, new BufferedImage[]{snakehead});
    }
    
    private BufferedImage getResource(String location){
        try {
            return(ImageIO.read(getClass().getResourceAsStream(location)));
        } catch (IOException ex) {
            System.out.println("[!!!] Error initializing sprite located at " + location);
            System.getLogger(SnakeGame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        try {
            return(ImageIO.read(getClass().getResourceAsStream("/images/env/Default.png")));
        } catch (IOException ex) {
            System.out.println("[!!!] Error initializing default sprite");
            System.getLogger(SnakeGame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return(null);
    }
    
    private void initImages(){
        snakehead = getResource("/images/entity/SnakeHead.png");
        snakebody = getResource("/images/entity/SnakeBelly.png");
        snaketail = getResource("/images/entity/SnakeTail.png");
        snakeL = getResource("/images/entity/SnakeTurnLeft.png");
        snakeR = getResource("/images/entity/SnakeTurnRight.png");
        cherry = getResource("/images/entity/Cherry.png");
        patty = getResource("/images/entity/Patty.png");
        bomb = getResource("/images/entity/Bomb2.png");
        lizhead = getResource("/images/entity/LizHead.png");
        tadhead = getResource("/images/entity/TadHead.png");
        tadtail = getResource("/images/entity/TadTail.png");
        carL = getResource("/images/entity/TruckL.png");
        carR = getResource("/images/entity/TruckR.png");
        warning = getResource("/images/env/Warning.png");
        blank = getResource("/images/env/Blank.png");
        error = getResource("/images/env/Default.png");
    }
    
    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
    }

    public boolean isOver() {
        return isOver;
    }
    
    public boolean isWin() {
        return isWin;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }
    
    public void setIsWin(boolean isWin) {
        this.isWin = isWin;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    public int getProgress(){
        return(int) (((System.currentTimeMillis() - startTime)*100)/currentLevel.getLength());
    }
    
    
}
