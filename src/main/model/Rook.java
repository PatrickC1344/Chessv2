package main.model;
import java.util.ArrayList;


public class Rook extends Piece{

    public Rook(boolean side, int row, int col){super(side, row, col);}
    public String toString() {return this.getSide() ? "R" : "r";}
    public ArrayList<Tile> getAttacks(Board board, int row, int col) {return this.straights(board, row, col);}
}
