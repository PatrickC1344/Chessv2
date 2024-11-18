package main.model;
import java.util.ArrayList;

public class Bishop extends Piece{
    public Bishop(boolean side, int row, int col){super(side, row, col);}
    public String toString() {return this.getSide() ? "B" : "b";}
    public ArrayList<Tile> getAttacks(Board board, int row, int col) {
        return this.diagonals(board, row, col);
    }
}



