/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import com.harsh.entities.SnakeBody;
import com.harsh.entities.SnakeHead;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class LizBody extends SnakeBody {
    
    public LizBody(int x, int y, int newDirection, int newLifespan, SnakeBody newNext, SnakeHead parent, Random rand, BufferedImage[] newImgs) {
        super(x, y, newDirection, newLifespan, newNext, parent, rand, newImgs);
    }
    
    @Override
    public int collision(Entity ent){   //impliment liz tail shrinking at some point
        if (ent.isLethal()) {
            toDelete = true;
            return (7);
        }
        return (0);
    }
    
    @Override
    public BufferedImage[] getGallerySlides() {
        return (new BufferedImage[]{img, snakebody, snaketail});
    }
}
