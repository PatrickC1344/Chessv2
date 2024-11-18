package main.model;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Knight extends Piece{
    public Knight(boolean side, int row, int col){super(side, row, col);}
    public String toString() {return this.getSide() ? "N" : "n";}
    public ArrayList<Tile> getAttacks(Board board, int row, int col) {
        ArrayList<Tile> moves = new ArrayList<>();
        Tile[][] tiles = board.getTiles();
        Tile t;
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2 ; j++) {
                if (i == j  || i == -j || i == 0 || j == 0)
                    continue;

                if (row + i < 0 || col + j < 0 || row + i > 7 || col + j > 7)
                    continue;

                t = tiles[row + i][col + j];
                if (t.isOccupied()) {
                    if (t.getPiece().getSide() == this.getSide()) {
                        continue;
                    }
                }

                if (resultsInCheck(board, row, col, row + i, col + j))
                    continue;

                moves.add(t);
            }
        }

        return moves;
    }
}
