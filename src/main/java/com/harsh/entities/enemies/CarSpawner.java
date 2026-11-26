/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author Harsh Noise
 */
public class CarSpawner extends Entity{
    
    
    
    public CarSpawner(int x, int y, Random rand) {
        super(x, y, rand);
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/images/env/Blank.png"));
        } catch (IOException ex) {
            System.getLogger(CarSpawner.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        lethal = false;
    }
    
    public void move(){
        xCoord--;
    }
    
    @Override
    public int update(){
        if(xCoord == 0){
            toDelete = true;
            //return(null);
        }
        return(4);
    }
    
}
