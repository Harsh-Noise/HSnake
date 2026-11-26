/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.logic.sound;

/**
 *
 * @author Harsh Noise
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Rafiul
 */
public class Sound {
    
    private Clip clip;
    private volatile boolean running,looping;
    
    public Sound(String location){
        looping = running = false;
        System.out.println("Loading sound " + getClass().getClassLoader().getResource(location));
        try {
            loadAndDecode(AudioSystem.getAudioInputStream(getClass().getClassLoader().getResource(location)));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.getLogger(Sound.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public Sound(File audioFile){
        looping = running = false;
        try {
            loadAndDecode(AudioSystem.getAudioInputStream(audioFile));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.getLogger(Sound.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * Loads audio (e.g. OGG) from an in-memory byte array, such as one read
     * out of a packed level file, instead of a classpath resource or File.
     * @param audioData
     */
    public Sound(byte[] audioData){
        //System.out.print(HexFormat.of().formatHex(audioData));
        
        looping = running = false;
        try {
            // ByteArrayInputStream supports mark()/reset() out of the box,
            // which is what AudioSystem needs to sniff the stream's format,
            // so no BufferedInputStream wrapper is required here.
            System.out.println("Audiodata null status @ Sound.java " + (audioData == null));
            loadAndDecode(AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioData)));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.getLogger(Sound.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * Shared by all three constructors: takes the raw, still-encoded stream
     * (e.g. OGG Vorbis, decoded via whatever AudioFileReader SPI is on the
     * classpath), converts it to signed 16-bit PCM, and opens it on a Clip.
     */
    private void loadAndDecode(AudioInputStream oggStream) throws IOException, LineUnavailableException {
        try (oggStream) {
            AudioFormat baseFormat = oggStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );
            AudioInputStream pcmStream
                    = AudioSystem.getAudioInputStream(decodedFormat, oggStream);
            clip = AudioSystem.getClip();
            clip.open(pcmStream);
        }
    }
    
    public void play() {
        running = true;
        clip.setFramePosition(0);
        new Thread(() -> {
            clip.start();
            //System.out.println("clip started!");
            while(true){
                if(clip.getMicrosecondPosition() == clip.getMicrosecondLength())
                    break;
                if(!running){
                    //System.out.println("Breaking out of music");
                    break;
                }
            }
        }).start();
    }
    public void pause(){
        running = false;
        clip.stop();
    }
    public void resume(){
        running = true;
        new Thread(() -> {
            clip.start();
            while(true){
                if(clip.getMicrosecondPosition() == clip.getMicrosecondLength())
                    break;
                if(!running)
                    break;
            }
        }).start();
    }
    public void stop(){
        running = false;
        looping = false;
        clip.setFramePosition(0);
        clip.stop();
    }
    public void loop(){
        looping = true;
        new Thread(() -> {
            clip.start();
            while(looping){
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }).start();
    }
    public boolean isResumed(){
        return clip.getMicrosecondPosition()>0;
    }
}