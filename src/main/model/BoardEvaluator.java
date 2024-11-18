package main.model;
import java.util.ArrayList;

public class BoardEvaluator {
    private final Board b;

    public BoardEvaluator(Board b) {
        this.b = b;
    }

    public double evaluatePosition() {

        int totalPhase = 4 + 4 + 8 + 8;
        int phase = (genPhase(totalPhase) * 256 + (totalPhase / 2))/ totalPhase;
        int opening = 0;
        int endgame = 0;

        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.addAll(b.getPieces(true));
        pieces.addAll(b.getPieces(false));
        for(Piece p : pieces) {
            switch(p) {
                case Pawn _ -> {

                }
                case Knight _ -> {

                }
                case Bishop _ -> {

                }
                case Rook _ -> {

                }
                case Queen _ -> {

                }
                default -> {}
            }
        }
        double eval = ((opening * (256 - phase)) + (endgame * phase)) / 256;

        return eval / 100.0;
    }
    private int genPhase(int totalPhase) {
        int phase = totalPhase;
        ArrayList<Piece> pieces = new ArrayList<>();
        for(Piece p : pieces) {
            switch(p) {
                case Bishop _, Knight _ -> phase -= 1;
                case Rook _ -> phase -= 2;
                case Queen _ -> phase -= 4;
                default -> {}
            }
        }
        return phase;
    }

    //Evaluates piece-square relations and mobility of pieces
    public int evalPieces(boolean side) {
        int pieceEval = 0;
        int bCount = 0;
        for (Piece p : b.getPieces(side)) {
            int specPieceEval;
            switch(p) {
                case Pawn _ -> {
                    specPieceEval = 100;
                    if(!isNotIsolated(p))
                        specPieceEval -= 50;

                    if(isDoubled(p))
                        specPieceEval -= 20;

                    if(isSupported(p))
                        specPieceEval += 15;

                    int direction = p.getSide() ? -1 : 1;
                    //Pass Pawn
                    if(!openFile(p.getRow(), p.getCol(), direction, !p.getSide())) {
                        //Semi or Full
                        if(sideColHasPawns(p)) {
                            specPieceEval += 50;
                        } else {
                            specPieceEval += 100;
                        }
                    }
                }
                case Knight _ -> {
                    specPieceEval = 320;

                    if(isSupported(p)) {
                        if(sideColHasPawns(p)) {
                            specPieceEval += 25;
                        } else {
                            specPieceEval += 65;
                        }
                    }
                    pieceEval += specPieceEval;
                }
                case Bishop _ -> {
                    specPieceEval = 330;
                    bCount++;

                    if(fianchetto(p)) {
                        if(openDiagonal(p)) {

                        } else {

                        }
                    }

                }
                case Rook _ -> {
                    specPieceEval = 500;
                    //Open file
                    int direction = p.getSide() ? -1 : 1;
                    if(openFile(p.getRow(),p.getCol(),direction, p.getSide())) {
                        //Fully or semi open file
                        if(openFile(p.getRow(),p.getCol(),direction, !p.getSide())) {
                            specPieceEval += 50;
                        }else {
                            specPieceEval += 75;
                        }
                    }
                }
                case Queen _ -> {
                    specPieceEval = 900;
                }
                default -> {
                    specPieceEval = 20000;
                }
            }
            pieceEval += specPieceEval;
        }

        if(bCount == 2)
            pieceEval += 70;

        return pieceEval;
    }

    //Pawn Specifics
    private boolean isNotIsolated(Piece p) {
        Tile[][] tiles = b.getTiles();
        for(int i = -1; i <= 1; i++) {
            int checkRow = p.getRow() + i;
            for(int j = -1; j <= 1; j++) {
                int checkCol = p.getCol() + j;
                Tile t = tiles[checkRow][checkCol];
                if (t.isOccupied()) {
                    Piece tilePiece = t.getPiece();
                    if (tilePiece instanceof Pawn && tilePiece.getSide() == p.getSide()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isDoubled(Piece p) {
        for(int i = 0; i <= 1; i++) {
            int checkRow = p.getRow() + i;
            if (checkRow != p.getRow()) {
                Tile t = b.getTiles()[checkRow][p.getCol()];
                if(checkTileForPawn(t, p.getSide()))
                    return true;
            }
        }
        return false;
    }
    private boolean isSupported(Piece p) {
        int i = p.getSide() ? 1 : -1;
        Tile t1 = b.getTiles()[p.getRow() + i][p.getCol() - 1];
        Tile t2 = b.getTiles()[p.getRow() + i][p.getCol() + 1];
        return checkTileForPawn(t1, p.getSide()) || checkTileForPawn(t2, p.getSide());
    }

    private boolean sideColHasPawns(Piece p) {
        int outOfBounds;
        int direction;
        if(p.getSide()) {
            outOfBounds = 0;
            direction = -1;
        } else {
            outOfBounds = 7;
            direction = 1;
        }
        if (p.getRow() == outOfBounds)
            return false;

        for(int j = -1; j <= 1; j += 2) {
            int checkCol = p.getCol() + j;
            if(checkCol < 0 || checkCol > 7)
                continue;

            if(openFile(p.getRow(), checkCol, direction,  p.getSide()))
                return true;
        }
        return false;
    }
    private boolean openFile(int startRow, int checkCol, int direction, boolean side) {
        for(int i = 1; i <= 7; i++) {
            int checkRow = startRow + (direction * i);
            if(checkRow < 0 || checkRow > 7)
                return false;

            Tile t = b.getTiles()[checkRow][checkCol];
            if(checkTileForPawn(t, !side))
                return true;
        }
        return false;
    }
    private boolean fianchetto(Piece p) {
        int reqRow = p.getSide() ? 6 : 1;
        if(p.getRow() != reqRow)
            return false;

        return p.getCol() == 1 || p.getCol() == 6;
    }
    private boolean openDiagonal(Piece p) {
        if(p.getCol() == p.getRow()) {

        } else {
            
        }

        return true;
    }
    private boolean checkDiagForPawn(int startRow, int checkCol) {
        return false;
    }
    private boolean checkTileForPawn(Tile t, boolean side) {
        if (t.isOccupied()) {
            Piece tilePiece = t.getPiece();
            return tilePiece instanceof Pawn && tilePiece.getSide() == side;
        }
        return false;
    }
}
