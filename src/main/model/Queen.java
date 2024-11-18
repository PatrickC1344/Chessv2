package main.model;
import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(boolean side, int row, int col){super(side, row, col);}
    public String toString() {return this.getSide() ? "Q" : "q";}
    public ArrayList<Tile> getAttacks(Board board, int row, int col) {
        ArrayList<Tile> moves = new ArrayList<>();
        moves.addAll(this.straights(board,row,col));
        moves.addAll(this.diagonals(board,row,col));
        return moves;
    }
}
