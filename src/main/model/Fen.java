package main.model;

public class Fen {
    private final String s;
    private final boolean turn;
    private final Move lastMove;
    private final int fiftyMoveCount;
    private final int numberOfFullMoves;

    public Fen(Board board, boolean turn, Move lastMove, int fiftyMoveCount, int numberOfFullMoves) {
        this.turn = turn;
        this.lastMove = lastMove;
        this.fiftyMoveCount = fiftyMoveCount;
        this.numberOfFullMoves = numberOfFullMoves;

        StringBuilder sb = new StringBuilder();
        Tile[][] tiles = board.getTiles();
        int emptySpaceCount = 0;
        for (int i = 0; i <= 7; i++) {
            for(int j = 0; j <= 7; j++) {
                Tile t = tiles[i][j];
                if(t.isOccupied()){
                    if (emptySpaceCount != 0)
                        sb.append(emptySpaceCount);
                    emptySpaceCount = 0;

                    Piece p = t.getPiece();
                    sb.append(p.toString());
                }
                else {
                    emptySpaceCount++;
                }
            }
            if (emptySpaceCount != 0)
                sb.append(emptySpaceCount);
            emptySpaceCount = 0;
            sb.append("/");
        }
        sb.replace(sb.length() - 1, sb.length(), " ");

        //Dictating turns
        sb.append(turn ? "w " : "b ");
        sb.append(genCastlePrivs(board));

        this.s = sb.toString();
    }
    public String toString() {return this.s + genEnpassant(turn, lastMove) + " " + this.fiftyMoveCount + " " +  this.numberOfFullMoves;}
    public String getS() {return this.s;}

    private String genEnpassant(boolean turn, Move lastMove) {
        if (lastMove == null)
            return "-";

        if (!(lastMove.getMovedPiece() instanceof Pawn))
            return "-";

        if (lastMove.getStartCol() != lastMove.getEndCol())
            return "-";

        int rowDiff = Math.abs(lastMove.getStartRow() - lastMove.getEndRow());
        if (rowDiff != 2)
            return "-";

        int i  = turn ? 1 : -1;
        int row = -1 * (lastMove.getEndRow() + i - 8);
        return lastMove.getEndTile().getColRep() + row;
    }
    private String genCastlePrivs(Board board) {
        King wK = (King) board.getWKTile().getPiece();
        King bK = (King) board.getBKTile().getPiece();
        boolean wKEast = false;
        boolean wKWest = false;
        boolean bKEast = false;
        boolean bKWest = false;
        if (bK.hasMoved() && wK.hasMoved())
            return "-";

        if (!wK.hasMoved()) {
            wKEast = wK.hasEastCastlingPriv(board, 7);
            wKWest = wK.hasWestCastlingPriv(board, 7);
        }

        if (!bK.hasMoved()) {
            bKEast = bK.hasEastCastlingPriv(board, 0);
            bKWest = bK.hasWestCastlingPriv(board, 0);
        }

        StringBuilder tempS = new StringBuilder();
        if (wKEast)
            tempS.append("K");
        if (wKWest)
            tempS.append("Q");
        if (bKEast)
            tempS.append("k");
        if (bKWest)
            tempS.append("q");

        return tempS + " ";
    }

    public Move getLastMove() {return this.lastMove;}
    public int getFiftyMoveCount() {return this.fiftyMoveCount;}
    public int getNumberOfFullMoves() {return this.numberOfFullMoves;}

    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Fen checkFen))
            return false;

        return this.getS().equals(checkFen.getS());

    }
    public int hashCode() {
        int hash = 31;
        return hash * 7 + s.hashCode();
    }
}
