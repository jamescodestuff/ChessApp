package com.example.chess;

/**
 *  contains an abstract class ChessPiece which allows other classes to extend upon it and implment methods respecting to it's each indivisual chess piece
 * @author Joey Zheng jz813, Junfeng Wang jw1397
 * @since 2023-03-18
 * @version 1.0.0
 */
public class chessPieces{
    /**
     This is an abstract class representing a chess piece.
     It contains information about the piece's position and color, as well as methods for getting and setting these values.
     The isValidMove method is abstract and must be implemented by subclasses to determine if a move is valid for the particular type of piece.
     */
    public static abstract class ChessPiece {
        /**
         * declear a private int row
         */
        private int row;
        /**
         * declear a private int col
         */
        private int col;
        /**
         * declear a private boolean color
         */
        private boolean color; //True: White False: Black

        /**
         Constructor for creating a new chess piece.
         @param color The color of the piece.
         @param row The row of the piece on the board.
         @param col The column of the piece on the board.
         */
        public ChessPiece(boolean color, int row, int col) {
            this.row = row;
            this.col = col;
            this.color = color;
        }

        /**
         Returns the color of the chess piece.
         @return True for white, false for black.
         */
        public boolean getColor() {
            return color;
        }
        /**
         Returns the row of the chess piece on the board.
         @return The row of the piece.
         */
        public int getRow() {
            return row;
        }
        /**
         Returns the col of the chess piece on the board.
         @return The col of the piece.
         */
        public int getCol() {
            return col;
        }
        /**
         Sets the row of the chess piece on the board.
         @param row The new row value.
         */
        public void setRow(int row) {
            this.row = row;
        }
        /**
         Sets the col of the chess piece on the board.
         @param col The new row value.
         */
        public void setCol(int col) {
            this.col = col;
        }

        /**
         Determines if a move is valid for the particular type of chess piece.
         @param board The current state of the chess board.
         @param fromRow The row of the piece before the move.
         @param fromCol The column of the piece before the move.
         @param toRow The row of the piece after the move.
         @param toCol The column of the piece after the move.
         @return True if the move is valid, false otherwise.
         */
        public abstract boolean isValidMove(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol);
    }

    /**
     *  representing a pawn piece in chess. It has a constructor that initializes the piece's color, row, and column, as well as a method to check whether a given move is valid for the pawn.
     *  two additional methods to get and set a boolean variable enpassant and to return a string representation of the piece's color and type.
     */
    public static class Pawn extends ChessPiece {
        /**
         * declear a public boolean enpassant and set it to false
         */
        public boolean enpassant = false;

        /**
         * Creates a new instance of the Pawn class with color, row, and column position on the board.
         * calls the constructor of the superclass, ChessPiece, passing in the color, row, and column position.
         * @param color color of the Pawn
         * @param row row index of the Pawn
         * @param col col index of the Pawn
         */
        public Pawn(boolean color, int row, int col) {
            super(color, row, col);
        }

        /**
         * checks whether the move is within the bounds of the board, and then determines whether the move is valid based on the position of the pawn,
         *  whether the destination square is occupied, and whether the move is a valid pawn move according to the rules of chess.
         * @param board the board of the chess pieces will be displayed on
         * @param fromRow the current row index of the piece
         * @param fromCol the current col index of the piece
         * @param toRow the index location on the board where the piece wants to be placed
         * @param toCol the row and col number where piece wants to go
         * @return a true/false of whether if it's a valid move
         */
        public boolean isValidMove(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
            if(fromRow < 0 || fromCol >= 8 || toRow < 0 || toCol >= 8) return false; //has to be in board
            boolean atStart = fromRow == 1 || fromRow == 6;
            boolean isOccupied = board[toRow][toCol] != null;

            ChessPiece pawn = board[fromRow][fromCol];
            if(pawn.getColor()){ //white pieces
                //Can move 1 space up if empty or can move 2 spaces up if empty and is at starting position
                if( (fromCol == toCol && fromRow == toRow + 1 && !isOccupied) ) return true;
                if((atStart && fromCol == toCol && fromRow == toRow + 2 && !isOccupied)){
                    return true;
                }
                //Can move diagonal if there is a enemy piece diagonal
                if(isOccupied && !board[toRow][toCol].getColor() && Math.abs(fromCol - toCol) == 1 && fromRow == toRow + 1) return true;
            }
            else{ //black pieces
                if( (fromCol == toCol && fromRow == toRow - 1 && !isOccupied) ) return true;
                if((atStart && fromCol == toCol && fromRow == toRow - 2 && !isOccupied)){
                    return true;
                }
                if(isOccupied && board[toRow][toCol].getColor() && Math.abs(fromCol - toCol) == 1 && fromRow == toRow - 1) return true;

            }
            return false;
        }

        /**
         * gets the value of getEnpassant
         *@return the current value of the "getEnpassant" boolean variable.
         */
        public boolean getEnpassant(){
            return enpassant;
        }

        /**
         * sets the value of setEnpassant
         * @param x true/false
         */
        public void setEnpassant(boolean x){
            this.enpassant = x;
        }

        /**
         * checking getColor() function and returning it's respectable color of the piece
         * @return a string representation of the piece's color and type
         */
        public String toString(){
            if(this.getColor()) return "wP";
            else return "bP";
        }
    }

    /**
     *  Extends the "ChessPiece" class to check if the Rook has moved, and validate a move for the King on a given chess board.
     */
    //Rook: Horizontal/Vertical movement --- R
    public static class Rook extends ChessPiece {
        /**
         * Creates a new instance of the Rook class with color, row, and column position on the board.
         * calls the constructor of the superclass, ChessPiece, passing in the color, row, and column position.
         * @param color color of the Rook
         * @param row row index of the Rook
         * @param col col index of the Rook
         */
        public Rook(boolean color, int row, int col) {
            super(color, row, col);
        }

        /**
         * declear a private boolean hasMoved and set to false
         */
        private boolean hasMoved = false;
        /**
         * gets the value of hasMoved
         *@return the current value of the "hasMoved" boolean variable.
         */

        public boolean getHasMoved() {
            return hasMoved;
        }

        /**
         Sets the value of the "hasMoved" boolean variable.
         @param moved the new value for the "hasMoved" boolean variable.
         */

        public void setHasMoved(boolean moved) {
            this.hasMoved = moved;
        }

        /**
         * checks whether a move from one position to another on a chessboard is valid for a generic chess piece based on its movement rules.
         *  It considers the starting and ending positions, checks for obstruction, and ensures that the move is within the boundaries of the board.
         * @param board the board of the chess pieces will be displayed on
         * @param fromRow the current row index of the piece
         * @param fromCol the current col index of the piece
         * @param toRow the index location on the board where the piece wants to be placed
         * @param toCol the row and col number where piece wants to go
         * @return a true/false of whether if it's a valid move
         */
        public boolean isValidMove(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
            if(board[fromRow][fromCol] == null) return false;
            if(fromRow < 0 || fromCol >= 8 || toRow < 0 || toCol >= 8) return false; //has to be in board
            int rowInc;
            int colInc;
            int rowDif = fromRow - toRow;
            int colDif = fromCol - toCol;

            if(rowDif == 0 && colDif == 0) return false;

            if(rowDif == 0) rowInc = 0;
            else if(rowDif < 0) rowInc = 1;
            else rowInc = -1;

            if(colDif == 0) colInc = 0;
            else if(colDif < 0) colInc = 1;
            else colInc = -1;


            if((rowDif == 0 || colDif == 0) && (board[toRow][toCol] == null || board[fromRow][fromCol].getColor() != board[toRow][toCol].getColor())){
                fromRow += rowInc;
                fromCol += colInc;

                while(fromRow != toRow || fromCol != toCol){
                    if(board[fromRow][fromCol] != null) return false;
                    fromRow += rowInc;
                    fromCol += colInc;
                }
                return true;
            }

            return false;
        }

        /**
         * checking getColor() function and returning it's respectable color of the piece
         * @return a string representation of the piece's color and type
         */
        public String toString(){
            if(getColor()) return "wR";
            else return "bR";
        }
    }

    //Knight: moves 2 in one direction and one in another --- N
    /**
     * Knight class that inherits from a ChessPiece superclass.
     * It has a constructor that calls the superclass constructor and a method to check if a move is valid according to the rules of the Knight movement.
     *  It also has a toString method that returns a string representation of the piece's color and type.
     */
    public static class Knight extends ChessPiece {
        /**
         * Creates a new instance of the Knight class with color, row, and column position on the board.
         * calls the constructor of the superclass, ChessPiece, passing in the color, row, and column position.
         * @param color color of the Knight
         * @param row row index of the Knight
         * @param col col index of the Knight
         */

        public Knight(boolean color, int row, int col) {
            super(color, row, col);
        }

        /**
         * verifies if the destination position is within the boundaries of the board, if the destination position is unoccupied or occupied by an opposing piece
         *  if the move follows the L-shaped movement rules of a knight.
         * @param board the board of the chess pieces will be displayed on
         * @param fromRow the current row index of the piece
         * @param fromCol the current col index of the piece
         * @param toRow the index location on the board where the piece wants to be placed
         * @param toCol the row and col number where piece wants to go
         * @return a true/false of whether if it's a valid move
         */
        public boolean isValidMove(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
            if(fromRow < 0 || fromCol >= 8 || toRow < 0 || toCol >= 8) return false; //has to be in board
            boolean isOccupied = board[toRow][toCol] != null;
            if( ((Math.abs(fromRow - toRow) == 1 && Math.abs(fromCol - toCol) == 2) || (Math.abs(fromRow - toRow) == 2 && Math.abs(fromCol - toCol) == 1)) && (!isOccupied || (board[toRow][toCol].getColor() != board[fromRow][fromCol].getColor())) ) {
                return true;
            }
            return false;
        }

        /**
         * checking getColor() function and returning it's respectable color of the piece
         * @return a string representation of the piece's color and type
         */
        public String toString(){
            if(getColor()) return "wN";
            else return "bN";
        }
    }

    //Bishop: Diagonal movement --- B
    /**
     *  Bishop class that extends the ChessPiece, it has a constructor that initializes the color, row, and column position of the bishop.
     * It also has an isValidMove method to check if a move is valid according to the rules of a bishop's diagonal movement, toString method returns a
     *  string representation of the bishop's color and type.
     */
    public static class Bishop extends ChessPiece {
        /**
         * Creates a new instance of the Bishop class with color, row, and column position on the board.
         * calls the constructor of the superclass, ChessPiece, passing in the color, row, and column position.
         * @param color color of the Bishop
         * @param row row index of the Bishop
         * @param col col index of the Bisjop
         */
        public Bishop(boolean color, int row, int col) {
            super(color, row, col);
        }

        /**
         * Checks if the move is valid by verifying that the initial position is not empty, the destination position is within the boundaries of the board,
         *  and that the move follows the diagonal movement rules of the chess piece
         * @param board the board of the chess pieces will be displayed on
         * @param fromRow the current row index of the piece
         * @param fromCol the current col index of the piece
         * @param toRow the index location on the board where the piece wants to be placed
         * @param toCol the row and col number where piece wants to go
         * @return a true/false of whether if it's a valid move
         */
        public boolean isValidMove(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
            if(board[fromRow][fromCol] == null) return false;
            if(fromRow < 0 || fromCol >= 8 || toRow < 0 || toCol >= 8) return false; //has to be in board
            int rowInc;
            int colInc;
            int rowDif = fromRow - toRow;
            int colDif = fromCol - toCol;
            if(rowDif == 0) return false;
            if(rowDif < 0) rowInc = 1;
            else rowInc = -1;
            if(colDif < 0) colInc = 1;
            else colInc = -1;

            if(Math.abs(rowDif) == Math.abs(colDif) && (board[toRow][toCol] == null || board[fromRow][fromCol].getColor() != board[toRow][toCol].getColor())){
                fromRow += rowInc;
                fromCol += colInc;
                while(fromRow != toRow){
                    if(board[fromRow][fromCol] != null) return false;
                    fromRow += rowInc;
                    fromCol += colInc;
                }
                return true;
            }
            return false;
        }


        /**
         * checking getColor() function and returning it's respectable color of the piece
         * @return a string representation of the piece's color and type
         */
        public String toString(){
            if(getColor()) return "wB";
            else return "bB";
        }
    }

    //Queen: Bishop + Rook --- Q
    /**
     * Queen class that extends ChessPiece, includes methods to check if a move is valid and to get a string representation of the piece's color and type.
     *
     */
    public static class Queen extends ChessPiece {
        /**
         * Creates a new instance of the Queen class with color, row, and column position on the board.
         * calls the constructor of the superclass, ChessPiece, passing in the color, row, and column position.
         * @param color color of the Queen
         * @param row row index of the Queen
         * @param col col index of the Queen
         */
        public Queen(boolean color, int row, int col) {
            super(color, row, col);
        }

        /**
         * creates instances of Bishop and Rook objects and then returns the logical OR of the isValidMove
         * method calls for each of these objects with the input parameters.
         * @param board the board of the chess pieces will be displayed on
         * @param fromRow the current row index of the piece
         * @param fromCol the current col index of the piece
         * @param toRow the index location on the board where the piece wants to be placed
         * @param toCol the row and col number where piece wants to go
         * @return true if it's a valid move, false otherwise
         */
        public boolean isValidMove(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
            ChessPiece queen = board[fromRow][fromCol];
            Bishop queenDia = new Bishop(queen.getColor(),fromRow,fromCol);
            Rook queenHor = new Rook(queen.getColor(),fromRow,fromCol);
            return queenDia.isValidMove(board, fromRow, fromCol, toRow, toCol) || queenHor.isValidMove(board, fromRow, fromCol, toRow, toCol);
        }

        /**
         * checking getColor() function and returning it's respectable color of the piece
         * @return a string representation of the piece's color and type
         */
        public String toString(){
            if(getColor()) return "wQ";
            else return "bQ";
        }
    }

    //King: One space any direction --- K
    /**
     * Extends the "ChessPiece" class to check if the King has moved, and validate a move for the King on a given chess board.
     */
    public static class King extends ChessPiece {
        /**
         * declear a private boolean hasMoved and set to false
         */
        private boolean hasMoved = false;

        /**
         * gets the value of hasMoved
         *@return the current value of the "hasMoved" boolean variable.
         */
        public boolean getHasMoved() {
            return hasMoved;
        }

        /**
         Sets the value of the "hasMoved" boolean variable.
         @param moved the new value for the "hasMoved" boolean variable.
         */
        public void setHasMoved(boolean moved) {
            this.hasMoved = moved;
        }

        /**
         * constructor for a King class that takes a boolean value color and two integer values row and col as arguments,
         * which are passed to the constructor of the superclass ChessPiece
         * @param color color of the King
         * @param row row index of the King
         * @param col col index of the King
         */

        public King(boolean color, int row, int col) {
            super(color, row, col);
        }

        /**
         * checks if the move is within one square of the current position and
         * if the piece to be captured is not of the same color as the current piece.
         * @param board the board of the chess pieces will be displayed on
         * @param fromRow the current row index of the piece
         * @param fromCol the current col index of the piece
         * @param toRow the index location on the board where the piece wants to be placed
         * @param toCol the row and col number where piece wants to go
         * @return true if it's a valid move, false otherwise
         */
        public boolean isValidMove(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
            int absRowDiff = Math.abs(toRow - fromRow);
            int absColDiff = Math.abs(toCol - fromCol);
            if (absRowDiff <= 1 && absColDiff <= 1) {
                // check the peice it's trying to capture is not same color as the King
                if (board[toRow][toCol] == null || board[toRow][toCol].getColor() != getColor()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * checking getColor() function and returning it's respectable color of the piece
         * @return a string representation of the piece's color and type
         */
        public String toString(){
            if(getColor()) return "wK";
            else return "bK";
        }
    }
}