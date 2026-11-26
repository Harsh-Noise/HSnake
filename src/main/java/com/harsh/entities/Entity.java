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
public class Entity {
    protected int lifespan = 1;
    protected int xCoord = 0;
    protected int yCoord = 0;
    protected int direction = 3;
    protected boolean lethal = true;
    protected boolean killable = false;
    protected Random rand;
    protected boolean toDelete = false;
    protected BufferedImage img;

    @Override
    public String toString() {
        return(this.getClass().getName() + '{' + "xCoord=" + xCoord + ", yCoord=" + yCoord + '}');
    }
    
    public Entity(int x, int y, Random rand) {
        xCoord = x;
        yCoord = y;
        this.rand = rand;
    }
    
    public int update(){
        return(0);
    }
    public Sprite getSprite(){
        return(new Sprite(img, xCoord, yCoord, direction));
    }
    
    public int collision(Entity ent){   //returns death code to instruct the game what to do
        if(ent.isLethal() && killable){
            toDelete = true;
        }
        return(0);
    }
    
    public void prune(){
        
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public void setDirection(int x) {
        direction = x;
    }
    
    public int getDirection() {
        return (direction);
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public BufferedImage getImg() {
        return img;
    }

    public boolean isLethal() {
        return lethal;
    }

    public void setLethal(boolean lethal) {
        this.lethal = lethal;
    }

    public boolean isKillable() {
        return killable;
    }

    public void setKillable(boolean killable) {
        this.killable = killable;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }
    
    
    
}
