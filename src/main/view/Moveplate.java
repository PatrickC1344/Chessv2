package main.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Moveplate extends JPanel {
    DefaultTableModel model;
    JTable table;
    JScrollPane pane;
    Color BACKGROUND = new Color(38, 37, 34);
    Color FONTCOLOR = new Color(169,169,169);

    public Moveplate() {
        this.setPreferredSize(new Dimension(240, 512));
        this.setBackground(BACKGROUND);

        model = new DefaultTableModel();
        table = new JTable(model);
        pane = new JScrollPane(table);

        initializeColumns();
        initializeTable();
        initializePane();

        this.add(pane, BorderLayout.CENTER);
    }

    private void initializeColumns(){
        model.addColumn("Moves");
        model.addColumn("White");
        model.addColumn("Black");
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
    }
    private void initializeTable() {
        table.getTableHeader().setUI(null);
        table.setShowGrid(false);
        table.setFont(new Font("Arial", Font.BOLD, 12));
        table.setForeground(FONTCOLOR);
        table.setBackground(BACKGROUND);
    }
    private void initializePane() {
        pane.setPreferredSize(new Dimension(230, 500));
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setBackground(BACKGROUND);
        pane.getViewport().setBackground(BACKGROUND);
        JScrollBar vCB = pane.getVerticalScrollBar();
        vCB.setForeground(Color.BLACK);
        vCB.setBackground(BACKGROUND);
        vCB.setUI(new ModernScrollBarUI());
    }

    public void addWhite(String whiteMove) {
        int row = table.getRowCount() + 1;
        model.addRow(new Object[] {row + ".", whiteMove});
    }
    public void addBlack(String blackMove) {
        int row = table.getRowCount() - 1;
        model.setValueAt(blackMove, row, 2);
    }
    public void undoMove(boolean turn) {
        int row = table.getRowCount() - 1;
        if(turn)
            model.removeRow(row);
        else
            model.setValueAt("",row, 2);
    }
}

class ModernScrollBarUI extends BasicScrollBarUI {
    private final Dimension d = new Dimension(5,100);
    protected Dimension getMaximumThumbSize() {return d;}
    protected Dimension getMinimumThumbSize() {return d;}
    protected JButton createIncreaseButton(int i) {return new NullButton();}
    protected JButton createDecreaseButton(int i) {return new NullButton();}

    protected void paintTrack(Graphics g, JComponent jc, Rectangle rect) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = rect.width / 2;
        int x = rect.x + ((rect.width - size) / 2);
        int y = rect.y;
        int height = rect.height;
        g2.setColor(scrollbar.getBackground());
        g2.fillRect(x, y, size, height);
    }
    protected void paintThumb(Graphics g, JComponent jc, Rectangle rect) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = rect.x;
        int y = rect.y;
        int height = rect.height;
        y+=8;
        height-=16;
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(x, y, rect.width, height, 10, 10);
    }

    private static class NullButton extends JButton {
        public NullButton() {setBorder(BorderFactory.createEmptyBorder());}
        public void paint(Graphics g) {}
    }
}