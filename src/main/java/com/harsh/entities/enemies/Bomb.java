/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import com.harsh.entities.SnakeHead;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class Bomb extends Entity{
    
    public Bomb(int x, int y, Random rand, BufferedImage[] newImgs) {
        super(x, y, rand);
        img = newImgs[0];
        killable = true;
    }
    
    @Override
    public int collision(Entity ent){   //returns death code to instruct the game what to do
        if(ent.getClass() == SnakeHead.class){
            return(0);
        }
        if (ent.isLethal()) {
            toDelete = true;
        }
        return(0);
    }
    
}
