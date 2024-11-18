package main.model;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.beans.*;

public class Board {
    private final Tile[][] tiles = new Tile[8][8];
    private Tile selectedTile;
    private Tile oldTile;
    private Tile wKTile;
    private Tile bKTile;
    private Piece selectedPiece;
    private Piece takenPiece = null;

    private final ArrayList<Piece> blackPieces;
    private final ArrayList<Piece> whitePieces;
    private ArrayList<Tile> moves;
    private final ArrayList<Fen> fenList;
    private Move lastMove;

    int fiftyMoveCount;
    int numberOfFullMoves;
    boolean turn;
    boolean wasEnpassant = false;

    private final PropertyChangeSupport change;

    public Board(String fen) {
        moves = new ArrayList<>();
        fenList = new ArrayList<>();
        blackPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        lastMove = null;

        change = new PropertyChangeSupport(this);

        startBoard(fen);
        printBoard();
        change.firePropertyChange("tiles", null, null);
    }
    private void startBoard(String fen) {
        for (int i = 0; i<8;i++) {
            for (int j = 0; j < 8; j++) {
                tiles[i][j] = new Tile(null, i, j);
            }
        }

        String[] x = fen.split(" ");
        String[] pieces = x[0].split("/");

        for(int row = 0; row <= 7; row++) {
            String s = pieces[row];
            int index = 0;
            for(int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);

                if(Character.isDigit(c)) {
                    int delta = c - '0';
                    index += delta;
                } else {
                    Tile t = tiles[row][index];
                    Piece p = genPiece(Character.toLowerCase(c), Character.isUpperCase(c), t);
                    t.setPiece(p);
                    addPiece(p);
                    index++;

                    switch (p) {
                        case King _ -> {
                            if (p.getSide()) wKTile = t;
                            else bKTile = t;
                        }
                        case Pawn _ -> {
                            int rowOfPawns = p.getSide() ? 6 : 1;
                            if (t.getRow() != rowOfPawns)
                                p.moved();
                        }
                        default -> {}
                    }
                }
            }
        }

        String castling = x[2];
        switch (castling) {
            case "-" -> {
                wKTile.getPiece().moved();
                bKTile.getPiece().moved();
            }
            case "KQkq" -> {}
            case "KQ" -> bKTile.getPiece().moved();
            case "kq" -> wKTile.getPiece().moved();
            default -> adjustCastling(castling);
        }

        turn = x[1].equals("w");
        if (!x[3].equals("-"))
            lastMove = generateLastMove(x[3]);
        fiftyMoveCount = Integer.parseInt(x[4]);
        numberOfFullMoves = Integer.parseInt(x[5]);
    }
    private Piece genPiece(char c, boolean side, Tile t) {
        return switch (c) {
            case 'p' -> new Pawn(side, t.getRow(), t.getCol());
            case 'n' -> new Knight(side, t.getRow(), t.getCol());
            case 'b' -> new Bishop(side, t.getRow(), t.getCol());
            case 'r' -> new Rook(side, t.getRow(), t.getCol());
            case 'q' -> new Queen(side, t.getRow(), t.getCol());
            default -> new King(side, t.getRow(), t.getCol());
        };
    }
    public void adjustCastling(String castling) {
        char c = ' ';
        if(castling.contains("KQ")) {
            c = castling.charAt(2);
        } else if(castling.contains("kq")) {
            c = castling.charAt(0);
        }
        Tile t;
        switch(c) {
            case 'k' -> t = tiles[0][0];
            case 'q' -> t = tiles[0][7];
            case 'K' -> t = tiles[7][0];
            default -> t = tiles[7][7];
        }
        if(t.isOccupied()) {
            t.getPiece().moved();
        }
    }
    private Move generateLastMove(String pawnMove) {
        int row = (pawnMove.charAt(1) - '0') - 1;
        char[] colRep = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int col = 0;
        for(int i = 0; i <= 7; i++) {
            if(pawnMove.charAt(0) == colRep[i]) {
                col = i;
                break;
            }
        }
        int i = turn ? -2 : 2;
        Tile eTile = tiles[row][col];
        Tile sTile = tiles[row + i][col];

        return new Move(eTile.getPiece(),null, sTile,eTile, this, isKingInCheck(turn), 0,false, false);
    }
    public void printBoard() {
        for(int i = 0; i<= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                if(tiles[i][j].isOccupied())
                    System.out.print(tiles[i][j].getPiece());
                else
                    System.out.print(" ");
            }
            System.out.println();
        }
    }

    public Tile[][] getTiles() {return this.tiles;}
    public ArrayList<Piece> getPieces(boolean side) {return side ? whitePieces : blackPieces;}
    public ArrayList<Tile> getMoves() {return this.moves;}
    public Tile getSelectedTile() {return this.selectedTile;}
    public Tile getWKTile() {return this.wKTile;}
    public Tile getBKTile(){return this.bKTile;}
    public Move getLastMove() {return this.lastMove;}
    public boolean getTurn() {return this.turn;}
    public void setKTile(boolean side, Tile tile) {
        if(side)
            wKTile = tile;
        else
            bKTile = tile;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.change.addPropertyChangeListener(listener);
    }

    private void removePiece(Piece p) {
        if (p.getSide())
            whitePieces.remove(p);
        else
            blackPieces.remove(p);
    }
    private void addPiece(Piece p) {
        if (p.getSide())
            whitePieces.add(p);
        else
            blackPieces.add(p);
    }

    public void processTurn(int row, int col) {
        selectedTile = tiles[row][col];

        if (selectedTile == oldTile) {
            unselect();
        } else if (selectedPiece == null) {
            if (selectedTile.isOccupied() && selectedTile.getPiece().getSide() == turn) {
                select(row, col);
            }
        } else {
            if (selectedTile.isOccupied() && selectedTile.getPiece().getSide() == turn) {
                reselect(row, col);
            } else if (moves != null && moves.contains(selectedTile)) {
                move();
            } else {
                System.out.print("Make a valid move ");
                unselect();
            }
        }
        change.firePropertyChange("move", null, null);
    }
    private void move() {
        //Move logic
        int promotion = 0;
        boolean kingCastle = false;
        boolean queenCastle = false;
        wasEnpassant = false;
        takenPiece = null;
        fiftyMoveCount++;

        if (selectedPiece instanceof King) {
            if (selectedPiece.getSide())
                wKTile = selectedTile;
            else
                bKTile = selectedTile;

            if (wasCastlingMove()) {
                int colDiff = selectedTile.getCol() - oldTile.getCol();
                int row = selectedTile.getRow();
                if (colDiff > 0) {
                    Piece p = tiles[row][7].getPiece();
                    tiles[row][5].setPiece(p);
                    tiles[row][7].setPiece(null);
                    kingCastle = true;
                }else {
                    Piece p = tiles[row][0].getPiece();
                    tiles[row][3].setPiece(p);
                    tiles[row][0].setPiece(null);
                    queenCastle = true;
                }
            }
        }
        if (selectedPiece instanceof Pawn) {
            if (wasEnPassantMove()) {
                int i = turn ? 1 : -1;
                Tile t = tiles[selectedTile.getRow() + i][selectedTile.getCol()];
                takenPiece = t.getPiece();
                removePiece(takenPiece);
                t.setPiece(null);
                wasEnpassant = true;
                change.firePropertyChange("takenPiece", null, takenPiece);
            }

            int i = turn ? 0 : 7;
            if (selectedTile.getRow() == i) {
                removePiece(selectedPiece);
                promotion = promotion();
                Piece p = switch (promotion) {
                    case 1 -> new Queen(turn, selectedTile.getRow(), selectedTile.getCol());
                    case 2 -> new Rook(turn, selectedTile.getRow(), selectedTile.getCol());
                    case 3 -> new Knight(turn, selectedTile.getRow(), selectedTile.getCol());
                    default -> new Bishop(turn, selectedTile.getRow(), selectedTile.getCol());
                };
                addPiece(p);
                selectedPiece = p;
            }

            fiftyMoveCount = 0;
        }
        if (selectedTile.isOccupied()) {
            takenPiece = selectedTile.getPiece();
            removePiece(takenPiece);
            fiftyMoveCount = 0;
            change.firePropertyChange("takenPiece", null, takenPiece);
        }

        //Make the move
        selectedTile.setPiece(selectedPiece);
        oldTile.setPiece(null);

        //Generate the last move
        lastMove = new Move(selectedPiece, takenPiece, oldTile, selectedTile, this, isKingInCheck(!turn), promotion, kingCastle, queenCastle);
        change.firePropertyChange("madeMove", null, null);

        //Generate the FEN for the new position
        if (!turn) numberOfFullMoves++;
        fenList.add(new Fen(this, !turn, lastMove, fiftyMoveCount, numberOfFullMoves));

        //Reset variables
        selectedPiece.moved();
        selectedPiece = null;
        moves.clear();
        turn = !turn;
    }
    private void unselect() {
        selectedPiece = null;
        oldTile = null;
        if (moves != null) {
            moves.clear();
        }
        System.out.println("Unselected");
    }
    private void select(int row, int col) {
        selectedPiece = selectedTile.getPiece();
        oldTile = selectedTile;
        moves = selectedPiece.getAttacks(this, row, col);
    }
    private void reselect(int row, int col){
        oldTile = selectedTile;
        selectedPiece = selectedTile.getPiece();
        moves = selectedPiece.getAttacks(this,row, col);
        System.out.println("Reselected: " + selectedPiece + " " + row + " " + col);
    }
    private int promotion() {
        String[] options = {"Queen", "Rook", "Knight", "Bishop"};
        return JOptionPane.showOptionDialog(null, "Promote to:", "Promotion",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]) + 1;
    }
    private boolean wasEnPassantMove() {
        if (lastMove == null)
            return false;

        if(!(lastMove.getMovedPiece() instanceof Pawn))
            return false;

        if (selectedTile.getCol() == oldTile.getCol())
            return false;

        return !selectedTile.isOccupied();
    }
    private boolean wasCastlingMove() {
        if (selectedPiece.hasMoved())
            return false;

        int colDiff = Math.abs(oldTile.getCol() - selectedTile.getCol());
        return colDiff == 2;
    }
    public void undoTurn() {
        if(lastMove != null) {
            Piece movedPiece = lastMove.getMovedPiece();
            Tile eTile = lastMove.getEndTile();
            Tile sTile = lastMove.getStartTile();

            if(movedPiece instanceof King) {
                if(movedPiece.getSide())
                    wKTile = sTile;
                else
                    bKTile = sTile;
            }

            if(lastMove.isFirstMove())
                movedPiece.undoneMoved();

            if(lastMove.wasKingCastle()) {
                int row = eTile.getRow();
                Piece p = tiles[row][5].getPiece();
                p.undoneMoved();
                tiles[row][5].setPiece(null);
                tiles[row][7].setPiece(p);
            } else if (lastMove.wasQueenCastle()) {
                int row = eTile.getRow();
                Piece p = tiles[row][3].getPiece();
                p.undoneMoved();
                tiles[row][3].setPiece(null);
                tiles[row][0].setPiece(p);
            }

            if(!wasEnpassant) {
                eTile.setPiece(lastMove.getTakenPiece());
            } else {
                int i = turn ? -1 : 1;
                Tile t = tiles[eTile.getRow() + i][eTile.getCol()];
                t.setPiece(takenPiece);
                eTile.setPiece(null);
            }

            sTile.setPiece(movedPiece);
            addPiece(takenPiece);
            turn = !turn;

            fenList.removeLast();
            lastMove = fenList.isEmpty() ? null : fenList.getLast().getLastMove();
            fiftyMoveCount =  fenList.isEmpty() ? 0 : fenList.getLast().getFiftyMoveCount();
            numberOfFullMoves = fenList.isEmpty() ? 1 : fenList.getLast().getNumberOfFullMoves();
            change.firePropertyChange("undo", null, null);
        }
    }

    public boolean isKingInCheck(boolean side) {
        Tile kTile;
        if (side)
            kTile = wKTile;
        else
            kTile = bKTile;

        int row = kTile.getRow();
        int col = kTile.getCol();

        return checkK(side, row, col) || checkP(side, row, col) || checkSt8(side, row, col) || checkD(side, row, col);

    }
    private boolean checkK(boolean side, int row, int col) {
        Tile checkTile;
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2 ; j++) {
                if (i == j  || i == -j || i == 0 || j == 0)
                    continue;

                if (row + i < 0 || col + j < 0 || row + i > 7 || col + j > 7)
                    continue;

                checkTile = tiles[row + i][col + j];
                if (checkTile.isOccupied()) {
                    Piece p = checkTile.getPiece();
                    if (p instanceof Knight && p.getSide() != side)
                        return true;
                }
            }
        }
        return false;
    }
    private boolean checkP(boolean side, int row, int col) {
        int i = side ? -1 : 1;

        Tile t;
        for(int j = -1; j <= 1; j+=2) {
            if (col + j <= 0 || col + j >= 7)
                continue;
            t = tiles[row + i][col + j];
            if(t.isOccupied()) {
                Piece p = t.getPiece();
                if (p instanceof Pawn && p.getSide() != side)
                    return true;
            }
        }
        return false;
    }
    private boolean checkSt8(boolean side, int row, int col) {
        int check;
        for (int i = row - 1; i >= 0; i--) {
            check = checkRQ(side,i,col);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        for (int i = row + 1; i <= 7; i++) {
            check = checkRQ(side,i,col);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        for (int i = col - 1; i >= 0; i--) {
            check = checkRQ(side,row,i);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        for (int i = col + 1; i <= 7; i++) {
            check = checkRQ(side,row,i);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        return false;
    }
    private boolean checkD(boolean side, int row, int col) {
        int check;
        for (int i = row + 1, j = col + 1; i <= 7 && j <= 7; i++,j++) {
            check = checkBQ(side,i,j);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        for (int i = row - 1, j = col + 1; i >= 0 && j <= 7; i--,j++) {
            check = checkBQ(side,i,j);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--,j--) {
            check = checkBQ(side,i,j);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        for (int i = row + 1, j = col - 1; i <= 7 && j >= 0; i++,j--) {
            check = checkBQ(side,i,j);
            if(check == 0)
                break;
            else if (check == 1)
                return true;
        }
        return false;
    }
    private int checkRQ(boolean side, int checkRow, int checkCol) {
        Tile t = tiles[checkRow][checkCol];
        if (t.isOccupied()) {
            Piece p = t.getPiece();
            if ((p instanceof Queen || p instanceof Rook) && p.getSide() != side) {
                return 1;
            }
            return 0;
        }
        return -1;
    }
    private int checkBQ(boolean side, int checkRow, int checkCol) {
        Tile t = tiles[checkRow][checkCol];
        if (t.isOccupied()) {
            Piece p = t.getPiece();
            if ((p instanceof Queen || p instanceof Bishop) && p.getSide() != side) {
                return 1;
            }
            return 0;
        }
        return -1;
    }

    private boolean noFPStalemate() {
        if (whitePieces.size() > 3 || blackPieces.size() > 3)
            return false;

        int wNCount = 0;
        int bNCount = 0;
        int wBCount = 0;
        int bBCount = 0;
        for(Piece p : whitePieces) {
            if (p instanceof Rook || p instanceof Queen || p instanceof Pawn)
                return false;
            else if (p instanceof Knight)
                wNCount++;
            else if (p instanceof Bishop)
                wBCount++;
        }
        for(Piece p : blackPieces) {
            if (p instanceof Rook || p instanceof Queen || p instanceof Pawn)
                return false;
            else if (p instanceof Knight)
                bNCount++;
            else if (p instanceof Bishop)
                bBCount++;
        }

        if (wBCount == 2 || bBCount == 2)
            return false;
        return (wBCount != 1 || wNCount != 1) && (bBCount != 1 || bNCount != 1);
    }
    private boolean noMoveStalemate() {
        ArrayList<Piece> pieces = turn ? whitePieces : blackPieces;
        for (Piece p : pieces) {
            Tile t = tiles[p.getRow()][p.getCol()];
            if (!p.getAttacks(this, t.getRow(), t.getCol()).isEmpty())
                return false;
        }
        return true;
    }
    private boolean fiftyMoveRule() {return fiftyMoveCount >= 100;}
    private boolean repetition() {
        int count = 0;
        if (fenList.isEmpty())
            return false;

        Fen lastFen  = fenList.getLast();
        for (Fen f : fenList) {
            if (lastFen.hashCode() == f.hashCode())
                count ++;
        }
        return count >= 3;
    }
    public GameState gameConditions() {
        boolean noMoveSM = noMoveStalemate();
        if(isKingInCheck(turn) && noMoveSM) {
            return GameState.CHECKMATE;
        } else if (noMoveSM || noFPStalemate() || fiftyMoveRule() || repetition()) {
            return GameState.STALEMATE;
        }
        return GameState.INPLAY;
    }

    public void writeToFile(String whiteName, String blackName) {
        try {
            Fen f = null;
            if(!fenList.isEmpty())
                f = fenList.getLast();

            if (f != null) {
                File file = new File("savefile.txt");
                FileWriter write = new FileWriter(file);
                write.write(whiteName + "/" + blackName + "\n");
                write.write(f.toString());
                write.close();
            }
        } catch(IOException e) {
            System.out.println("File could not be found or created");
        }
    }
}
