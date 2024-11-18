package main.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Nameplate extends JPanel {
    String name;
    StringBuilder pieces;
    public BufferedImage WPAWN;
    public BufferedImage WBISHOP;
    public BufferedImage WKNIGHT;
    public BufferedImage WQUEEN;
    public BufferedImage WROOK;
    public BufferedImage BPAWN;
    public BufferedImage BBISHOP;
    public BufferedImage BKNIGHT;
    public BufferedImage BQUEEN;
    public BufferedImage BROOK;

    public Nameplate(boolean containsWhitePieces, String name) {
        this.setPreferredSize(new Dimension(300, 80));
        this.setLayout(new FlowLayout(FlowLayout.LEADING));
        this.name = name;

        pieces = new StringBuilder();
        initializeIcons(containsWhitePieces);

        Font font = new Font("Arial", Font.BOLD, 18);
        JLabel displayName = new JLabel(name);
        displayName.setFont(font);
        displayName.setForeground(Color.WHITE);
        displayName.setPreferredSize(new Dimension(100, 75));

        this.add(displayName);
        Color BACKGROUND = new Color(49, 46, 43);
        this.setBackground(BACKGROUND);
    }

    public void initializeIcons(boolean containsWhitePieces) {
        try {
            if (containsWhitePieces) {
                WPAWN = ImageIO.read(new File("src/main/resources/whitePawnIcon.png"));
                WBISHOP = ImageIO.read(new File("src/main/resources/whiteBishopIcon.png"));
                WKNIGHT = ImageIO.read(new File("src/main/resources/whiteKnightIcon.png"));
                WQUEEN = ImageIO.read(new File("src/main/resources/whiteQueenIcon.png"));
                WROOK = ImageIO.read(new File("src/main/resources/whiteRookIcon.png"));
            } else {
                BPAWN = ImageIO.read(new File("src/main/resources/blackPawnIcon.png"));
                BBISHOP = ImageIO.read(new File("src/main/resources/blackBishopIcon.png"));
                BKNIGHT = ImageIO.read(new File("src/main/resources/blackKnightIcon.png"));
                BQUEEN = ImageIO.read(new File("src/main/resources/blackQueenIcon.png"));
                BROOK = ImageIO.read(new File("src/main/resources/blackRookIcon.png"));
            }
        } catch (IOException e) {
            System.out.println("File(s) are missing or corrupted");
        }
    }

    public String getName() {return this.name;}

    public void addPiece(String s) {
        this.pieces.append(s);
        repaint();
    }
    public void removePiece() {
        if(!pieces.isEmpty()) {
            this.pieces.deleteCharAt(pieces.length() - 1);
            repaint();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this. paintPieces(g);
    }
    private void paintPieces(Graphics g) {
        if (pieces != null) {
            char[] takenPieces = pieces.toString().toCharArray();
            Arrays.sort(takenPieces);
            int count = 100;
            for(char c : takenPieces) {
                g.drawImage(findImage(c), count,24, this);
                count += 32;
            }
        }
    }
    private BufferedImage findImage(char c) {
        return switch (c) {
            case 'p' -> BPAWN;
            case 'n' -> BKNIGHT;
            case 'b' -> BBISHOP;
            case 'r' -> BROOK;
            case 'q' -> BQUEEN;
            case 'P' -> WPAWN;
            case 'N' -> WKNIGHT;
            case 'B' -> WBISHOP;
            case 'R' -> WROOK;
            default -> WQUEEN;
        };
    }
}
