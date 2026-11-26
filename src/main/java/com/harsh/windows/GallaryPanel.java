/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.harsh.windows;
import com.harsh.entities.*;
import com.harsh.logic.SnakeGame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 *
 * @author Harsh Noise
 */
public class GallaryPanel extends javax.swing.JPanel {  //make it so gallary auto-fills sprites with blanks
    private final MainFrame rootFrame;
    
    public BufferedImage snakehead, snakebody, snaketail, snakeL, snakeR, cherry, patty, bomb, lizhead, tadhead, tadtail, carL, carR, warning, blank, error;

    
    GallerySprite snake, liz, gerald, tad;
    
    GallerySprite[] gallery;
    String[] descriptions = new String[]{"Our titular hero! Snake's favorite ice cream is cherry chip, and his lucky number is 3.", "Snake's bitch of an ex-wife. Liz left the kids but took the dog. She's now living on the sandy beaches of LA.", "Snake's roommate Gerald is a simple man. He recently installed ultra-bright LED headlights to see better at night.", "Snake and Tad were highschool lovers who separated due to personality conflict. She still checks Snake's socials."};
    String[] names = new String[]{"Snake", "Liz", "Gerald", "Tad"};
    int gallaryPos = 0;
    private ScheduledExecutorService scheduler;
    //int timer = 0;
    
    /**
     * Creates new form SettingsPanel
     * @param parent
     */
    
    public GallaryPanel(MainFrame parent) {
        rootFrame = parent;
        initComponents();
        initImages();
        graphicsPanel1.setSquareSize(5);
        
        snake = new GallerySprite(new BufferedImage[]{snakehead, snakebody, snaketail, blank, blank});
        liz = new GallerySprite(new BufferedImage[]{lizhead, snakebody, snaketail, blank, blank});
        gerald = new GallerySprite(new BufferedImage[]{warning, warning, carL, carR, blank});
        tad = new GallerySprite(new BufferedImage[]{tadhead, tadtail, blank, blank, blank});
        
        gallery = new GallerySprite[]{snake, liz, gerald, tad};

        Description.setText(descriptions[gallaryPos]);
        NameLabel.setText(names[gallaryPos]);
    }
    
    public GallaryPanel() {
        rootFrame = null;
        initComponents();
    }
    
    public void iterate() {
        for (int x = 0; x < 4; x++) {
            gallery[x].iterate();
        }
    }
    
    public GraphicsPanel getGraphicsPanel() {
        return graphicsPanel1;
    }
    
    private javax.swing.Timer refreshTimer;
    public void gallaryThreader() {
        refreshTimer = new javax.swing.Timer(16, e -> graphicsPanel1.repaint());
        refreshTimer.start();
        new Thread(this::gallaryManager).start();
    } 
    
    private void gallaryManager(){
        long delay = 465;
        //Single reusable scheduler
        scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture thisFuture = scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                iterate();
                graphicsPanel1.setSprites(gallery[gallaryPos].getSprites());
                graphicsPanel1.repaint();
            });
        }, 0, delay, TimeUnit.MILLISECONDS);

        while (!thisFuture.isDone()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }
    
    private void initImages() {
        try {
            snakehead = ImageIO.read(getClass().getResourceAsStream("/images/entity/SnakeHead.png"));
            snakebody = ImageIO.read(getClass().getResourceAsStream("/images/entity/SnakeBelly.png"));
            snaketail = ImageIO.read(getClass().getResourceAsStream("/images/entity/SnakeTail.png"));
            snakeL = ImageIO.read(getClass().getResourceAsStream("/images/entity/SnakeTurnLeft.png"));
            snakeR = ImageIO.read(getClass().getResourceAsStream("/images/entity/SnakeTurnRight.png"));
            cherry = ImageIO.read(getClass().getResourceAsStream("/images/entity/Cherry.png"));
            patty = ImageIO.read(getClass().getResourceAsStream("/images/entity/Patty.png"));
            bomb = ImageIO.read(getClass().getResourceAsStream("/images/entity/Bomb2.png"));
            lizhead = ImageIO.read(getClass().getResourceAsStream("/images/entity/LizHead.png"));
            tadhead = ImageIO.read(getClass().getResourceAsStream("/images/entity/TadHead.png"));
            tadtail = ImageIO.read(getClass().getResourceAsStream("/images/entity/TadTail.png"));
            carL = ImageIO.read(getClass().getResourceAsStream("/images/entity/TruckL.png"));
            carR = ImageIO.read(getClass().getResourceAsStream("/images/entity/TruckR.png"));
            warning = ImageIO.read(getClass().getResourceAsStream("/images/env/Warning.png"));
            blank = ImageIO.read(getClass().getResourceAsStream("/images/env/Blank.png"));
            error = ImageIO.read(getClass().getResourceAsStream("/images/env/Default.png"));
        } catch (IOException ex) {
            System.getLogger(SnakeGame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        NextButton = new javax.swing.JButton();
        PrevButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        SettingsLabel = new javax.swing.JLabel();
        NameLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Description = new javax.swing.JTextArea();
        graphicsPanel1 = new com.harsh.windows.GraphicsPanel();

        setLayout(new java.awt.BorderLayout());

        jButton1.setText("Back to menu");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        add(jButton1, java.awt.BorderLayout.PAGE_END);

        NextButton.setText("===>");
        NextButton.addActionListener(this::NextButtonActionPerformed);
        add(NextButton, java.awt.BorderLayout.LINE_END);

        PrevButton.setText("<===");
        PrevButton.addActionListener(this::PrevButtonActionPerformed);
        add(PrevButton, java.awt.BorderLayout.LINE_START);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        SettingsLabel.setFont(new java.awt.Font("sansserif", 0, 36)); // NOI18N
        SettingsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SettingsLabel.setText("Gallery");
        SettingsLabel.setAlignmentX(0.5F);
        jPanel1.add(SettingsLabel);

        NameLabel.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
        NameLabel.setText("Entity Name");
        NameLabel.setAlignmentX(0.5F);
        jPanel1.add(NameLabel);

        Description.setEditable(false);
        Description.setColumns(20);
        Description.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        Description.setLineWrap(true);
        Description.setRows(5);
        Description.setText("Entity description\n");
        Description.setMargin(new java.awt.Insets(2, 100, 2, 100));
        jScrollPane1.setViewportView(Description);

        jPanel1.add(jScrollPane1);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout graphicsPanel1Layout = new javax.swing.GroupLayout(graphicsPanel1);
        graphicsPanel1.setLayout(graphicsPanel1Layout);
        graphicsPanel1Layout.setHorizontalGroup(
            graphicsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
        );
        graphicsPanel1Layout.setVerticalGroup(
            graphicsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 311, Short.MAX_VALUE)
        );

        add(graphicsPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        refreshTimer.stop();
        scheduler.shutdown();
        rootFrame.goToMenu();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void NextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextButtonActionPerformed
        // TODO add your handling code here:
        gallaryPos = (gallaryPos+1)%gallery.length;
        Description.setText(descriptions[gallaryPos]);
        NameLabel.setText(names[gallaryPos]);
    }//GEN-LAST:event_NextButtonActionPerformed

    private void PrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrevButtonActionPerformed
        // TODO add your handling code here:
        gallaryPos = ((gallaryPos+gallery.length)-1)%gallery.length;
        Description.setText(descriptions[gallaryPos]);
        NameLabel.setText(names[gallaryPos]);
    }//GEN-LAST:event_PrevButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Description;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JButton NextButton;
    private javax.swing.JButton PrevButton;
    private javax.swing.JLabel SettingsLabel;
    private com.harsh.windows.GraphicsPanel graphicsPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
