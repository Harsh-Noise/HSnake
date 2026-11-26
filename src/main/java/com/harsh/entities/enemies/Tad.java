/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import com.harsh.entities.fruit.Cherry;
import com.harsh.entities.fruit.Patty;
import com.harsh.logic.SnakeGame;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class Tad extends Liz{
    public Tad(int x, int y, Random rand, SnakeGame game, BufferedImage[] newImgs) {
        super(x, y, rand, game, newImgs);
    }
    
    @Override
    public int update(){
        super.update();
        return(5);
    }
    
    @Override
    public int collision(Entity ent) {   //returns death code to instruct the game what to do
        if (ent.isLethal()) {
            toDelete = true;
            return (0);
        }
        if (ent.getClass() == Cherry.class) {
            next.grow();
            return (5);
        }
        if (ent.getClass() == Patty.class) {
            return (2);
        }
        return (0);
    }
    
    @Override
    public void grow(){
        next.grow();
    }
}
