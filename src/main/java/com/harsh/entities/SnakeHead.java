/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities;

import com.harsh.entities.fruit.Cherry;
import com.harsh.entities.fruit.Patty;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class SnakeHead extends Entity{
    public SnakeBody next;
    int prevDirection = direction;
    //boolean grow = false;

    public SnakeHead(int x, int y, Random rand, BufferedImage[] newImgs) {
        super(x, y, rand);
        img = newImgs[0];
        direction = 0;
        lifespan = 1;
        killable = true;
    }
    
    @Override
    public int collision(Entity ent){   //returns death code to instruct the game what to do
        if(ent.isLethal()){
            toDelete = true;
            return(0);
        }
        if(ent.getClass() == Cherry.class){
            return(1);
        }
        if(ent.getClass() == Patty.class){
            return(2);
        }
        return(0);
    }
    
    public boolean isValidDirection(int x){
        return(x != prevDirection-2 && x != prevDirection+2);
    }
    
    public void changeDirection(int x) {
        if(x == prevDirection-2 || x == prevDirection+2){
            return;
        }
        direction = x;
    }
    
    public void move(){
        prevDirection = direction;
        switch(direction){
            case 0 -> //up
                super.yCoord--;
            case 1 -> //right
                super.xCoord++;
            case 2 -> //down
                super.yCoord++;
            case 3 -> //left
                super.xCoord--;
        }
        
    }
    
    public void grow(){
        if(next == null){
            lifespan++;
            return;
        }
        next.grow();
        lifespan++;
    }
    
    public void shrinkBy(int value){
        if(next == null){
            lifespan -= value;
            return;
        }
        lifespan -= value;
        next.shrinkBy(value);
    }
    
    @Override
    public int update(){
        //SnakeBody temp = newNext();
        //move();
        if (lifespan == 0) {
            return 2;
        }
        return 1;
    }
    
    @Override
    public void prune(){
        chainPrune();
    }
    
    private void chainPrune(){
        if (next == null) {
            return;
        }
        if (next.toDelete) {
            next = null;
        } else {
            next.prune();
        }
    }
    
    public void deleteAll(){
        if(next == null){
            return;
        }
        next.deleteAll();
        next = null;
    }
    
    public int getBonus(){
        return((100*lifespan)/(lifespan+1));
    }

    public SnakeBody getNext() {
        return next;
    }

    public void setNext(SnakeBody next) {
        this.next = next;
    }

    public int getPrevDirection() {
        return prevDirection;
    }

    public void setPrevDirection(int prevDirection) {
        this.prevDirection = prevDirection;
    }
    
    
}
