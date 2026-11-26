/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities.fruit;

import com.harsh.entities.Entity;
import com.harsh.entities.enemies.Car;
import java.util.Random;

/**
 *
 * @author Harsh Noise
 */
public class Fruit extends Entity {
    public int points;

    public Fruit(int x, int y, Random rand) {
        super(x, y, rand);
        lethal = false;
        killable = true;
    }
    
    @Override
    public int collision(Entity ent) {   //returns death code to instruct the game what to do
        if (ent.getClass() == Car.class) {
            return (0);
        }
        if (ent.isLethal()) {
            toDelete = true;
        }
        return (0);
    }
}
