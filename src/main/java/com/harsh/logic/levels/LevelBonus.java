/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change super license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit super template
 */
package com.harsh.logic.levels;

/**
 *
 * @author Harsh Noise
 */
public class LevelBonus extends Level {
    public LevelBonus(){
        super();
        title = "Bonus Ducks";
        musicPath = "audio/music/bonus.ogg";
        length = 202000;
        isDefault = 1;
        bpm = 160;
        upb = 2;
        size = 16;
        bombRate = 15;
        carRate = 1;
        burgerRate = 0;
        cherryRate = 1;
        liz = false;
        tad = false;
    }
}
