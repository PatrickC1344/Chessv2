package main.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class ChessView extends JFrame{
    BoardGUI bGUI;
    Nameplate white;
    Nameplate black;
    Moveplate madeMoves;
    ToolBar bar;

    public ChessView(){
        super("Chess");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void initializeComponents(String wName, String bName) {
        bar = new ToolBar();

        bGUI = new BoardGUI();

        String whiteName = wName;
        String blackName = bName;
        
        if (wName == null)
            whiteName = JOptionPane.showInputDialog(null, "Enter white player name: ", "White");
        if (bName == null)
            blackName = JOptionPane.showInputDialog(null, "Enter black player  name: ", "Black");

        white = new Nameplate(false, whiteName);
        black = new Nameplate(true, blackName);

        madeMoves = new Moveplate();
        this.add(bGUI, BorderLayout.CENTER);
        this.add(white, BorderLayout.SOUTH);
        this.add(black, BorderLayout.NORTH);
        this.add(madeMoves, BorderLayout.EAST);
        this.add(bar, BorderLayout.WEST);
        this.pack();
        this.setVisible(true);
    }
    public int welcome() {
        String[] options = new String[]{"Chess", "Load Game", "Rules"};
        return JOptionPane.showOptionDialog(null, "Welcome to Chess", "Start menu",JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }
    public void rules() {
        String[] options = new String[]{"Understood"};
        JOptionPane.showOptionDialog(null,
                """
                        Basic Rules
                        In chess, each player takes turns to make a single move. Players cannot
                        choose to skip a turn, and must move a piece. Each piece moves in a
                        specific way. Excluding the knight, pieces are unable to move through
                        pieces of any color. Pieces capture enemy pieces by landing on them.

                        Win/Loss/Stalemate
                        When a piece moves in a way that would allow a player to capture the\s
                        opponent's king on their next turn, it is considered a check. If a player
                        is in check, they can take the block the attack, take the attacking piece,
                        and/or move their king to a safe spot if legal. If none of the 3 options
                        above are legal then that player has been checkmated and loses, while the
                        attacking player wins. Currently, there are two functioning stalemate\s
                        conditions, if a player cannot make any legal moves and/or  both player's
                        pieces consists of either: a long king, king & bishop, or king & knight.

                        Special Moves
                        Promotion of a pawn: if a pawn reaches the opponent's side of the board
                        the pawn will be exchanged for another piece that isn't a pawn or king.
                        En Passant: a method of capturing when a pawn captures a horizontally
                        adjacent enemy pawn that has just made it's initial two square move
                        Castling: if a player's king and a rook haven't moved during the game,
                        there are no pieces in between the king & rook, and none of the squares
                        would result in a check on the king, then the king and rook switch places
                        """,
                "Rules",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }
    public void displayStalemate() {
        JOptionPane.showMessageDialog(this, "Draw by Stalemate", "Draw", JOptionPane.INFORMATION_MESSAGE, null);
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    public void displayCheckmate(boolean winner) {
        String win = winner ? "White" : "Black";
        JOptionPane.showMessageDialog(this, win + " wins by checkmate", "Checkmate", JOptionPane.INFORMATION_MESSAGE, null);
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    public void displayResign(boolean loser) {
        String loss = loser ? "White" : "Black";
        JOptionPane.showMessageDialog(this, loss + " resigns", "Resignation", JOptionPane.INFORMATION_MESSAGE, null);
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public int saveGame() {
        return JOptionPane.showConfirmDialog(null,"Save game?", "Save", JOptionPane.YES_NO_OPTION);
    }

    public BoardGUI getBoardGUI() {return this.bGUI;}
    public Nameplate getWhitePlate() {return this.white;}
    public Nameplate getBlackPlate() {return this.black;}
    public Moveplate getMovePlate() {return this.madeMoves;}
    public JButton getResignButton() {return this.bar.getResignButton();}
    public JButton getUndoButton() {return this.bar.getUndoButton();}
}
