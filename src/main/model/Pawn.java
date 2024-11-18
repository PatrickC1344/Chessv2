package main.model;
import java.util.ArrayList;

public class Pawn extends Piece{

    public Pawn(boolean side, int row, int col){super(side, row, col);}
    public String toString() {return this.getSide() ? "P" : "p";}
    private boolean canEnPassent(Board board, int row, int col) {
        if(!this.hasMoved())
            return false;

        if (row != 3 && this.getSide())
            return false;

        if (row != 4 && !this.getSide())
            return false;

        Move lastMove = board.getLastMove();

        if(!(lastMove.getMovedPiece() instanceof Pawn))
            return false;

        int sTileRow = lastMove.getStartRow();
        int sTileCol = lastMove.getStartCol();
        int eTileRow = lastMove.getEndRow();
        int eTileCol = lastMove.getEndCol();

        if (sTileCol != eTileCol)
            return false;

        int rowDiff = Math.abs(eTileRow- sTileRow);
        if (rowDiff != 2)
            return false;


        int colDiff = Math.abs(col - eTileCol);
        return colDiff == 1;
    }
    public ArrayList<Tile> getAttacks(Board board, int row, int col) {
        ArrayList<Tile> moves = new ArrayList<>();
        Tile[][] tiles = board.getTiles();
        Tile t;
        int i = this.getSide() ? -1 : 1;

        int checkRow = row + (i * 2);
        if (!this.hasMoved() && checkRow >= 0 && checkRow <= 7) {
            t = tiles[checkRow][col];
            if (!t.isOccupied() && !resultsInCheck(board, row, col, checkRow, col))
                moves.add(t);
        }

        checkRow = row + i;
        t = tiles[row + i][col];
        if (!t.isOccupied()  && !resultsInCheck(board, row, col, checkRow, col))
            moves.add(t);

        for (int j = -1; j <= 1; j+=2) {
            int checkCol = col + j;
            if (checkCol <= 7 && checkCol >= 0) {
                t = tiles[checkRow][checkCol];
                if (resultsInCheck(board, row, col, checkRow, checkCol))
                    continue;

                if (t.isOccupied() && t.getPiece().getSide() != this.getSide() )
                    moves.add(t);
            }
        }

        if (canEnPassent(board, row, col)) {
            Move lastMove = board.getLastMove();
            Tile eTile = lastMove.getEndTile();
            int eCol = eTile.getCol();
            moves.add(tiles[row + i][eCol]);
        }

        return moves;
    }
}
