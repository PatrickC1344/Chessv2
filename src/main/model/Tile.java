package main.model;

public class Tile {
    private Piece p;
    private final int row;
    private final int col;
    private final String stringRep;

    public Tile(Piece p, int row, int col) {
        this.p = p;
        this.row = row;
        this.col = col;

        int rowRep = -1 * (this.row - 8);
        String[] colRep = {"a", "b", "c", "d", "e", "f", "g", "h"};
        this.stringRep = colRep[this.col] + rowRep;
    }

    public void setPiece(Piece p) {
        this.p = p;
    }
    public boolean isOccupied() {
        return this.p != null;
    }
    public Piece getPiece(){
        return this.p;
    }
    public int getRow() {
        return this.row;
    }
    public int getCol() {
        return this.col;
    }
    public String getColRep() {
        return this.stringRep.charAt(0) + "";
    }

    public String toString() {
        return this.stringRep;
    }

}
