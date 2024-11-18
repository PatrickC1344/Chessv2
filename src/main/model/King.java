package main.model;
import java.util.ArrayList;

public class King extends Piece{

    public King(boolean side, int row, int col){super(side, row, col);}
    public String toString() {return this.getSide() ? "K" : "k";}

    public boolean hasWestCastlingPriv(Board board, int rowOfSide) {
        Tile[][] tiles = board.getTiles();

        if (!tiles[rowOfSide][0].isOccupied())
            return false;

        Piece p = tiles[rowOfSide][0].getPiece();
        return p instanceof Rook && !p.hasMoved() && p.getSide() == this.getSide();
    }
    public boolean hasEastCastlingPriv(Board board, int rowOfSide) {
        Tile[][] tiles = board.getTiles();

        if (!tiles[rowOfSide][7].isOccupied())
            return false;

        Piece p = tiles[rowOfSide][7].getPiece();
        return p instanceof Rook && !p.hasMoved() && p.getSide() == this.getSide();
    }
    private boolean canCastleEast(Board board, int rowOfSide, Tile sTile) {
        Tile[][] tiles = board.getTiles();

        if (!hasEastCastlingPriv(board, rowOfSide))
            return false;

        for(int i = 5; i <= 6; i++) {
            Tile t = tiles[rowOfSide][i];
            if (t.isOccupied())
                return false;

            if (this.isNotLegal(board, sTile, t))
                return false;
        }
        return true;
    }
    private boolean canCastleWest(Board board, int rowOfSide, Tile sTile) {
        Tile[][] tiles = board.getTiles();

        if (!hasWestCastlingPriv(board, rowOfSide))
            return false;

        for(int i = 3; i >= 1; i--) {
            Tile t = tiles[rowOfSide][i];
            if (t.isOccupied())
                return false;

            if (this.isNotLegal(board, sTile, t))
                return false;
        }
        return true;
    }

    private boolean isNotLegal(Board board, Tile sTile, Tile eTile) {
        boolean isLegal = true;
        board.setKTile(this.getSide(), eTile);

        if (board.isKingInCheck(this.getSide()))
            isLegal = false;

        board.setKTile(this.getSide(), sTile);
        return !isLegal;
    }
    public ArrayList<Tile> getAttacks(Board board, int row, int col) {
        ArrayList<Tile> moves = new ArrayList<>();
        Tile[][] tiles = board.getTiles();
        Tile sTile = tiles[row][col];
        Tile t;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1 ; j++) {
                if (i == 0 && j == 0)
                    continue;

                if (row + i < 0 || col + j < 0 || row + i > 7 || col + j > 7)
                    continue;

                t = tiles[row + i][col + j];

                if (t.getPiece() != null) {
                    if (t.getPiece().getSide() == this.getSide())
                        continue;
                }

                if(isNotLegal(board, sTile, t))
                    continue;

                moves.add(t);
            }
        }

        if(!(this.hasMoved() || board.isKingInCheck(this.getSide()))) {
            int rowOfSide = this.getSide() ? 7 : 0;

            if (canCastleEast(board, rowOfSide, sTile))
                moves.add(tiles[rowOfSide][6]);
            if (canCastleWest(board, rowOfSide, sTile))
                moves.add(tiles[rowOfSide][2]);
        }

        return moves;
    }
}
