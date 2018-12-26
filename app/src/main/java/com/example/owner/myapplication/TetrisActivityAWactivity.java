package com.example.owner.myapplication;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class TetrisActivityAWactivity extends AppCompatActivity {
    private NextBlockView nextBlockView;
    private TetrisViewAW tetrisViewAW;
    private TextView gameStatusTip;
    private MediaPlayer mp=new MediaPlayer();
    public TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris_awactivity);
        nextBlockView = (NextBlockView) findViewById(R.id.nextBlockView1);
        tetrisViewAW = (TetrisViewAW) findViewById(R.id.tetrisViewAW1);
        tetrisViewAW.setFather(this);
        gameStatusTip = (TextView) findViewById(R.id.game_staus_tip);
        score = (TextView) findViewById(R.id.score);
        mp =MediaPlayer.create(this, R.raw.tetris);

    }
    public void setNextBlockView(List<BlockUnit> blockUnits, int div_x) {
        nextBlockView.setBlockUnits(blockUnits, div_x);
    }

    public void startGame(View view) {
        tetrisViewAW.startGame();
        gameStatusTip.setText("遊戲運行中");
        mp.start();
    }

    public void pauseGame(View view) {
        tetrisViewAW.pauseGame();
        gameStatusTip.setText("遊戲已暫停");
    }

    public void continueGame(View view) {
        tetrisViewAW.continueGame();
        gameStatusTip.setText("遊戲運行中");
    }

    public void stopGame(View view) {
        tetrisViewAW.stopGame();
        score.setText(""+0);
        gameStatusTip.setText("遊戲已停止");
    }

    public void toLeft(View view) {
        tetrisViewAW.toLeft();
    }
    public void toRight(View view) {
        tetrisViewAW.toRight();
    }
    public void toRoute(View view) {
        tetrisViewAW.route();
    }
}
