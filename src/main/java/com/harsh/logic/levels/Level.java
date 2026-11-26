/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.harsh.logic.levels;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 *
 * @author Harsh Noise
 */
public class Level {

    //0 = uncompressed
    //1 = zipped
    private static final int HLVL_VERSION = 1;

    //Name of the single file stored inside the zip archive.
    private static final String ZIP_ENTRY_NAME = "data.hlvl";
    
    //File path of saved levels
    static String levelDirectory = System.getProperty("user.home") + "/.levels";

    String title;
    String musicPath;
    String nameOne;
    int scoreOne;
    String nameTwo;
    int scoreTwo;
    String nameThree;
    int scoreThree;
    int isDefault;
    double length; //music lenth in miliseconds
    int bpm;
    int upb;    //updates per beat
    int size;   //side length of game
    int bombRate;   //bombs spawned per cherry
    int carRate;    //updates between car spawns
    double burgerRate;  //percent chance of burger spawns when collecting cherry
    double cherryRate;  //percent chance of cherry duplication
    boolean liz;    //spawns liz at the start
    boolean tad;    //spawns tad
    byte[] musicData;

    public Level(){
        nameOne = "SNK";
        scoreOne = 10000;
        nameTwo = "TAD";
        scoreTwo = 5000;
        nameThree = "LIZ";
        //scoreThree = 2000;
        scoreThree = -1;
    }

    public Level(File levelFile){
        loadLevelFile(levelFile, false);
    }

    public Level(Level copy) {
        this.title = copy.title;
        this.musicPath = copy.musicPath;
        this.nameOne = copy.nameOne;
        this.scoreOne = copy.scoreOne;
        this.nameTwo = copy.nameTwo;
        this.scoreTwo = copy.scoreTwo;
        this.nameThree = copy.nameThree;
        this.scoreThree = copy.scoreThree;
        this.isDefault = copy.isDefault;
        this.length = copy.length;
        this.bpm = copy.bpm;
        this.upb = copy.upb;
        this.size = copy.size;
        this.bombRate = copy.bombRate;
        this.carRate = copy.carRate;
        this.burgerRate = copy.burgerRate;
        this.cherryRate = copy.cherryRate;
        this.liz = copy.liz;
        this.tad = copy.tad;
        this.musicData = copy.musicData;
    }

    public Level(String title, double length, int bpm, int upb, int size, int bombRate, int carRate, double burgerRate, double cherryRate, boolean liz, boolean tad, File musicFile) {
        this();
        this.title = title;
        isDefault = 0;
        this.length = length;
        this.bpm = bpm;
        this.upb = upb;
        this.size = size;
        this.bombRate = bombRate;
        this.carRate = carRate;
        this.burgerRate = burgerRate;
        this.cherryRate = cherryRate;
        this.liz = liz;
        this.tad = tad;

        loadLevelFile(musicFile, true);
    }

    public InputStream getMIS(){
        //System.out.println("Music path in MIS == null? " + (musicPath == null));
        return(getClass().getClassLoader().getResourceAsStream(musicPath));
    }

    public InputStream getMIS(File file) {
        try{
            return(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println("[???] file not found " + file.getAbsolutePath());
        }
        return(null);
    }

    public AudioInputStream convertByteArrayToAudioStream(byte[] audioData, AudioFormat format) {
        if (audioData == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        long frameLength = audioData.length / format.getFrameSize();
        return new AudioInputStream(bais, format, frameLength);
    }

    public boolean verifyOGG(){
        try (InputStream mis = getMIS()) {
            return startsWithOggS(mis);
        } catch (IOException ex) {
            System.getLogger(Level.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            return(false);
        }
    }

    public boolean verifyOGG(File file) {
        try (InputStream mis = getMIS(file)) {
            return startsWithOggS(mis);
        } catch (IOException ex) {
            System.getLogger(Level.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            return (false);
        }
    }

    private static boolean startsWithOggS(InputStream in) throws IOException {
        if (in == null) {
            return false;
        }
        byte[] magic = in.readNBytes(4);
        boolean ok = magic.length == 4
                && magic[0] == 'O' && magic[1] == 'g' && magic[2] == 'g' && magic[3] == 'S';
        if (!ok) {
            System.out.println("[!!!] OggS header not found in file, are you sure this is an Ogg Vorbis music file?");
        }
        return ok;
    }

    public File createLevelFile(File file){
        InputStream music;

        if(file == null){
            if(musicPath == null){  //saving new high score
                music = new ByteArrayInputStream(musicData);
            }else{  //creating new default levels
                if (!verifyOGG()) {
                    System.out.println("Only Ogg Vorbis files are supported by this audio system. So sorry, I did my best :(");
                    return (null);
                }
                music = getMIS();
            }
        }else{  //levels from editor
            if (!verifyOGG(file)) {
                System.out.println("Only Ogg Vorbis files are supported by this audio system. So sorry, I did my best :(");
                return (null);
            }
            music = getMIS(file);
        }

        File outputDir = new File(levelDirectory);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File output = new File(outputDir, title + ".hlvl");
        if (output.exists()) {
            output.delete();
            System.out.println("Replacing pre-existing file " + title);
        }

        // The stream "stack", from the outside in:
        //   DataOutputStream  turns ints/doubles/strings into bytes
        //   ZipOutputStream   compresses those bytes into a zip entry
        //   FileOutputStream  writes the compressed bytes to disk
        //
        // try-with-resources closes everything automatically, even if an
        // exception is thrown halfway through. Closing the ZipOutputStream is
        // what writes the zip's directory at the end of the file, so a zip
        // that never gets closed is unreadable.
        try (InputStream in = music;
             ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
             DataOutputStream dos = new DataOutputStream(zos)) {

            if (in.available() > 1000000) {
                System.out.print("Levels are easier to share with smaller filesizes. " + title + " is approximately ");
                System.out.printf("%.2g", (in.available() / 1000000.0));
                System.out.println(" megabytes.");
            }

            zos.setLevel(Deflater.BEST_COMPRESSION);
            // A zip can hold many files; we only store one. putNextEntry
            // says "everything written from now on belongs to this file".
            zos.putNextEntry(new ZipEntry(ZIP_ENTRY_NAME));

            dos.writeUTF("hlvl");   //magic numbers
            dos.writeInt(HLVL_VERSION);    //hlvl version

            dos.writeUTF(title);
            dos.writeDouble(length);    //length of the song in milliseconds
            dos.writeInt(isDefault);    //flag for the 5 default levels
            dos.writeUTF(nameOne);
            dos.writeInt(scoreOne);
            dos.writeUTF(nameTwo);
            dos.writeInt(scoreTwo);
            dos.writeUTF(nameThree);
            dos.writeInt(scoreThree);
            dos.writeInt(bpm);  //Song beats per minute
            dos.writeInt(upb);    //updates per beat
            dos.writeInt(size);   //side length of game
            dos.writeInt(bombRate);   //bombs spawned per cherry
            dos.writeInt(carRate);    //updates between car spawns
            dos.writeDouble(burgerRate);  //percent chance of burger spawns when collecting cherry
            dos.writeDouble(cherryRate);  //percent chance of cherry duplication
            dos.writeBoolean(liz);    //spawns liz at the start
            dos.writeBoolean(tad);    //spawns tad

            in.transferTo(dos);     //music goes last, so it runs to the end of the entry

            dos.flush();            // push everything down into the zip stream
            zos.closeEntry();       // finish this entry
        } catch (IOException ex) {
            System.getLogger(Level.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            return (null);
        }

        if (file == null) {
            return (null);
        } else {
            return (output);
        }
    }

    public final void loadLevelFile(File gameFile, boolean isMusic){
        if(isMusic){
            gameFile = createLevelFile(gameFile);
        }
        
        if (gameFile == null || !gameFile.exists()) {
            System.out.println("[!!!] Settings file not found.");
            return;
        }

        // Reading mirrors writing: File -> (unzip) -> DataInputStream
        try (DataInputStream dis = new DataInputStream(openLevelStream(gameFile))) {
            if (!dis.readUTF().equals("hlvl")) {
                System.out.println("[???] Settings file is corrupted or unsupported.");
                return;
            }
            int version = dis.readInt();
            if (version != 0 && version != HLVL_VERSION) {
                System.out.println("[???] Unsupported hlvl version " + version);
                return;
            }

            title = dis.readUTF();
            length = dis.readDouble();
            isDefault = dis.readInt();
            nameOne = dis.readUTF();
            scoreOne = dis.readInt();
            nameTwo = dis.readUTF();
            scoreTwo = dis.readInt();
            nameThree = dis.readUTF();
            scoreThree = dis.readInt();
            bpm = dis.readInt();
            upb = dis.readInt();    //updates per beat
            size = dis.readInt();   //side length of game
            bombRate = dis.readInt();   //bombs spawned per cherry
            carRate = dis.readInt();    //updates between car spawns
            burgerRate = dis.readDouble();  //percent chance of burger spawns when collecting cherry
            cherryRate = dis.readDouble();  //percent chance of cherry duplication
            liz = dis.readBoolean();    //spawns liz at the start
            tad = dis.readBoolean();    //spawns tad

            // Read everything that's left. Do NOT size the array with
            // available(): on a ZipInputStream it returns only 1 or 0.
            musicData = dis.readAllBytes();
            
            System.out.println("Reading file " + title + " file version " + version);

        } catch (IOException e) {
            System.out.println("[!!!] Error reading settings file. Invalid or possibly corrupted.");
        }
    }

    /**
     * Opens a level file for reading, unzipping it if needed.
     * Zip files always start with the bytes 'P' 'K' 3 4, so we peek at the
     * first four bytes to tell new (zipped) files from old (version 0) ones.
     */
    private static InputStream openLevelStream(File file) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            in.mark(4);                     // remember this position...
            byte[] sig = in.readNBytes(4);
            in.reset();                     // ...and oldrewind to it

            boolean isZip = sig.length == 4
                    && sig[0] == 'P' && sig[1] == 'K' && sig[2] == 3 && sig[3] == 4;
            if (!isZip) {
                return in;                  // old uncompressed file
            }

            ZipInputStream zis = new ZipInputStream(in);
            // Move to the first (and only) entry. Reads after this return
            // that entry's uncompressed bytes, and hit end-of-stream at its end.
            if (zis.getNextEntry() == null) {
                throw new IOException("[!!!] Level archive is empty: " + file);
            }
            return zis;
        } catch (IOException e) {
            in.close();
            throw e;
        }
    }

    public static File[] getLevelFiles(){
        File folder = new File(levelDirectory);
        File[] folderFiles = folder.listFiles();
        
        if (folderFiles == null) {
            try {
                Level[] levels = new Level[]{new LevelOne(), new LevelTwo(), new LevelThree(), new LevelFour(), new LevelFive()};
                for (Level level : levels) {
                    level.createLevelFile(null);
                }
                String content = "This is the levels file for HSnake, storing the campaign and custom levels you've made. It can be safely deleted if need be, and is automatically generated at startup.";
                File output = new File(levelDirectory, "readme.txt");
                Files.writeString(Paths.get(output.toURI()), content);
                
                return(getLevelFiles());
            } catch (IOException ex) {
                System.getLogger(Level.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        
        File[] prunedFiles = new File[folderFiles.length];
        
        int pointer = 0;
        for(File e : folderFiles){
            if(e.getName().endsWith(".hlvl")){
                prunedFiles[pointer] = e;
                pointer++;
            }
        }
        
        File[] levelFiles = new File[pointer];
        for(int x = 0; x < pointer; x++){
            levelFiles[x] = prunedFiles[x];
        }
        
        
        
        return(levelFiles);
    }
    
    

    public double getLength() {
        return length;
    }

    public String getTitle() {
        return title;
    }

    public int getDefault() {
        return isDefault;
    }

    public byte[] getMusicData() {
        return musicData;
    }

    public int getDelay() {
        return ((int) (1 / (bpm * upb * (1.0 / 60000.0))));
    }

    public int getBpm() {
        return bpm;
    }

    public int getUpb() {
        return upb;
    }

    public int getSize() {
        return size;
    }

    public int getBombRate() {
        return bombRate;
    }

    public int getCarRate() {
        return carRate;
    }

    public double getBurgerRate() {
        return burgerRate;
    }

    public double getCherryRate() {
        return cherryRate;
    }

    public boolean isLiz() {
        return liz;
    }

    public boolean isTad() {
        return tad;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public String getNameOne() {
        return nameOne;
    }

    public void setNameOne(String nameOne) {
        this.nameOne = nameOne;
    }

    public int getScoreOne() {
        return scoreOne;
    }

    public void setScoreOne(int scoreOne) {
        this.scoreOne = scoreOne;
    }

    public String getNameTwo() {
        return nameTwo;
    }

    public void setNameTwo(String nameTwo) {
        this.nameTwo = nameTwo;
    }

    public int getScoreTwo() {
        return scoreTwo;
    }

    public void setScoreTwo(int scoreTwo) {
        this.scoreTwo = scoreTwo;
    }

    public String getNameThree() {
        return nameThree;
    }

    public void setNameThree(String nameThree) {
        this.nameThree = nameThree;
    }

    public int getScoreThree() {
        return scoreThree;
    }

    public void setScoreThree(int scoreThree) {
        this.scoreThree = scoreThree;
    }

    public String[] getNames() {
        return (new String[]{nameOne, nameTwo, nameThree});
    }

    public int[] getScores() {
        return (new int[]{scoreOne, scoreTwo, scoreThree});
    }

}
