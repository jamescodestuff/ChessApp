package com.example.chess;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class playbackActivity extends AppCompatActivity {
    private chessPieces.ChessPiece[][] board = new chessPieces.ChessPiece[8][8];
    private GridLayout chessboard;
    private boolean colorTurn = true;
    private int counter = 0;

    private TextView turn;

    private List<Pair<Integer,Integer>> history;



    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback);
        chessboard = findViewById(R.id.board);

        //Name of ser
        Intent intent = getIntent();
        String message = intent.getStringExtra("STRING_KEY");

        makeBoard();
        initGrid();
        instantiateBoard();

        turn = findViewById(R.id.textView2);
        turn.setText("White's turn");

        //Get previous move's data
        //Change hello to message
        SharedPreferences sharedPrefs = getSharedPreferences(message, MODE_PRIVATE);
        String serializedPairs = sharedPrefs.getString("pairs", null);

        if (serializedPairs != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Pair<Integer,Integer>>>(){}.getType();
            history = gson.fromJson(serializedPairs, listType);
            // use the deserialized `history` object as needed

        } else {
            System.out.println("did not work");
        }


        Button next = findViewById(R.id.button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(history == null || counter >= history.size()) return;
                Pair<Integer,Integer> pair1 = history.get(counter);
                Pair<Integer,Integer> pair2 = history.get(counter+1);

                //get cord
                int fromRow = pair1.first;
                int fromCol = pair1.second;
                int toRow = pair2.first;
                int toCol = pair2.second;

                chessPieces.ChessPiece currentPiece = board[fromRow][fromCol];

                movePiece(board, fromRow, fromCol, toRow, toCol);
                //promotion
                if(currentPiece instanceof chessPieces.Pawn) {
                    if(toRow == 0 || toCol == 7){
                        board[toRow][toCol] = new chessPieces.Queen(currentPiece.getColor(),toRow,toCol);
                    }
                }

                ImageView originalPiece = (ImageView) chessboard.getChildAt(fromRow * 8 + fromCol);
                originalPiece.setImageDrawable(null);
                ImageView newPiece = (ImageView) chessboard.getChildAt(toRow * 8 + toCol);
                setImage(newPiece, board[toRow][toCol]);
                colorTurn = !colorTurn;
                if(colorTurn) turn.setText("White's turn");
                else turn.setText("Black's turn");
                counter = counter + 2;
            }
        });

        Button home = findViewById(R.id.button3);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(playbackActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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
            board[1][col] = new chessPieces.Pawn(false, 1, col);
        }
        for (int col = 0; col < board.length; col++) {
            board[6][col] = new chessPieces.Pawn(true, 6, col);
        }

        board[0][0] = new chessPieces.Rook(false, 0, 0);
        board[0][1] = new chessPieces.Knight(false, 0, 1);
        board[0][2] = new chessPieces.Bishop(false, 0, 2);
        board[0][3] = new chessPieces.Queen(false, 0, 3);
        board[0][4] = new chessPieces.King(false, 0, 4);
        board[0][5] = new chessPieces.Bishop(false, 0, 5);
        board[0][6] = new chessPieces.Knight(false, 0, 6);
        board[0][7] = new chessPieces.Rook(false, 0, 7);

        board[7][0] = new chessPieces.Rook(true, 7, 0);
        board[7][1] = new chessPieces.Knight(true, 7, 1);
        board[7][2] = new chessPieces.Bishop(true, 7, 2);
        board[7][3] = new chessPieces.Queen(true, 7, 3);
        board[7][4] = new chessPieces.King(true, 7, 4);
        board[7][5] = new chessPieces.Bishop(true, 7, 5);
        board[7][6] = new chessPieces.Knight(true, 7, 6);
        board[7][7] = new chessPieces.Rook(true, 7, 7);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void makeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ImageView tileView = new ImageView(this);
                // set the background color based on the position in the
                tileView.setBackgroundColor((row + col) % 2 == 0 ? Color.parseColor("#eeeed2") : Color.parseColor("#769656"));
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

    public void setImage(ImageView v, chessPieces.ChessPiece piece) {
        boolean color = piece.getColor();
        if (piece instanceof chessPieces.Pawn) {
            if (color) v.setImageResource(R.drawable.wp);
            else v.setImageResource(R.drawable.bp);
        } else if (piece instanceof chessPieces.Rook) {
            if (color) v.setImageResource(R.drawable.wr);
            else v.setImageResource(R.drawable.br);
        } else if (piece instanceof chessPieces.Bishop) {
            if (color) v.setImageResource(R.drawable.wb);
            else v.setImageResource(R.drawable.bb);
        } else if (piece instanceof chessPieces.Knight) {
            if (color) v.setImageResource(R.drawable.wkn);
            else v.setImageResource(R.drawable.bkn);
        } else if (piece instanceof chessPieces.Queen) {
            if (color) v.setImageResource(R.drawable.wq);
            else v.setImageResource(R.drawable.bq);
        } else if (piece instanceof chessPieces.King) {
            if (color) v.setImageResource(R.drawable.wk);
            else v.setImageResource(R.drawable.bk);
        } else {
            v.setImageResource(0);
        }
    }

    public void movePiece(chessPieces.ChessPiece[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        //Assuming it is valid move
        try {
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = null;
        } catch (Exception ignored) {
        }

    }



}
