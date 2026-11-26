/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.fruit;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class Cherry extends Fruit{
    
    public Cherry(int x, int y, Random rand, BufferedImage[] newImgs) {
        super(x, y, rand);
        img = newImgs[0];
        points = 1;
    }
}
