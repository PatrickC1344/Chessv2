package main.view;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ToolBar extends JPanel{
    JButton undo;
    JButton resign;
    Color BACKGROUND = new Color(38, 37, 34);

    public ToolBar() {
        super(new GridLayout(2,1));
        this.setPreferredSize(new Dimension(28, 512));
        this.setBackground(BACKGROUND);

        initializeButtons();

        this.add(undo);
        this.add(resign);
    }

    private void initializeButtons() {
        ImageIcon undoImg = null;
        ImageIcon resignImg = null;

        try {
            BufferedImage i = ImageIO.read(new File("src/main/resources/undo.jpg"));
            undoImg = new ImageIcon(i);
            i = ImageIO.read(new File("src/main/resources/resign.jpg"));
            resignImg = new ImageIcon(i);
        } catch(IOException e) {
            System.out.println("Files corrupt or missing");
        }

        undo = new JButton(undoImg);
        resign = new JButton(resignImg);
        undo.setBackground(BACKGROUND);
        resign.setBackground(BACKGROUND);

        Border emptyBorder = BorderFactory.createEmptyBorder();
        undo.setBorder(emptyBorder);
        resign.setBorder(emptyBorder);

        undo.setFocusPainted(false);
        resign.setFocusPainted(false);
    }

    public JButton getResignButton() {return this.resign;}
    public JButton getUndoButton() {return this.undo;}
}
