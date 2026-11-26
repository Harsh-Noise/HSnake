/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import com.harsh.entities.SnakeHead;
import com.harsh.entities.fruit.Cherry;
import com.harsh.entities.fruit.Patty;
import com.harsh.logic.SnakeGame;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class Liz extends SnakeHead{
    SnakeGame game;
    
    public Liz(int x, int y, Random rand, SnakeGame game, BufferedImage[] newImgs) {
        super(x, y, rand, newImgs);
        killable = true;
        this.game = game;
    }
    
    @Override
    public int update(){
        direction = game.getRivalMove(this);
        return super.update();
    }
    
    @Override
    public int collision(Entity ent) {   //returns death code to instruct the game what to do
        if (ent.isLethal()) {
            toDelete = true;
            return (0);
        }
        if (ent.getClass() == Cherry.class) {
            return (4);
        }
        if (ent.getClass() == Patty.class) {
            return (2);
        }
        return (0);
    }
    
    @Override
    public void prune(){
        chainPrune();
    }    
    
    private void chainPrune(){
        if (next == null) {
            return;
        }
        if (next.isToDelete()) {
            next = null;
        } else {
            next.prune();
        }
    }
}
