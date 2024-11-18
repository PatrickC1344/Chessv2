package main.model;
import java.util.ArrayList;

public abstract class Piece {
    private final boolean side;
    private boolean moved = false;
    private int row;
    private int col;

    public Piece(boolean side, int row, int col){
        this.side = side;
        this.row = row;
        this.col = col;
    }
    public void moved() {this.moved = true;}
    public void changePos(int row, int col) {
        setRow(row);
        setCol(col);
    }
    public void undoneMoved() {this.moved = false;}
    public boolean hasMoved() {return this.moved;}
    public boolean getSide() {return this.side;}
    public int getRow() {return this.row;}
    public int getCol() {return this.col;}
    public void setRow(int row) {this.row = row;}
    public void setCol(int col) {this.col = col;}

    public ArrayList<Tile> straights(Board board, int row, int col) {
        ArrayList<Tile> moves = new ArrayList<>();
        Tile[][] tiles = board.getTiles();
        for (int i = row - 1; i >= 0; i--) {
            if (checkTile(board, row, col, i, col, moves))
                break;
        }
        for (int i = row + 1; i <= 7; i++) {
            if (checkTile(board, row, col, i, col, moves))
                break;
        }
        for (int i = col - 1; i >= 0; i--) {
            if (checkTile(board, row, col, row, i, moves))
                break;
        }
        for (int i = col + 1; i <= 7; i++) {
            if (checkTile(board, row, col, row, i, moves))
                break;
        }
        return moves;
    }
    public ArrayList<Tile> diagonals(Board board, int row, int col) {
        ArrayList<Tile> moves = new ArrayList<>();
        for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++,j++) {
            if (checkTile(board, row, col, i, j, moves))
                break;
        }
        for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--,j++) {
            if (checkTile(board, row, col, i, j, moves))
                break;
        }
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--,j--) {
            if (checkTile(board, row, col, i, j, moves))
                break;
        }
        for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++,j--) {
            if (checkTile(board, row, col, i, j, moves))
                break;
        }
        return moves;
    }
    private boolean checkTile(Board board, int row, int col, int checkRow, int checkCol, ArrayList<Tile> moves) {
        Tile[][] tiles = board.getTiles();
        Tile t = tiles[checkRow][checkCol];
        if (t.isOccupied()) {
            if (t.getPiece().getSide() != this.getSide())
                moves.add(t);
            return true;
        }

        if (resultsInCheck(board, row, col, checkRow, checkCol))
            return true;

        moves.add(t);
        return false;
    }
    public boolean resultsInCheck(Board board, int sR, int sC, int eR, int eC) {
        Tile[][] tiles = board.getTiles();
        Tile sTile = tiles[sR][sC];
        Tile eTile = tiles[eR][eC];
        Piece ePiece = null;
        boolean test = false;
        if (eTile.isOccupied())
            ePiece = eTile.getPiece();

        eTile.setPiece(this);
        sTile.setPiece(null);
        if (board.isKingInCheck(this.getSide()))
            test = true;

        eTile.setPiece(ePiece);
        sTile.setPiece(this);
        return test;
    }

    public abstract ArrayList<Tile> getAttacks(Board board, int row, int col);
    public abstract String toString();
}
