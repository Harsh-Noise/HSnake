/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.harsh.windows;
 
import com.harsh.logic.levels.Level;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
 
/**
 *
 * @author Harsh Noise
 */
public class LevelBrowserPanel extends javax.swing.JPanel {
    private MainFrame rootFrame;
 
    DefaultListModel<Level> menuModel = new DefaultListModel<>();
    DefaultListModel<Level> runModel = new DefaultListModel<>();
 
    /**
     * Creates new form LevelBrowserPanel
     * @param parent
     */
    public LevelBrowserPanel(MainFrame parent) {
        rootFrame = parent;
        initComponents();
        initLevelList();
    }
 
    public LevelBrowserPanel() {
        initComponents();
        rootFrame = null;
    }
 
    public Level[] getLevels() {
        return rootFrame.getLevels();
    }
 
    public final void initLevelList(){
        Level[] gameLevels = rootFrame.getLevels();
        Level[] currentRun = rootFrame.getRun();
        menuModel.removeAllElements();
        runModel.removeAllElements();
 
        //System.out.println(gameLevels.length + " levels found");
        for (Level gameLevel : gameLevels) {
            menuModel.addElement(gameLevel);
            //System.out.println("Adding " + gameLevels[x].getTitle());
        }
        LevelMenu = createLevelList(menuModel);
        jScrollPane1.setViewportView(LevelMenu);
 
 
        for (Level currentRun1 : currentRun) {
            runModel.addElement(currentRun1);
            //System.out.println("Adding " + currentRun[x].getTitle());
        }
        LevelOrder = createLevelList(runModel);
        jScrollPane3.setViewportView(LevelOrder);
 
        new LevelDragController(LevelMenu, LevelOrder);
    }
 
    public void addElement(Level newLevel){
        for(int x = 0; x < menuModel.size(); x++){
            if(menuModel.get(x).getTitle().equals(newLevel.getTitle())){
                menuModel.remove(x);
                System.out.println("Replacing old level from menu");
                break;
            }
        }
        for (int x = 0; x < runModel.size(); x++) { //If it is in the run, replace the one in the run
            if (runModel.get(x).getTitle().equals(newLevel.getTitle())) {
                runModel.remove(x);
                System.out.println("Removing old level from run");
                runModel.addElement(newLevel);
                break;
            }
        }
        menuModel.add(0, newLevel); //add to menu regardless of one being replaced
    }
 
    private String loadLevelData(Level level){
        return(
                "Title\t- " + level.getTitle() + "\n" +
                "Length\t- " + ((int)(level.getLength()/1000)/60) + ":" + String.format("%02d", ((int)(level.getLength()/1000.0)%60)) + "\n" +
                "Top scores\t- " + level.getNameOne() + " | " + level.getScoreOne() + "\n" +
                "\t- " + level.getNameTwo() + " | " + level.getScoreTwo() + "\n" +
                "\t- " + level.getNameThree() + " | " + level.getScoreThree() + "\n"
        );
    }
 
    private JList<Level> createLevelList(DefaultListModel<Level> model) {
        JList<Level> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new LevelButtonRenderer());
        //list.setFixedCellHeight(44);
        //list.setVisibleRowCount(8);
        // Dragging, and clicking to show a level's info, are handled by
        // LevelDragController (installed at the end of initLevelList).
        return list;
    }
 
    // ---- Drag controller -------------------------------------------------
    /**
     * Drag-and-drop between the two lists, done with plain Swing mouse
     * listeners and an overlay rather than the AWT drag-and-drop engine (which
     * draws its own cursor and shows a "forbidden" icon on many Linux setups).
     *
     * - Available Levels (menu): dragging from it adds a copy to the run; the
     *   menu itself never changes, so a level can be added more than once.
     * - Current Run: entries can be reordered by dragging within the list, and
     *   removed by dragging them back onto the menu.
     * - A press and release without moving shows the level's info in LevelData.
     */
    private class LevelDragController extends MouseAdapter {
        private static final int DRAG_THRESHOLD = 5;
 
        private final JList<Level> menuList;
        private final JList<Level> orderList;
        private final DragLayer layer = new DragLayer();
 
        private JList<Level> sourceList;
        private int sourceIndex = -1;
        private Point pressPoint;
        private boolean dragging;
        private JList<Level> targetList;
        private int dropIndex;
        private Point lastPoint; // last pointer position, in sourceList coordinates
        private JLayeredPane layerHost;
 
        // While the pointer rests near the top/bottom edge of a list, scroll it.
        private final Timer edgeScroll = new Timer(30, e -> edgeScrollTick());
 
        LevelDragController(JList<Level> menuList, JList<Level> orderList) {
            this.menuList = menuList;
            this.orderList = orderList;
            install(menuList);
            install(orderList);
        }
 
        private void install(JList<Level> list) {
            // JList autoscrolls by default. Once the pointer leaves the list the
            // press started in, Swing stops delivering real drag events to it and
            // replays the last one on a 100 ms timer -- so the overlay would update
            // only ~10 times a second over the other list. Turn that off;
            // edgeScroll replaces it.
            list.setAutoscrolls(false);
            list.addMouseListener(this);
            list.addMouseMotionListener(this);
        }
 
        @SuppressWarnings("unchecked")
        @Override
        public void mousePressed(MouseEvent e) {
            reset();
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }
            JList<Level> list = (JList<Level>) e.getSource();
            int index = indexAt(list, e.getPoint());
            if (index >= 0) {
                sourceList = list;
                sourceIndex = index;
                pressPoint = e.getPoint();
            }
        }
 
        @Override
        public void mouseDragged(MouseEvent e) {
            if (sourceList == null) {
                return;
            }
            if (!dragging) {
                if (e.getPoint().distance(pressPoint) < DRAG_THRESHOLD || !showLayer()) {
                    return;
                }
                dragging = true;
                setDragCursor(true);
                edgeScroll.start();
            }
            // JList normally moves the selection to whatever row is under the
            // pointer while dragging; keep it on the row being dragged instead.
            if (sourceList.getSelectedIndex() != sourceIndex) {
                sourceList.setSelectedIndex(sourceIndex);
            }
            updateDrag(e.getPoint());
        }
 
        @Override
        public void mouseReleased(MouseEvent e) {
            if (sourceList == null) {
                return;
            }
            JList<Level> src = sourceList;
            int idx = sourceIndex;
 
            if (dragging) {
                updateDrag(e.getPoint());
                JList<Level> tgt = targetList;
                int drop = dropIndex;
                reset();
                applyDrop(src, idx, tgt, drop);
            } else if (indexAt(src, e.getPoint()) == idx) {
                Level level = src.getModel().getElementAt(idx);
                reset();
                LevelData.setText(loadLevelData(level));
            } else {
                reset();
            }
        }
 
        // -- overlay -------------------------------------------------------
 
        /**
         * Puts the overlay on the window's layered pane, above everything else.
         * (The layered pane is used rather than the glass pane so MainFrame's
         * glass pane is left alone.) Returns false if the panel isn't in a window.
         */
        private boolean showLayer() {
            JRootPane root = SwingUtilities.getRootPane(LevelBrowserPanel.this);
            if (root == null) {
                return false;
            }
            layerHost = root.getLayeredPane();
            layer.setBounds(0, 0, layerHost.getWidth(), layerHost.getHeight());
            layerHost.add(layer, JLayeredPane.DRAG_LAYER);
            return true;
        }
 
        private void hideLayer() {
            if (layerHost != null) {
                layer.clear();
                layerHost.remove(layer);
                layerHost.repaint(layer.getBounds());
                layerHost = null;
            }
        }
 
        // -- drag state ----------------------------------------------------
 
        /** @param p pointer position in the coordinates of the list the drag started in */
        private void updateDrag(Point p) {
            lastPoint = p;
            targetList = hit(orderList, p);
            if (targetList == null) {
                targetList = hit(menuList, p);
            }
 
            Rectangle line = null;
            Rectangle removeZone = null;
            if (targetList == orderList) {
                dropIndex = insertionIndex(orderList, SwingUtilities.convertPoint(sourceList, p, orderList));
                line = insertionLine(orderList, dropIndex);
            } else if (targetList == menuList && sourceList == orderList) {
                removeZone = SwingUtilities.convertRectangle(menuList, menuList.getVisibleRect(), layer);
            }
 
            Rectangle cell = sourceList.getCellBounds(sourceIndex, sourceIndex);
            layer.setState(sourceList.getModel().getElementAt(sourceIndex).getTitle(),
                    new Dimension(Math.min(cell.width, 220), cell.height),
                    SwingUtilities.convertPoint(sourceList, p, layer), line, removeZone);
        }
 
        /** Scrolls the list under the pointer when it is within a margin of its top/bottom edge. */
        private void edgeScrollTick() {
            if (!dragging || targetList == null || lastPoint == null) {
                return;
            }
            final int margin = 28;
            Rectangle view = targetList.getVisibleRect();
            Point q = SwingUtilities.convertPoint(sourceList, lastPoint, targetList);
            int dy = 0;
            if (q.y < view.y + margin) {
                dy = -Math.min(14, Math.max(2, (view.y + margin - q.y) / 2));
            } else if (q.y > view.y + view.height - margin) {
                dy = Math.min(14, Math.max(2, (q.y - (view.y + view.height - margin)) / 2));
            }
            if (dy != 0) {
                targetList.scrollRectToVisible(new Rectangle(view.x, view.y + dy, view.width, view.height));
                if (targetList.getVisibleRect().y != view.y) {
                    // The list moved under a stationary pointer, so the drop spot changed.
                    updateDrag(lastPoint);
                }
            }
        }
 
        private void reset() {
            edgeScroll.stop();
            if (dragging) {
                setDragCursor(false);
            }
            hideLayer();
            dragging = false;
            sourceList = null;
            sourceIndex = -1;
            pressPoint = null;
            targetList = null;
        }
 
        /**
         * Hand cursor while dragging. The overlay covers the whole window, so its
         * cursor is the one shown; the lists are set too for good measure.
         * null = back to the inherited/default cursor.
         */
        private void setDragCursor(boolean on) {
            Cursor c = on ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : null;
            layer.setCursor(c);
            menuList.setCursor(c);
            orderList.setCursor(c);
        }
 
        // -- drop ------------------------------------------------------------
 
        private void applyDrop(JList<Level> src, int idx, JList<Level> tgt, int drop) {
            if (tgt == orderList) {
                DefaultListModel<Level> orderModel = (DefaultListModel<Level>) orderList.getModel();
                if (src == orderList) {
                    // Reorder: take it out, then insert at the (possibly shifted) index.
                    Level moved = orderModel.remove(idx);
                    if (idx < drop) {
                        drop--;
                    }
                    orderModel.add(drop, moved);
                } else {
                    // From the menu: insert a fresh copy; the menu is untouched.
                    Level original = src.getModel().getElementAt(idx);
                    orderModel.add(drop, new Level(original));
                }
                orderList.setSelectedIndex(drop);
            } else if (tgt == menuList && src == orderList) {
                // Dropped back on the menu: remove from the run only.
                ((DefaultListModel<Level>) orderList.getModel()).remove(idx);
            }
            // Dropped on the menu from the menu, or outside both lists: no change.
        }
 
        // -- geometry helpers --------------------------------------------------
 
        /** Index of the cell actually under the point, or -1. */
        private int indexAt(JList<Level> list, Point p) {
            int i = list.locationToIndex(p);
            if (i < 0) {
                return -1;
            }
            Rectangle r = list.getCellBounds(i, i);
            return (r != null && r.contains(p)) ? i : -1;
        }
 
        /** The list if the point (given in sourceList coordinates) is over its visible area, else null. */
        private JList<Level> hit(JList<Level> list, Point p) {
            Point q = SwingUtilities.convertPoint(sourceList, p, list);
            return list.getVisibleRect().contains(q) ? list : null;
        }
 
        /** Where a drop at this point would insert: before or after the nearest cell. */
        private int insertionIndex(JList<Level> list, Point p) {
            if (list.getModel().getSize() == 0) {
                return 0;
            }
            int i = list.locationToIndex(p);
            Rectangle r = list.getCellBounds(i, i);
            return p.y > r.y + r.height / 2 ? i + 1 : i;
        }
 
        /** Thin horizontal bar (in overlay coordinates) marking the insertion point. */
        private Rectangle insertionLine(JList<Level> list, int index) {
            int size = list.getModel().getSize();
            int y;
            if (size == 0) {
                y = 0;
            } else if (index < size) {
                y = list.getCellBounds(index, index).y;
            } else {
                Rectangle last = list.getCellBounds(size - 1, size - 1);
                y = last.y + last.height;
            }
            Rectangle visible = list.getVisibleRect();
            return SwingUtilities.convertRectangle(list,
                    new Rectangle(visible.x, y - 1, visible.width, 3), layer);
        }
    }
 
    // ---- Drag overlay: ghost button, insertion line, "remove" zone ------
    /** Transparent layer painted above the whole window while a drag is in progress. */
    static class DragLayer extends JComponent {
        private String ghostText;
        private Dimension ghostSize = new Dimension();
        private Point mouse;
        private Rectangle insertionLine;
        private Rectangle removeZone;
        private Font font;
 
        // Areas painted last time, so a repaint covers only what actually changed.
        // (A bare repaint() would redraw the whole window, lists included, on
        // every mouse movement -- this layer spans the entire window.)
        private Rectangle paintedGhost;
        private Rectangle paintedLine;
        private Rectangle paintedZone;
 
        void setState(String ghostText, Dimension ghostSize, Point mouse,
                      Rectangle insertionLine, Rectangle removeZone) {
            this.ghostText = ghostText;
            this.ghostSize = ghostSize;
            this.mouse = mouse;
            this.insertionLine = insertionLine;
            this.removeZone = removeZone;
 
            Rectangle ghost = new Rectangle(mouse.x - ghostSize.width / 2 - 2, mouse.y - ghostSize.height / 2 - 2,
                    ghostSize.width + 4, ghostSize.height + 4);
            Rectangle line = insertionLine == null ? null : new Rectangle(insertionLine.x - 1,
                    insertionLine.y - 1, insertionLine.width + 2, insertionLine.height + 2);
 
            repaintChange(paintedGhost, ghost);
            repaintChange(paintedLine, line);
            repaintChange(paintedZone, removeZone);
            paintedGhost = ghost;
            paintedLine = line;
            paintedZone = removeZone;
        }
 
        private void repaintChange(Rectangle before, Rectangle after) {
            if (before != null) {
                if (before.equals(after)) {
                    return;
                }
                repaint(before.x, before.y, before.width, before.height);
            }
            if (after != null) {
                repaint(after.x, after.y, after.width, after.height);
            }
        }
 
        void clear() {
            mouse = null;
            insertionLine = null;
            removeZone = null;
            paintedGhost = null;
            paintedLine = null;
            paintedZone = null;
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            if (mouse == null) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (font == null) {
                    font = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 13f);
                }
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
 
                if (removeZone != null) {
                    g2.setColor(new Color(220, 60, 60, 40));
                    g2.fillRect(removeZone.x, removeZone.y, removeZone.width, removeZone.height);
                    g2.setColor(new Color(200, 50, 50, 170));
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                            10f, new float[]{6f, 4f}, 0f));
                    g2.drawRect(removeZone.x + 1, removeZone.y + 1, removeZone.width - 3, removeZone.height - 3);
                    String hint = "Release to remove";
                    int hw = fm.stringWidth(hint) + 20;
                    int hh = fm.getHeight() + 8;
                    int hx = removeZone.x + (removeZone.width - hw) / 2;
                    int hy = removeZone.y + removeZone.height / 2 - hh / 2;
                    g2.setColor(new Color(255, 255, 255, 235));
                    g2.fillRoundRect(hx, hy, hw, hh, 8, 8);
                    g2.setColor(new Color(200, 50, 50));
                    g2.drawString(hint, hx + 10, hy + 4 + fm.getAscent());
                }
 
                if (insertionLine != null) {
                    // Orange so it stays visible against the blue selected row.
                    g2.setColor(new Color(235, 120, 20));
                    g2.fillRect(insertionLine.x, insertionLine.y, insertionLine.width, insertionLine.height);
                }
 
                int w = ghostSize.width;
                int h = ghostSize.height;
                int x = mouse.x - w / 2;
                int y = mouse.y - h / 2;
                g2.setComposite(AlphaComposite.SrcOver.derive(0.85f));
                g2.setColor(new Color(100, 149, 237));
                g2.fillRoundRect(x, y, w, h, 8, 8);
                g2.setColor(Color.WHITE);
                g2.drawString(ghostText, x + (w - fm.stringWidth(ghostText)) / 2,
                        y + (h + fm.getAscent() - fm.getDescent()) / 2);
            } finally {
                g2.dispose();
            }
        }
    }
 
    // ---- Rendering: make list cells look like buttons -------------------
    static class LevelButtonRenderer extends JLabel implements ListCellRenderer<Level> {
        // Created once: the renderer runs for every visible cell on every repaint.
        private static final Color SELECTED_BG = new Color(100, 149, 237);
        private static final Color NORMAL_BG = new Color(235, 235, 235);
        private static final Border CELL_BORDER = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                new EmptyBorder(10, 14, 10, 14));
 
        LevelButtonRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setBorder(CELL_BORDER);
        }
 
        @Override
        public Component getListCellRendererComponent(JList<? extends Level> list, Level value,
                int index, boolean isSelected, boolean cellHasFocus) {
            //setText(value.getTitle());
            setText((value == null) ? "Temp" : value.getTitle());
            setBackground(isSelected ? SELECTED_BG : NORMAL_BG);
            setForeground(isSelected ? Color.WHITE : Color.BLACK);
            return this;
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

        Title = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        LevelOrder = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel5 = new javax.swing.JPanel();
        EditorButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jButton1 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        LevelMenu = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        LevelData = new javax.swing.JTextPane();

        setLayout(new java.awt.BorderLayout());

        Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Title.setText("Run Builder");
        add(Title, java.awt.BorderLayout.PAGE_START);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Run"));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 50));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setViewportView(LevelOrder);

        jPanel3.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel3);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel1.add(filler1);
        jPanel1.add(jPanel5);

        EditorButton.setText("Level Creator");
        EditorButton.setAlignmentX(0.5F);
        EditorButton.addActionListener(this::EditorButtonActionPerformed);
        jPanel1.add(EditorButton);
        jPanel1.add(filler3);

        jButton1.setText("Main Menu");
        jButton1.setAlignmentX(0.5F);
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel1.add(jButton1);
        jPanel1.add(filler2);

        jPanel4.add(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Levels"));
        jPanel2.setName(""); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 50));
        jPanel2.setLayout(new java.awt.BorderLayout());

        LevelMenu.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(LevelMenu);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel2);

        add(jPanel4, java.awt.BorderLayout.CENTER);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(90, 120));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(90, 120));

        LevelData.setEditable(false);
        LevelData.setMinimumSize(new java.awt.Dimension(90, 400));
        LevelData.setPreferredSize(new java.awt.Dimension(90, 400));
        jScrollPane2.setViewportView(LevelData);

        add(jScrollPane2, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Level[] returnArray = new Level[LevelOrder.getModel().getSize()];
        for (int i = 0; i < LevelOrder.getModel().getSize(); i++) {
            returnArray[i] = LevelOrder.getModel().getElementAt(i);
        }
        rootFrame.setCurrentRun(returnArray);
        rootFrame.goToMenu();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void EditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditorButtonActionPerformed
        // TODO add your handling code here:
        rootFrame.goToEditor();
    }//GEN-LAST:event_EditorButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton EditorButton;
    private javax.swing.JTextPane LevelData;
    private javax.swing.JList<Level> LevelMenu;
    private javax.swing.JList<Level> LevelOrder;
    private javax.swing.JLabel Title;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
