package main.presenter;

import main.model.Board;
import main.model.BoardEvaluator;
import main.model.Piece;
import main.model.Tile;
import main.view.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ChessPresenter implements PropertyChangeListener {
    private final Board b;
    private final BoardEvaluator eval;
    private final BoardGUI bGUI;
    private final Nameplate white;
    private final Nameplate black;
    private final Moveplate madeMoves;

    public ChessPresenter(ChessView cV) {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String whiteName = null;
        String blackName = null;
        switch(cV.welcome()) {
            case 1:
                try {
                    File file = new File("savefile.txt");
                    FileReader read = new FileReader(file);
                    BufferedReader bRead = new BufferedReader(read);

                    String[] data = new String[2];
                    for(int i = 0; bRead.ready(); i++){
                        data[i] = bRead.readLine();
                    }
                    fen = data[1];
                    String[] names = data[0].split("/");
                    whiteName = names[0];
                    blackName = names[1];

                    bRead.close();
                    read.close();
                } catch (IOException e) {
                    System.out.println("No savefile exists");
                }
                break;
            case 2:
                cV.rules();
                break;
        }

        this.b = new Board(fen);
        b.addPropertyChangeListener(this);
        this.eval = new BoardEvaluator(this.b);
        System.out.println(eval.evaluatePosition());

        cV.initializeComponents(whiteName, blackName);
        this.bGUI = cV.getBoardGUI();
        bGUI.updatePieces(genPieces());
        bGUI.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int row = y / 64;
                int col = x / 64;
                b.processTurn(row, col);
                switch (b.gameConditions()) {
                    case CHECKMATE:
                        cV.displayCheckmate(b.getTurn());
                        break;
                    case STALEMATE:
                        cV.displayStalemate();
                        break;
                }
            }
        });

        this.madeMoves = cV.getMovePlate();

        this.white = cV.getWhitePlate();
        this.black = cV.getBlackPlate();

        cV.getResignButton().addActionListener(e -> cV.displayResign(b.getTurn()));
        cV.getUndoButton().addActionListener(e -> b.undoTurn());
        cV.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (cV.saveGame() == 0) {
                    b.writeToFile(white.getName(), black.getName());
                }
            }
        });
    }

    public void propertyChange(PropertyChangeEvent e) {
        String s = e.getPropertyName();
        switch (s) {
            case "move" -> this.updateBoardGUI();
            case "takenPiece" -> this.addToNameplate((Piece) e.getNewValue());
            case "madeMove" -> this.addToMoveplate();
            case "undo" -> this.performUndo();
        }
    }
    private void addToMoveplate() {
        if (b.getTurn())
            madeMoves.addWhite(b.getLastMove().toString());
        else
            madeMoves.addBlack(b.getLastMove().toString());
    }
    private void addToNameplate(Piece p) {
        System.out.println(eval.evaluatePosition());
        boolean turn = b.getTurn();
        if (turn)
            white.addPiece(p.toString());
        else
            black.addPiece(p.toString());
    }
    private void updateBoardGUI() {
        bGUI.updatePieces(genPieces());
        bGUI.updateMoves(genMoves());
        bGUI.updateSelectedTile(getSelectedTile());
    }
    private char[][] genPieces() {
        Tile[][] tiles = b.getTiles();
        char[][] pieces = new char[8][8];
        for(int i = 0; i <= 7; i++ ) {
            for(int j = 0; j <= 7; j++) {
                Tile t = tiles[i][j];
                if(t.isOccupied())
                    pieces[i][j] = t.getPiece().toString().charAt(0);
                else
                    pieces[i][j] = ' ';
            }
        }
        return pieces;
    }
    private ArrayList<TileTuple> genMoves() {
        ArrayList<Tile> moves = b.getMoves();
        if (moves.isEmpty())
            return null;

        ArrayList<TileTuple> tuples = new ArrayList<>();
        for (Tile t : moves) {
            TileTuple tT = new TileTuple(t.getRow(), t.getCol(), false);

            if (t.isOccupied())
                if (t.getPiece().getSide() != b.getTurn())
                    tT.setIsEnemy(true);

            tuples.add(tT);
        }
        return tuples;
    }
    private TileTuple getSelectedTile() {
        Tile selectedTile = b.getSelectedTile();
        return new TileTuple(selectedTile.getRow(),selectedTile.getCol(), false);
    }

    private void performUndo() {
        updateBoardGUI();
        if(b.getTurn())
            white.removePiece();
        else
            black.removePiece();
        madeMoves.undoMove(b.getTurn());
    }

    public static void main(String[] args) {
        ChessView cV = new ChessView();
        new ChessPresenter(cV);
    }
}
