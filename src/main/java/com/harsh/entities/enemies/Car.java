/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import com.harsh.entities.Sprite;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class Car extends Entity {    
    BufferedImage warning;
    BufferedImage carl;
    BufferedImage carr;
    
    public Car(int x, int y, Random rand, BufferedImage[] newImgs) {
        super(x, y, rand);
        lifespan = 8;
        lethal = false;
        warning = newImgs[0];
        carl = newImgs[1];
        carr = newImgs[2];
        
        /*
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/images/env/Blank.png"));
            warning = ImageIO.read(getClass().getResourceAsStream("/images/env/Warning.png"));
            carl = ImageIO.read(getClass().getResourceAsStream("/images/entity/TruckL.png"));
            carr = ImageIO.read(getClass().getResourceAsStream("/images/entity/TruckR.png"));
        } catch (IOException ex) {
            System.getLogger(Car.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
*/
    }
    
    /*
    @Override
    public Entity update(){
        if(lifespan == 0){
            toDelete = true;
            lethal = false;
            return(null);
        }
        if(lifespan < 3){
            lethal = true;
        }
        lifespan--;
        return(null);
    }
*/
    
    @Override
    public int update() {
        if (lifespan == 0) {
            toDelete = true;
            lethal = false;
            return (0);
        }
        if (lifespan < 3) {
            lethal = true;
        }
        lifespan--;
        return (0);
    }
    
    @Override
    public Sprite getSprite(){
        if(lifespan > 1){
            return(new Sprite(warning, xCoord, yCoord, direction));
        }
        
        if(lifespan == 1){
            return(new Sprite(carl, xCoord, yCoord, direction));
        }
        
        if(lifespan == 0){
            return(new Sprite(carr, xCoord, yCoord, direction));
        }
        
        return(new Sprite(warning, xCoord, yCoord, direction));
    }
    
    public BufferedImage[] getGallerySlides() {
        return (new BufferedImage[]{warning, warning, carl, carr, img});
    }
}
