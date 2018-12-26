package com.example.owner.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TetrisViewAW extends View {
    public static final int beginPoint = 10;
    private static int max_x, max_y;
    private static int num_x = 0, num_y = 0;
    private static Paint paintWall = null;
    private static Paint paintBlock = null;
    private static final int BOUND_WIDTH_OF_WALL = 2;
    private List<BlockUnit> blockUnits = new ArrayList<BlockUnit>();
    private List<BlockUnit> blockUnitBufs = new ArrayList<BlockUnit>();
    private List<BlockUnit> routeBlockUnitBufs = new ArrayList<BlockUnit>();
    private List<BlockUnit> allBlockUnits = new ArrayList<BlockUnit>();
    private TetrisActivityAWactivity father = null;
    private int[] map = new int[100];
    private Thread mainThread = null;
    private boolean gameStatus = false;
    private boolean runningStatus = false;
    private static final int color[] = { Color.parseColor("#FF6600"), Color.BLUE, Color.RED, Color.GREEN, Color.GRAY };
    private int xx, yy;
    private TetrisBlock tetrisBlock;
    private int score = 0;
    private int blockType = 0;
    public TetrisViewAW(Context context) {
        this(context, null);
    }
    public TetrisViewAW(Context context, AttributeSet attrs){
        super(context, attrs);
        if (paintWall == null) {
            paintWall = new Paint();
            paintWall.setColor(Color.LTGRAY);
            paintWall.setStyle(Paint.Style.STROKE);
            paintWall.setStrokeWidth(BOUND_WIDTH_OF_WALL + 1);
        }
        if (paintBlock == null) {
            paintBlock = new Paint();
            paintBlock.setColor(Color.parseColor("#FF6600"));
        }
        tetrisBlock = new TetrisBlock();
        routeBlockUnitBufs = tetrisBlock.getUnits(beginPoint, beginPoint);
        Arrays.fill(map, 0);
    }
    public void setFather(TetrisActivityAWactivity tetrisActivityAW) {
        father = tetrisActivityAW;
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        max_x = getWidth();
        max_y = getHeight();
        RectF rel;
        num_x = 0;
        num_y = 0;
        for (int i = beginPoint; i < max_x - BlockUnit.UNIT_SIZE; i += BlockUnit.UNIT_SIZE) {
            for (int j = beginPoint; j < max_y - BlockUnit.UNIT_SIZE; j += BlockUnit.UNIT_SIZE) {
                rel = new RectF(i, j, i + BlockUnit.UNIT_SIZE, j + BlockUnit.UNIT_SIZE);
                canvas.drawRoundRect(rel, 8, 8, paintWall);
                num_y++;
            }
            num_x++;
        }
        int len = blockUnits.size();
        for (int i = 0; i < len; i++) {
            int x = blockUnits.get(i).x;
            int y = blockUnits.get(i).y;
            paintBlock.setColor(color[blockUnits.get(i).color]);
            rel = new RectF(x + BOUND_WIDTH_OF_WALL, y + BOUND_WIDTH_OF_WALL,
                    x + BlockUnit.UNIT_SIZE - BOUND_WIDTH_OF_WALL, y + BlockUnit.UNIT_SIZE - BOUND_WIDTH_OF_WALL);
            canvas.drawRoundRect(rel, 8, 8, paintBlock);
        }
        len = allBlockUnits.size();
        for (int i = 0; i < len; i++) {
            int x = allBlockUnits.get(i).x;
            int y = allBlockUnits.get(i).y;
            paintBlock.setColor(color[allBlockUnits.get(i).color]);
            rel = new RectF(x + BOUND_WIDTH_OF_WALL, y + BOUND_WIDTH_OF_WALL,
                    x + BlockUnit.UNIT_SIZE - BOUND_WIDTH_OF_WALL, y + BlockUnit.UNIT_SIZE - BOUND_WIDTH_OF_WALL);
            canvas.drawRoundRect(rel, 8, 8, paintBlock);
        }
    }
    public void startGame() {
        gameStatus = true;
        runningStatus = true;
        if (mainThread == null || !mainThread.isAlive()) {
            getNewBlock();
            mainThread = new Thread(new MainThread());
            mainThread.start();
        }
    }
    public void pauseGame() {
        runningStatus = false;
    }
    public void continueGame() {
        runningStatus = true;
    }
    public void stopGame() {
        runningStatus = false;
        gameStatus = false;
        mainThread.interrupt();
        blockUnits.clear();
        allBlockUnits.clear();
        score = 0;
        invalidate();
    }
    public void toLeft() {
        if (BlockUnit.toLeft(blockUnits, max_x, allBlockUnits)) {
            xx = xx - BlockUnit.UNIT_SIZE;
        }
        invalidate();
    }
    public void toRight() {
        if (BlockUnit.toRight(blockUnits, max_x, allBlockUnits)) {
            xx = xx + BlockUnit.UNIT_SIZE;
        }
        invalidate();
    }
    public void route() {
        if (blockType == 3) {
            return;
        }
        if (routeBlockUnitBufs.size() != blockUnits.size()) {
            routeBlockUnitBufs = tetrisBlock.getUnits(xx, yy);
        }
        for (int i = 0; i < blockUnits.size(); i++) {
            routeBlockUnitBufs.get(i).x = blockUnits.get(i).x;
            routeBlockUnitBufs.get(i).y = blockUnits.get(i).y;
        }
        for (BlockUnit blockUnit : routeBlockUnitBufs) {
            int tx = blockUnit.x;
            int ty = blockUnit.y;
            blockUnit.x = -(ty - yy) + xx;
            blockUnit.y = tx - xx + yy;
        }
        routeTran(routeBlockUnitBufs);
        if (!BlockUnit.canRoute(routeBlockUnitBufs, allBlockUnits)) {
            return;
        }
        for (BlockUnit blockUnit : blockUnits) {
            int tx = blockUnit.x;
            int ty = blockUnit.y;
            blockUnit.x = -(ty - yy) + xx;
            blockUnit.y = tx - xx + yy;
        }
        routeTran(blockUnits);
        invalidate();
    }
    public void routeTran(List<BlockUnit> blockUnitsBuf) {
        boolean needLeftTran = false;
        boolean needRightTran = false;
        for (BlockUnit u : blockUnitsBuf) {
            if (u.x < beginPoint) {
                needLeftTran = true;
            }
            if (u.x > max_x - BlockUnit.UNIT_SIZE) {
                needRightTran = true;
            }
        }
        if (needLeftTran || needRightTran) {
            for (BlockUnit u : blockUnitsBuf) {
                if (needLeftTran) {
                    u.x = u.x + BlockUnit.UNIT_SIZE;
                } else if (needRightTran) {
                    u.x = u.x - BlockUnit.UNIT_SIZE;
                }
            }
            routeTran(blockUnitsBuf);
        } else {
            return;
        }
    }
    private void getNewBlock() {
        this.xx = beginPoint + (num_x / 2) * BlockUnit.UNIT_SIZE;
        this.yy = beginPoint;
        if (blockUnitBufs.size() == 0) {
            blockUnitBufs = tetrisBlock.getUnits(xx, yy);
        }
        blockUnits = blockUnitBufs;
        blockType = tetrisBlock.blockType;
        blockUnitBufs = tetrisBlock.getUnits(xx, yy);
        if (father != null) {
            father.setNextBlockView(blockUnitBufs, (num_x / 2) * BlockUnit.UNIT_SIZE);
        }
    }
    private class MainThread implements Runnable {

        @Override
        public void run() {
            while (gameStatus) {
                while (runningStatus) {
                    if (BlockUnit.canMoveToDown(blockUnits, max_y, allBlockUnits)) {
                        BlockUnit.toDown(blockUnits, max_y, allBlockUnits);
                        yy = yy + BlockUnit.UNIT_SIZE;
                    }else {
                        for (BlockUnit blockUnit : blockUnits) {
                            blockUnit.y = blockUnit.y + BlockUnit.UNIT_SIZE;
                            allBlockUnits.add(blockUnit);
                        }
                        for (BlockUnit u : blockUnits) {
                            int index = (int) ((u.y - beginPoint) / 50); // 计算所在行数
                            map[index]++;
                        }
                        int end = (int) ((max_y - 50 - beginPoint) / BlockUnit.UNIT_SIZE);
                        int full = (int) ((max_x - 50 - beginPoint) / BlockUnit.UNIT_SIZE) + 1;
                        try {
                            Thread.sleep(GameConfig.SPEED);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i <= end; i++) {
                            if (map[i] >= full) {
                                BlockUnit.remove(allBlockUnits, i);
                                score += 100;
                                map[i] = 0;
                                for (int j = i; j > 0; j--)
                                    map[j] = map[j - 1];
                                map[0] = 0;
                                for (BlockUnit blockUnit : allBlockUnits) {
                                    if (blockUnit.y < (i * BlockUnit.UNIT_SIZE + beginPoint)) {
                                        blockUnit.y = blockUnit.y + BlockUnit.UNIT_SIZE;
                                    }
                                }
                            }
                        }
                        father.runOnUiThread(new Runnable() {
                            public void run() {
                                father.score.setText("" + score);
                                invalidate();
                            }
                        });
                        try {
                            Thread.sleep(GameConfig.SPEED * 3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        father.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getNewBlock();
                                score += 10;
                                father.score.setText("" + score);
                            }
                        });
                    }
                    father.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    try {
                        Thread.sleep(GameConfig.SPEED);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}