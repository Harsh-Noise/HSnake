/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Harsh Noise
 */
public class Sprite {
    BufferedImage img;
    int xCoord;
    int yCoord;
    //int rotation;
    
    public Sprite(int newXCoord, int newYCoord){
        xCoord = newXCoord;
        yCoord = newYCoord;
        //this.rotation = rotation;
    }
    
    public Sprite(BufferedImage newImg, int newXCoord, int newYCoord, int rotation){
        img = rotate(newImg, rotation);
        xCoord = newXCoord;
        yCoord = newYCoord;
        //this.rotation = rotation;
    }
    
    private BufferedImage rotate(BufferedImage img, int rotation){
        BufferedImage output = new BufferedImage(32, 32, img.getType());
        Graphics2D g2d = output.createGraphics();
        
        switch(rotation){   //sprites default to facing left, which unfortunately translates to a rotation value of 3. Very ugly system :(
            case 0:
                g2d.rotate(1.5707963268, 16, 16);
                break;
            case 1:
                g2d.rotate(3.1415926536, 16, 16);
                break;
            case 2:
                g2d.rotate(4.7123889804, 16, 16);
                break;
            case 3:
                g2d.rotate(0, 16, 16);
                break;
        }
        
        g2d.drawRenderedImage(img, null);
        g2d.dispose();
        return(output);
    }
    
    public void setImg(BufferedImage x) {
        img = x;
    }

    public BufferedImage getImg() {
        return (img);
    }

    public void setXCoord(int x) {
        xCoord = x;
    }

    public int getXCoord() {
        return (xCoord);
    }

    public void setYCoord(int x) {
        yCoord = x;
    }

    public int getYCoord() {
        return (yCoord);
    }
}
