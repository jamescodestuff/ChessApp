package com.example.chess;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.graphics.Color;
import com.example.chess.chessPieces.*;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChessboardActivity extends AppCompatActivity {

    private ChessPiece[][] board = new ChessPiece[8][8];
    private GridLayout chessboard;
    private boolean colorTurn = true;
    private boolean clicked = false;
    int origRow = -1, origCol = -1;
    private ChessPiece prev = null;
    private boolean ifUndo = true;

    private int[] undo = new int[4];

    private TextView turn;

    private boolean promoted = false;

    List<Pair<Integer,Integer>> history = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chessboard);
        chessboard = findViewById(R.id.board);
        makeBoard();
        initGrid();
        instantiateBoard();

        turn = findViewById(R.id.textView2);
        turn.setText("White's turn");

        Button resign = findViewById(R.id.resign);
        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent popup = new Intent(ChessboardActivity.this, saveActivity.class);
                String message;
                if(colorTurn) message = "White Wins!";
                else message = "Black Wins!";
                popup.putExtra("STRING_KEY", message);
                popup.putExtra("history_list", new Gson().toJson(history));
                startActivity(popup);
            }
        });

        Button draw = findViewById(R.id.draw);
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent popup = new Intent(ChessboardActivity.this, saveActivity.class);
                String message = "Draw!";
                popup.putExtra("STRING_KEY", message);
                popup.putExtra("history_list", new Gson().toJson(history));
                startActivity(popup);
            }
        });

        Button ai = findViewById(R.id.ai);
        ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ai();

            }
        });

        Button un = findViewById(R.id.button5);
        un.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make sure history is removed
                if(ifUndo) return;
                if(promoted) board[undo[2]][undo[3]] = new Pawn(board[undo[2]][undo[3]].getColor(),undo[2],undo[3]);
                movePiece(board,undo[2],undo[3],undo[0],undo[1]);
                placePiece(board,undo[2],undo[3],prev);

                ImageView originalPiece = (ImageView) chessboard.getChildAt(undo[2] * 8 + undo[3]);
                if(prev == null){
                    originalPiece.setImageDrawable(null);
                }
                else{
                    setImage(originalPiece,prev);
                }

                ImageView newPiece = (ImageView) chessboard.getChildAt(undo[0] * 8 + undo[1]);
                setImage(newPiece, board[undo[0]][undo[1]]);

                colorTurn = !colorTurn;
                if(colorTurn) turn.setText("White's turn");
                else turn.setText("Black's turn");
                ifUndo = true;
                history.remove(history.size() - 1);
                history.remove(history.size() - 1);


            }


        });

    }

    public void ai(){
        ChessPiece random = null;
        int randomRow = -1;
        int randomCol = -1;
        while(random == null){
            randomRow = (int) (Math.random() * 8);
            randomCol = (int) (Math.random() * 8);
            if(board[randomRow][randomCol] != null && board[randomRow][randomCol].getColor() == colorTurn) random = board[randomRow][randomCol];
        }

        int randomRowDes = -1;
        int randomColDes = -1;

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(random.isValidMove(board, randomRow, randomCol, i, j)){
                    int r = (int) (Math.random() * 2);
                    if(r == 0){
                        randomRowDes = i;
                        randomColDes = j;
                    }
                }
            }
        }
        if(randomColDes == -1) {
            ai();
            return;
        }

        if (board[randomRowDes][randomColDes] instanceof King) {
            //Change to something
            history.add(new Pair<>(randomRow, randomCol));
            history.add(new Pair<>(randomRowDes,randomColDes));

            //Add more stuff
            Intent popup = new Intent(ChessboardActivity.this, saveActivity.class);
            String winner = "";
            if(colorTurn) winner = "Black";
            else winner = "White";
            String message = winner + " Wins!";
            popup.putExtra("STRING_KEY", message);
            startActivity(popup);

        }

        undo[0] = randomRow;
        undo[1] = randomCol;
        undo[2] = randomRowDes;
        undo[3] = randomColDes;
        movePiece(board,randomRow,randomCol,randomRowDes,randomColDes);
        ImageView originalPiece = (ImageView) chessboard.getChildAt(randomRow * 8 + randomCol);
        originalPiece.setImageDrawable(null);
        ImageView newPiece = (ImageView) chessboard.getChildAt(randomRowDes * 8 + randomColDes);
        setImage(newPiece, board[randomRowDes][randomColDes]);
        ifUndo = false;
        history.add(new Pair<>(randomRow, randomCol));
        history.add(new Pair<>(randomRowDes,randomColDes));

        colorTurn = !colorTurn;
        if(colorTurn) turn.setText("White's turn");
        else turn.setText("Black's turn");
    }

    @SuppressLint("ClickableViewAccessibility")
    public void makeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ImageView tileView = new ImageView(this);
                // set the background color based on the position in the
                tileView.setBackgroundColor((row + col) % 2 == 0 ? Color.parseColor("#eeeed2") : Color.parseColor("#769656"));

                tileView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int newRow, newCol;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                // Save the original position of the chess piece
                                int index = chessboard.indexOfChild(v);

                                if (!clicked) {
                                    origCol = index % 8;
                                    origRow = index / 8;
                                    ImageView test = (ImageView) chessboard.getChildAt(origRow * 8 + origCol);
                                    if (test.getDrawable() == null) break;
                                    if (board[origRow][origCol].getColor() != colorTurn) {
                                        //Toast
                                        String message = "Wrong color";
                                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                        toast.show();
                                        break;
                                    }
                                    test.setBackgroundColor(Color.LTGRAY);
                                    clicked = true;
                                    break;
                                }

                                newCol = index % 8;
                                newRow = index / 8;
                                ImageView originalPiece = (ImageView) chessboard.getChildAt(origRow * 8 + origCol);
                                originalPiece.setBackgroundColor((origRow + origCol) % 2 == 0 ? Color.parseColor("#eeeed2") : Color.parseColor("#769656"));
                                clicked = false;

                                ChessPiece currentPiece = board[origRow][origCol];

                                if (origRow == newRow && origCol == newCol) {
                                    break;
                                }

                                //Check for valid move
                                //Check if validmove, enpassment
                                if (currentPiece.isValidMove(board, origRow, origCol, newRow, newCol)) {
                                    if (board[newRow][newCol] instanceof King) {
                                        //Change to something
                                        history.add(new Pair<>(origRow, origCol));
                                        history.add(new Pair<>(newRow,newCol));

                                        Intent popup = new Intent(ChessboardActivity.this, saveActivity.class);
                                        String message = "RESIGN!";
                                        if(colorTurn) message = "White Wins!";
                                        else message = "Black Wins!";
                                        popup.putExtra("STRING_KEY", message);
                                        popup.putExtra("history_list", new Gson().toJson(history));
                                        startActivity(popup);

                                    }
                                    prev = board[newRow][newCol];
                                    movePiece(board, origRow, origCol, newRow, newCol);
                                    promoted = false;
                                    //promotion
                                    if(currentPiece instanceof Pawn) {
                                        if(newRow == 0 || newCol == 7){
                                            promoted = true;
                                            board[newRow][newCol] = new Queen(currentPiece.getColor(),newRow,newCol);
                                        }
                                    }


                                }
                                 else { //Wrong move
                                    Toast toast = Toast.makeText(getApplicationContext(), "Illegal move, try again", Toast.LENGTH_SHORT);
                                    toast.show();
                                    break;
                                }

                                //End of check for valid move

                                undo[0] = origRow;
                                undo[1] = origCol;
                                undo[2] = newRow;
                                undo[3] = newCol;

                                originalPiece.setImageDrawable(null);
                                ImageView newPiece = (ImageView) chessboard.getChildAt(newRow * 8 + newCol);
                                setImage(newPiece, board[newRow][newCol]);
                                ifUndo = false;

                                history.add(new Pair<>(origRow, origCol));
                                history.add(new Pair<>(newRow,newCol));

                                colorTurn = !colorTurn;
                                if(colorTurn) turn.setText("White's turn");
                                else turn.setText("Black's turn");

                                boolean check1 = kingCheck(!colorTurn, board);
                                boolean check2 = kingCheck(colorTurn, board);

                                if (check1 || check2) {
                                    Snackbar.make(findViewById(android.R.id.content), "Check", Snackbar.LENGTH_SHORT).show();
                                }

                                break;
                            case MotionEvent.ACTION_UP:
                                // Handle the chess piece being released (e.g. check for valid move, update game state, etc.)
                                break;
                        }
                        return true;
                    }
                });

                // set the layout parameters of the tile
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 1;
                params.columnSpec = GridLayout.spec(col, 1f);
                params.rowSpec = GridLayout.spec(row, 1f);
                tileView.setLayoutParams(params);
                // add the tile to the grid layout
                chessboard.addView(tileView);
            }
        }
    }

    public void save(){


    }
    public void initGrid() {
        ImageView piece;
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 0);
        piece.setImageResource(R.drawable.br);
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 1);
        piece.setImageResource(R.drawable.bkn);
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 2);
        piece.setImageResource(R.drawable.bb);
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 3);
        piece.setImageResource(R.drawable.bq);
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 4);
        piece.setImageResource(R.drawable.bk);
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 5);
        piece.setImageResource(R.drawable.bb);
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 6);
        piece.setImageResource(R.drawable.bkn);
        piece = (ImageView) chessboard.getChildAt(0 * 8 + 7);
        piece.setImageResource(R.drawable.br);

        for (int i = 0; i < 8; i++) {
            piece = (ImageView) chessboard.getChildAt(1 * 8 + i);
            piece.setImageResource(R.drawable.bp);
        }

        for (int i = 0; i < 8; i++) {
            piece = (ImageView) chessboard.getChildAt(6 * 8 + i);
            piece.setImageResource(R.drawable.wp);
        }

        piece = (ImageView) chessboard.getChildAt(7 * 8 + 0);
        piece.setImageResource(R.drawable.wr);
        piece = (ImageView) chessboard.getChildAt(7 * 8 + 1);
        piece.setImageResource(R.drawable.wkn);
        piece = (ImageView) chessboard.getChildAt(7 * 8 + 2);
        piece.setImageResource(R.drawable.wb);
        piece = (ImageView) chessboard.getChildAt(7 * 8 + 3);
        piece.setImageResource(R.drawable.wq);
        piece = (ImageView) chessboard.getChildAt(7 * 8 + 4);
        piece.setImageResource(R.drawable.wk);
        piece = (ImageView) chessboard.getChildAt(7 * 8 + 5);
        piece.setImageResource(R.drawable.wb);
        piece = (ImageView) chessboard.getChildAt(7 * 8 + 6);
        piece.setImageResource(R.drawable.wkn);
        piece = (ImageView) chessboard.getChildAt(7 * 8 + 7);
        piece.setImageResource(R.drawable.wr);

    }

    public void instantiateBoard() {
        for (int col = 0; col < board.length; col++) {
            board[1][col] = new Pawn(false, 1, col);
        }
        for (int col = 0; col < board.length; col++) {
            board[6][col] = new Pawn(true, 6, col);
        }

        board[0][0] = new Rook(false, 0, 0);
        board[0][1] = new Knight(false, 0, 1);
        board[0][2] = new Bishop(false, 0, 2);
        board[0][3] = new Queen(false, 0, 3);
        board[0][4] = new King(false, 0, 4);
        board[0][5] = new Bishop(false, 0, 5);
        board[0][6] = new Knight(false, 0, 6);
        board[0][7] = new Rook(false, 0, 7);

        board[7][0] = new Rook(true, 7, 0);
        board[7][1] = new Knight(true, 7, 1);
        board[7][2] = new Bishop(true, 7, 2);
        board[7][3] = new Queen(true, 7, 3);
        board[7][4] = new King(true, 7, 4);
        board[7][5] = new Bishop(true, 7, 5);
        board[7][6] = new Knight(true, 7, 6);
        board[7][7] = new Rook(true, 7, 7);
    }

    public void movePiece(ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        //Assuming it is valid move
        try {
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = null;
        } catch (Exception ignored) {
        }

    }

    public void setImage(ImageView v, ChessPiece piece) {
        boolean color = piece.getColor();
        if (piece instanceof Pawn) {
            if (color) v.setImageResource(R.drawable.wp);
            else v.setImageResource(R.drawable.bp);
        } else if (piece instanceof Rook) {
            if (color) v.setImageResource(R.drawable.wr);
            else v.setImageResource(R.drawable.br);
        } else if (piece instanceof Bishop) {
            if (color) v.setImageResource(R.drawable.wb);
            else v.setImageResource(R.drawable.bb);
        } else if (piece instanceof Knight) {
            if (color) v.setImageResource(R.drawable.wkn);
            else v.setImageResource(R.drawable.bkn);
        } else if (piece instanceof Queen) {
            if (color) v.setImageResource(R.drawable.wq);
            else v.setImageResource(R.drawable.bq);
        } else if (piece instanceof King) {
            if (color) v.setImageResource(R.drawable.wk);
            else v.setImageResource(R.drawable.bk);
        } else {
            v.setImageResource(0);
        }
    }

    public boolean kingCheck(boolean color, ChessPiece[][] b) {
        int kingRow = -1;
        int kingCol = -1;
        //Find King
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (b[row][col] instanceof King && b[row][col].getColor() == color) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = b[row][col];
                if (piece != null && piece.getColor() != color) {
                    if (piece.isValidMove(b, row, col, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public void placePiece(ChessPiece[][] board, int row, int col, ChessPiece piece) {
        board[row][col] = piece;
    }




}



