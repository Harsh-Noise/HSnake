/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.enemies;

import com.harsh.entities.Entity;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class Wall extends Entity {
    public Wall(int x, int y, Random rand, BufferedImage[] newImgs) {
        super(x, y, rand);
        img = newImgs[0];
        lethal = true;
    }
}
