
public class Figures {

    public enum Type {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    private Type type;
    private boolean white;
    private int row;
    private int col;

    public Figures(Type type, boolean white, int row, int col) {
        this.type = type;
        this.white = white;
        this.row = row;
        this.col = col;
    }

    public Type getType() {
        return type;
    }

    public boolean isWhite() {
        return white;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isValidMove(Figures[][] board, int newRow, int newCol) {
        switch (type) {

            case PAWN:
                return validPawn(board, newRow, newCol);

            case KNIGHT:
                return validKnight(newRow, newCol);

            case KING:
                return validKing(newRow, newCol);

            case BISHOP:
                return validBishop(board, newRow, newCol);

            case ROOK:
                return validRook(board, newRow, newCol);

            case QUEEN:
                return validQueen(board, newRow, newCol);
        }

        return false;
    }

    private boolean validKing(int newRow, int newCol) {

        int dr = Math.abs(newRow - row);
        int dc = Math.abs(newCol - col);

        return dr <= 1 && dc <= 1;
    }
    
    private boolean validKnight(int newRow, int newCol) {

        int dr = Math.abs(newRow - row);
        int dc = Math.abs(newCol - col);

        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }
    
    private boolean validPawn(Figures[][] board, int newRow, int newCol) {

        int dir = isWhite() ? -1 : 1;

        // premik naprej
        if (newCol == col && board[newRow][newCol] == null) {
            return newRow == row + dir;
        }

        // diagonalni “capture”
        if (Math.abs(newCol - col) == 1 && newRow == row + dir) {
            return board[newRow][newCol] != null &&
                   board[newRow][newCol].isWhite() != this.isWhite();
        }

        return false;
    }

    private boolean validRook(Figures[][] board, int newRow, int newCol) {

        if (row != newRow && col != newCol) return false;

        int stepR = Integer.compare(newRow, row);
        int stepC = Integer.compare(newCol, col);

        int r = row + stepR;
        int c = col + stepC;

        while (r != newRow || c != newCol) {
            if (board[r][c] != null) return false;
            r += stepR;
            c += stepC;
        }

        return true;
    }
    
    private boolean validBishop(Figures[][] board, int newRow, int newCol) {

        if (Math.abs(newRow - row) != Math.abs(newCol - col)) return false;

        int stepR = Integer.compare(newRow, row);
        int stepC = Integer.compare(newCol, col);

        int r = row + stepR;
        int c = col + stepC;

        while (r != newRow) {
            if (board[r][c] != null) return false;
            r += stepR;
            c += stepC;
        }

        return true;
    }
    
    private boolean validQueen(Figures[][] board, int newRow, int newCol) {
        return validRook(board, newRow, newCol) ||
               validBishop(board, newRow, newCol);
    }
    
}