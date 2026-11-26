/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import com.harsh.entities.SnakeBody;
import com.harsh.entities.SnakeHead;
import com.harsh.entities.Sprite;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class TadBody extends SnakeBody{
    public BufferedImage tadtail;
    
    boolean newSpawn = false;
    
    public TadBody(int x, int y, int newDirection, int newLifespan, SnakeBody newNext, SnakeHead parent, Random rand, BufferedImage[] newImgs) {
        super(x, y, newDirection, newLifespan, newNext, parent, rand, newImgs);
        tadtail = newImgs[2];
    }
    
    @Override
    public void grow() {
        newSpawn = true;
    }
    
    @Override
    public int update(){
        lifespan--;
        if (lifespan <= 0) {
            toDelete = true;
            //System.out.println("A body segment is to delete " + lifespan);
        }
        if(newSpawn){
            //return(new Tad(xCoord, yCoord, rand, game));
            newSpawn = false;
            return (3);
        }
        return(0);
    }
    
    @Override
    public Sprite getSprite(){
        return(new Sprite(tadtail, xCoord, yCoord, direction));
    }
    
    @Override
    public int collision(Entity ent) {
        if(ent.isLethal()){
            toDelete = true;
        }
        return (0);
    }
}
