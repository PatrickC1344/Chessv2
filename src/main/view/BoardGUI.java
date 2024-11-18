package main.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BoardGUI extends JPanel{
    private final Color GREY = new Color(0, 0, 0, 90);
    private final Color ATKRED = new Color(255, 0, 0, 120);
    private final Color PALE = new Color(235,236,208);
    private final Color GREN = new Color(119,149,86);
    private final Color SLCTYELOW = new Color(194,224,99,225);
    private char[][] pieces;
    private ArrayList<TileTuple> moves;
    private TileTuple selectedTile;
    public BufferedImage WPAWN;
    public BufferedImage WBISHOP;
    public BufferedImage WKNIGHT;
    public BufferedImage WKING;
    public BufferedImage WQUEEN;
    public BufferedImage WROOK;

    public BufferedImage BPAWN;
    public BufferedImage BBISHOP;
    public BufferedImage BKNIGHT;
    public BufferedImage BKING;
    public BufferedImage BQUEEN;
    public BufferedImage BROOK;

    public BoardGUI() {
        this.setPreferredSize(new Dimension(512,512));
        Color BACKGROUND = new Color(49, 46, 43);
        this.setBackground(BACKGROUND);
        this.moves = new ArrayList<>();
        startIcons();
    }
    private void startIcons(){
        try {
            WPAWN = ImageIO.read(new File("src/main/resources/whitePawn.png"));
            WBISHOP = ImageIO.read(new File("src/main/resources/whiteBishop.png"));
            WKNIGHT = ImageIO.read(new File("src/main/resources/whiteKnight.png"));
            WKING = ImageIO.read(new File("src/main/resources/whiteKing.png"));
            WQUEEN = ImageIO.read(new File("src/main/resources/whiteQueen.png"));
            WROOK = ImageIO.read(new File("src/main/resources/whiteRook.png"));

            BPAWN = ImageIO.read(new File("src/main/resources/blackPawn.png"));
            BBISHOP = ImageIO.read(new File("src/main/resources/blackBishop.png"));
            BKNIGHT = ImageIO.read(new File("src/main/resources/blackKnight.png"));
            BKING = ImageIO.read(new File("src/main/resources/blackKing.png"));
            BQUEEN = ImageIO.read(new File("src/main/resources/blackQueen.png"));
            BROOK = ImageIO.read(new File("src/main/resources/blackRook.png"));
        } catch (IOException e) {
            System.out.println("Files are corrupted or do not exist.");
        }
    }

    private void paintTiles(Graphics g) {
        boolean temp = true;
        for (int i = 0; i <= 7; i++) {
            for(int j = 0; j <= 7; j++) {
                Color c = temp ? PALE : GREN;

                if (selectedTile != null)
                    if (i == selectedTile.getRow() && j == selectedTile.getCol())
                        c = SLCTYELOW;

                g.setColor(c);
                g.fillRect(j * 64, i * 64, 64, 64);

                if (pieces[i][j] != ' ')
                    g.drawImage(findImage(i,j), j * 64 , i * 64, this);

                temp = !temp;
            }
            temp = !temp;
        }
    }
    private void paintCircles(Graphics g) {
        if (!(moves == null || moves.isEmpty())){
            for (TileTuple tT : moves) {
                int row = tT.getRow();
                int col = tT.getCol();
                Color c = tT.isEnemy() ? ATKRED : GREY;
                g.setColor(c);
                g.fillOval(col * 64 + 22, row * 64 + 22, 20, 20);
            }
        }
    }
    private BufferedImage findImage(int i, int j) {
        char c = pieces[i][j];
        return switch (c) {
            case 'p' -> BPAWN;
            case 'n' -> BKNIGHT;
            case 'b' -> BBISHOP;
            case 'r' -> BROOK;
            case 'q' -> BQUEEN;
            case 'k' -> BKING;
            case 'P' -> WPAWN;
            case 'N' -> WKNIGHT;
            case 'B' -> WBISHOP;
            case 'R' -> WROOK;
            case 'Q' -> WQUEEN;
            default -> WKING;
        };
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintTiles(g);
        paintCircles(g);
    }

    public void updatePieces(char[][] pieces) {
        this.pieces = pieces;
        repaint();
    }
    public void updateMoves(ArrayList<TileTuple> tuples) {
        this.moves = tuples;
        repaint();
    }
    public void updateSelectedTile(TileTuple selectedTile) {
        this.selectedTile = selectedTile;
        repaint();
    }
}

