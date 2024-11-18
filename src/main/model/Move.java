package main.model;

import java.util.ArrayList;

public class Move {
    private final Piece movedPiece;
    private final Piece takenPiece;
    private final Tile sTile, eTile;
    private final boolean hasMoved;
    private final boolean kingCastle;
    private final boolean queenCastle;
    private String stringRep;

    public Move(Piece p, Piece taken, Tile sTile, Tile eTile, Board board, boolean check, int promotion, boolean kingCastle, boolean queenCastle) {
        this.movedPiece = p;
        this.takenPiece = taken;
        this.hasMoved = p.hasMoved();
        this.sTile = sTile;
        this.eTile = eTile;
        this.kingCastle = kingCastle;
        this.queenCastle = queenCastle;
        this.setStringRep(board, check, promotion, kingCastle, queenCastle);
    }

    private void setStringRep(Board board, boolean check, int promotion, boolean kingCastle, boolean queenCastle) {
        String eString = eTile.toString();
        String sString = sTile.toString();
        StringBuilder sB = new StringBuilder();

        if (this.movedPiece instanceof Pawn) {
            if(this.sTile.getCol() == this.eTile.getCol())
                sB.append(eString);
            else
                sB.append(sString.charAt(0)).append('x').append(eString);

            if(promotion != 0)
                sB.append(genPromotion(promotion));

        } else {
            if (kingCastle)
                sB.append("O-O");
            else if (queenCastle)
                sB.append("O-O-O");
            else {
                sB.append(genMovedPiece());

                if (movedPiece instanceof Knight)
                    checkForKnights(board, sString, sB);

                if (takenPiece != null)
                    sB.append('x');

                sB.append(eString);
            }
        }
        if(check)
            sB.append("+");

        stringRep = sB.toString();
    }

    private void checkForKnights(Board board, String sString, StringBuilder sB) {
        ArrayList<Piece> pieces = board.getPieces(movedPiece.getSide());
        Piece knight = null;
        for(Piece p : pieces) {
            if (p instanceof Knight && p != movedPiece) {
                knight = p;
            }
        }
        if(knight != null) {
            Tile otherKnight = board.getTiles()[knight.getRow()][knight.getCol()];
            int rowDiff = Math.abs(eTile.getRow() - otherKnight.getRow());
            int colDiff = Math.abs(eTile.getCol() - otherKnight.getCol());
            if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
                sB.append(otherKnight.getCol() != sTile.getCol() ? sString.charAt(0) : sString.charAt(1));
            }
        }
    }
    private String genPromotion(int promotion) {
        return switch (promotion) {
            case 1 -> "=Q";
            case 2 -> "=R";
            case 3 -> "=N";
            default -> "=B";
        };
    }
    private String genMovedPiece() {
        return switch (this.movedPiece) {
            case Rook _ -> "R";
            case King _ -> "K";
            case Queen _ -> "Q";
            case Bishop _ -> "B";
            default -> "N";
        };
    }

    public Piece getMovedPiece() {return this.movedPiece;}
    public Piece getTakenPiece() {return this.takenPiece;}
    public Tile getEndTile() {return this.eTile;}
    public Tile getStartTile() {return this.sTile;}
    public int getEndRow() {return this.eTile.getRow();}
    public int getEndCol() {return this.eTile.getCol();}
    public int getStartRow() {return this.sTile.getRow();}
    public int getStartCol() {return this.sTile.getCol();}
    public boolean isFirstMove() {return !this.hasMoved;}
    public boolean wasKingCastle() {return this.kingCastle;}
    public boolean wasQueenCastle() {return this.queenCastle;}

    public String toString() {return stringRep;}
}
