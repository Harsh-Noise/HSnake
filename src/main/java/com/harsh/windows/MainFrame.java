/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.harsh.windows;

import com.harsh.logic.sound.Sound;
import com.harsh.logic.levels.*;
import com.harsh.entities.*;
import com.harsh.logic.*;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


/**
 *
 * @author Harsh Noise
 */
public class MainFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    private final CardLayout cards;
    
    Clip click;
    Clip death;
    Clip celebrate;
    Clip bonus;
    
    public SnakeGame game = new SnakeGame();
    public volatile int direction;
    private ScheduledExecutorService scheduler;
    
    static Sound introMusic = new Sound("audio/music/intro.ogg");   //Switch to SourceDataLine at some point, or TinySound
    static Sound levelMusic;
    
    Level[] allLevels = {};
    Level[] currentRun = {};
    int totalScore = 0;
    
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        importLevels(false, false);
        initComponents();
        
        
        try {
            setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/entity/SnakeHead.png")));
        } catch (IOException ex) {
            System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        click = initSound("audio/sound/click.wav");
        death = initSound("audio/sound/death.wav");
        celebrate = initSound("audio/sound/celebrate.wav");
        bonus = initSound("audio/sound/bonus.wav");
        
        playSound(click);
        introMusic.play();
        cards = (CardLayout) RootPanel.getLayout();
        
        
        
        JRootPane root = getRootPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "moveUp");
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Movement detected");
                if(game.isValidDirection(0)){
                    direction = (0);
                } 
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "moveRight");
        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Movement detected");
                if(game.isValidDirection(1)){
                    direction = (1);
                } 
                
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "moveDown");
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Movement detected");
                if(game.isValidDirection(2)){
                    direction = (2);
                } 
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "moveLeft");
        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Movement detected");
                if(game.isValidDirection(3)){
                    direction = (3);
                } 
            }
        });
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                // do stuff
                //System.out.println("Detected");
                gamePanel1.getGraphicsPanel().setScale();
                gallaryPanel1.getGraphicsPanel().setScale();
            }
        });
    }
    
    private Clip initSound(String resource) {
        URL url = getClass().getClassLoader().getResource(resource);
        if (url == null) {
            System.err.println("Resource not found: " + resource);
            return null;
        }
        
        try (AudioInputStream in = AudioSystem.getAudioInputStream(url)) {
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("[!!!] Error initializing sound clips\n" + e.getMessage());
            return null;
        }
    }
    
    private void gameManager(){
        introMusic.stop();
        
        for(Level level : currentRun){
            gamePanel1.getGraphicsPanel().setSquareSize(level.getSize());
            gamePanel1.getGraphicsPanel().setScale();
            game.setCurrentLevel(level);
            game.initialize();
            byte[] musicData = game.getCurrentLevel().getMusicData();
            levelMusic = new Sound(musicData);
            levelMusic.play();
            
            long delay = game.getCurrentLevel().getDelay();
            
            //Single reusable scheduler
            scheduler = Executors.newSingleThreadScheduledExecutor();
            ScheduledFuture thisFuture = scheduler.scheduleAtFixedRate(() -> {
                //System.out.println("Checking gameover status" + game.isOver());
                if (game.isOver()) {
                    //System.out.println("Gameover detected!");
                    scheduler.shutdown();
                    onLevelEnd();
                    return;
                }
                // Game logic on scheduler thread (safe — no Swing calls here)
                game.setDirection(direction);
                game.fullTurn();

                // Snapshot values before hopping to EDT
                int pts = game.getPoints();
                int prog = game.getProgress();
                //System.out.println(prog);
                Sprite[] sprites = game.getSprites();
                
                // All rendering/UI updates on EDT
                SwingUtilities.invokeLater(() -> {
                    gamePanel1.setPoints(pts);
                    gamePanel1.setBar(prog);
                    gamePanel1.setSprites(sprites);
                    gamePanel1.getGraphicsPanel().repaint();
                });
                //System.out.println("Looping gameplay");
                
            }, 0, delay, TimeUnit.MILLISECONDS);
            
            while (!thisFuture.isDone()) {
                try {
                    //System.out.println("Future not done");
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
            //System.out.println("Future done!!!!!!!!!!!!!!!!");
            //System.out.println("Breaking out of gameplay loop");
            
            //if there's a new high score
            //game.setPoints(99999999);
            if (game.getPoints() > level.getScoreThree()) {
                HighScorePopup popup = new HighScorePopup();
                try{
                    if (game.getPoints() > level.getScoreTwo()) {
                        if (game.getPoints() > level.getScoreOne()) {
                            
                            //gold
                            level.setScoreThree(level.getScoreTwo());
                            level.setNameThree(level.getNameTwo());
                            
                            level.setScoreTwo(level.getScoreOne());
                            level.setNameTwo(level.getNameOne());
                            
                            level.setScoreOne(game.getPoints());
                            level.setNameOne("NEW");
                            popup = new HighScorePopup(this, true, level, 1);
                            popup.setVisible(true);
                        }else{
                            
                            //silver
                            level.setScoreThree(level.getScoreTwo());
                            level.setNameThree(level.getNameTwo());
                            
                            level.setScoreTwo(game.getPoints());
                            level.setNameTwo("NEW");
                            popup = new HighScorePopup(this, true, level, 2);
                            popup.setVisible(true);
                        }
                    }else{
                        
                        //bronze
                        level.setScoreThree(game.getPoints());
                        level.setNameThree("NEW");
                        popup = new HighScorePopup(this, true, level, 3);
                        popup.setVisible(true);
                    }
                    
                }catch(ParseException e){
                    System.out.println("[!!!] Parse Exception caught while trying to display leaderboard");
                }
                
                while(popup.isVisible()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                }
                
                levelBrowserPanel1.addElement(level);
            }
            
        }
        if(isBonusEligable() && !isAlreadyBonus()){
            try {
                playSound(bonus); 
                importLevels(true, true);
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        levelBrowserPanel1.initLevelList();
        goToMenu();
        introMusic.play();
    }
    
    private void onLevelEnd() {
        try {
            if(game.isWin()){
                levelMusic.stop();
                playSound(celebrate);
                Thread.sleep(2000);
                totalScore += game.getPoints();
                game.endGame();
                game.setIsOver(false);
                game.setIsWin(false);
                Thread.sleep(100);
            }else{
                levelMusic.stop();
                playSound(death);
                Thread.sleep(2000);
                totalScore += game.getPoints();
                game.endGame();
                game.setIsOver(false);
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    private void importLevels(boolean lightImport, boolean bonus){
        Level bonuslvl;
        if (bonus) {
            bonuslvl = new LevelBonus();
            bonuslvl.createLevelFile(null);
        }
        
        File[] levelFiles = Level.getLevelFiles();
        int zeroCounter = 0;
        
        allLevels = new Level[levelFiles.length];
        
        for(int x = 0; x < levelFiles.length ; x++){
            allLevels[x] = new Level(levelFiles[x]);
            if(allLevels[x].getDefault() < 2){ //counts number of non default levels
                zeroCounter++;
            }
            //System.out.println("Audiodata length MainFrame.java/importLevels " + allLevels[x].getMusicData().length);
        }
        
        Level[] defaultLevels = new Level[levelFiles.length - zeroCounter];
        
        //sort levels by their defaultedness
        for (int x = 0; x < levelFiles.length; x++) {
            for (int y = 0; y < levelFiles.length-1; y++){
                if(allLevels[y].getDefault() > allLevels[y+1].getDefault()){
                    Level tempLevel = allLevels[y];
                    allLevels[y] = allLevels[y+1];
                    allLevels[y+1] = tempLevel;
                }
            }
        }
        
        //set default to current run
        for (int x = zeroCounter; x < levelFiles.length; x++) {
            defaultLevels[x - zeroCounter] = allLevels[x];
        }
        
        if(!lightImport){
            currentRun = defaultLevels;
        }
        //System.out.println("Levels length " + allLevels.length);
    }
    
    private boolean isUnranked(Level level){
        return(level.getNameOne().equals("SNK") &&
                level.getNameTwo().equals("TAD") &&
                level.getNameThree().equals("LIZ")
                );
    }
    
    private boolean isBonusEligable(){
        for(Level e : allLevels){
            if(e.getDefault() > 0){
                if(isUnranked(e)){
                    return(false);
                }
            }
        }
        return(true);
    }
    
    private boolean isAlreadyBonus(){
        for (Level e : allLevels) {
            if (e.getDefault() == 1) {
                return (true);
            }
        }
        return(false);
    }
    
    public static void playSound(Clip clip) {
        if (clip == null) {
            return;
        }
        clip.stop();              // lets a rapid re-click restart the sound
        clip.setFramePosition(0);
        clip.start();
    }
    
    public void playClick() {
        playSound(click);
    }
    
    public void playDeath() {
        playSound(death);
    }
    
    public void goToMenu() {
        menuPanel1.setScoreText("Last game score: " + totalScore);
        cards.show(RootPanel, "card1");
        //menuPanel1.requestFocusInWindow();
        playSound(click);
    }

    public void goToGallary() {
        cards.show(RootPanel, "card2");
        //gallaryPanel1.requestFocusInWindow();
        playSound(click);
        //gallaryPanel1.isActive = true;
        gallaryPanel1.gallaryThreader();
    }
    
    public void goToLevels() {
        cards.show(RootPanel, "card4");
        //gallaryPanel1.requestFocusInWindow();
        playSound(click);
        //gallaryPanel1.isActive = true;
    }

    public void goToGame() {
        totalScore = 0;
        cards.show(RootPanel, "card3");
        //gamePanel1.requestFocusInWindow();
        playSound(click);
        // Push the blocking game loop OFF the EDT
        new Thread(this::gameManager).start();
    }
    
    public void goToEditor() {
        cards.show(RootPanel, "card6");
        //gallaryPanel1.requestFocusInWindow();
        playSound(click);
        //gallaryPanel1.isActive = true;
    }

    public void showCredits() {
        playSound(click);
        CreditsDialogue.setVisible(true);
    }

    public int getTotalScore() {
        return totalScore;
    }

    public Level[] getLevels() {
        return allLevels;
    }
    
    public Level[] getRun() {
        return currentRun;
    }

    public void setCurrentRun(Level[] currentRun) {
        this.currentRun = currentRun;
    }
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CreditsDialogue = new javax.swing.JDialog();
        CreditsClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        RootPanel = new javax.swing.JPanel();
        menuPanel1 = new com.harsh.windows.MenuPanel(this);
        gallaryPanel1 = new com.harsh.windows.GallaryPanel(this);
        gamePanel1 = new com.harsh.windows.GamePanel(this);
        levelBrowserPanel1 = new com.harsh.windows.LevelBrowserPanel(this);
        levelEditor1 = new com.harsh.windows.LevelEditor(this, levelBrowserPanel1);

        CreditsDialogue.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        CreditsDialogue.setMinimumSize(new java.awt.Dimension(350, 200));
        CreditsDialogue.setModal(true);

        CreditsClose.setText("Close");
        CreditsClose.addActionListener(this::CreditsCloseActionPerformed);
        CreditsDialogue.getContentPane().add(CreditsClose, java.awt.BorderLayout.PAGE_END);

        jTextPane1.setEditable(false);
        jTextPane1.setText("- Code and artwork by https://github.com/Harsh-Noise  \n- Music fell out the back of a truck lol  \n- Inspiration came from God");
        jScrollPane1.setViewportView(jTextPane1);

        CreditsDialogue.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(700, 500));

        RootPanel.setLayout(new java.awt.CardLayout());
        RootPanel.add(menuPanel1, "card1");
        RootPanel.add(gallaryPanel1, "card2");
        RootPanel.add(gamePanel1, "card3");
        RootPanel.add(levelBrowserPanel1, "card4");
        RootPanel.add(levelEditor1, "card6");

        getContentPane().add(RootPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CreditsCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreditsCloseActionPerformed
        // TODO add your handling code here:
        CreditsDialogue.setVisible(false);
        playSound(click);
    }//GEN-LAST:event_CreditsCloseActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CreditsClose;
    private javax.swing.JDialog CreditsDialogue;
    private javax.swing.JPanel RootPanel;
    private com.harsh.windows.GallaryPanel gallaryPanel1;
    private com.harsh.windows.GamePanel gamePanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private com.harsh.windows.LevelBrowserPanel levelBrowserPanel1;
    private com.harsh.windows.LevelEditor levelEditor1;
    private com.harsh.windows.MenuPanel menuPanel1;
    // End of variables declaration//GEN-END:variables
}
