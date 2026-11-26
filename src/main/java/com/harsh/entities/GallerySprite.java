/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities;

import java.awt.image.BufferedImage;

/**
 *
 * @author Harsh Noise
 */
public class GallerySprite extends Sprite{
    BufferedImage[] imgs; //animation states to cycle through (always 5 long)
    int rotation;   //int representing the current animation state
    
    public GallerySprite(int newXCoord, int newYCoord, int rotation) {
        super(newXCoord, newYCoord);
        this.rotation = rotation;
    }
    
    public GallerySprite(BufferedImage[] imgs) {
        super(0, 0);
        rotation = 0;
        this.imgs = imgs;
    }
    
    public Sprite[] getSprites(){
        Sprite[] returnSprites = new Sprite[5];
        for(int x = 0; x < 5; x++){
            int iteration = ((x + 5) - rotation) % 5;
            returnSprites[x] = new Sprite(imgs[x], iteration, 2, 3);
        }
        return(returnSprites);
    }
    
    public void setImgs(BufferedImage[] x) {
        imgs = x;
    }

    public BufferedImage[] getImgs() {
        return (imgs);
    }
    
    @Override
    public BufferedImage getImg() {
        return (imgs[rotation]);
    }
    
    public void iterate(){
        rotation = (rotation+1)%5;  //Cycle to the next of 5 frames or loop back to the start
    }
    
    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    
    
}
