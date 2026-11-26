/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class SnakeBody extends SnakeHead{
    SnakeHead parent;
    public BufferedImage snakebody;
    public BufferedImage snaketail;
    public BufferedImage snakeL;
    public BufferedImage snakeR;
    
    //SnakeHead parent;
    
    public SnakeBody(int x, int y, int newDirection, int newLifespan, SnakeBody newNext, SnakeHead parent, Random rand, BufferedImage[] newImgs) {
        super(x, y, rand, newImgs);
        snakebody = newImgs[1];
        snaketail = newImgs[2];
        snakeL = newImgs[3];
        snakeR = newImgs[4];
        super.lifespan = newLifespan;
        super.direction = newDirection;
        super.next = newNext;
        this.parent = parent;
    }
    
    @Override
    public int update(){
        //System.out.println("A body segment is updating " + lifespan);
        lifespan--;
        if(lifespan <= 0){
            toDelete = true;
            //System.out.println("A body segment is to delete " + lifespan);
        }
        return 0;
    }
    
    @Override
    public int collision(Entity ent){   //returns death code to instruct the game what to do
        if(ent.getClass() == SnakeHead.class){
            return(0);
        }
        if(ent.isLethal()){
            toDelete = true;
            return (3);
        }
        return(0);
    }
    
    @Override
    public void grow(){
        if(next == null){
            lifespan++;
            return;
        }
        next.grow();
        lifespan++;
    }
    
    @Override
    public void prune(){
        if (next == null) {
            return;
        }
        if (next.toDelete) {
            next = null;
        } else {
            next.prune();
        }
    }
    
    @Override
    public Sprite getSprite(){
        if(next == null){
            return(new Sprite(snaketail, xCoord, yCoord, direction));
        }
        
        int diff = (next.getDirection() - direction + 4) % 4;
        
        if(diff == 1){
            return(new Sprite(snakeR, xCoord, yCoord, direction));
        }
        if(diff == 3){
            return(new Sprite(snakeL, xCoord, yCoord, direction));
        }
        return(new Sprite(snakebody, xCoord, yCoord, direction));
    }
    
    public BufferedImage[] getGallerySlides(){
        return (new BufferedImage[]{img, snakebody, snaketail});
    }

    public SnakeHead getParent() {
        return parent;
    }
    
    
}
