package main.view;

public class TileTuple {
    private final int row;
    private final int col;
    private boolean isEnemy;
    public TileTuple(int row, int col, boolean isEnemy) {
        this.row = row;
        this.col = col;
        this.isEnemy = isEnemy;
    }
    public int getRow() {return this.row;}
    public int getCol() {return this.col;}
    public void setIsEnemy(boolean isEnemy) {
        this.isEnemy = isEnemy;
    }
    public boolean isEnemy() {return isEnemy;}
}