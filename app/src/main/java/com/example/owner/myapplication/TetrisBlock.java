package com.example.owner.myapplication;

import java.util.ArrayList;
import java.util.List;

public class TetrisBlock {
    private static final int TYPE_SUM = 7;
    public int blockType, blockDirection;
    private int color;
    private int x, y;
    public TetrisBlock() {
    }
    public TetrisBlock(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public List<BlockUnit> getUnits(int x, int y) {
        this.x = x;
        this.y = y;
        return returnUnit();
    }
    public List<BlockUnit> returnUnit() {
        List<BlockUnit> units = new ArrayList<BlockUnit>();
        blockType = (int) (Math.random() * TYPE_SUM) + 1;
        blockDirection = 1;
        color = (int) (Math.random() * 4) + 1;
        units.clear();
        switch (blockType) {
            case 1:
                for (int i = 0; i < 4; i++) {
                    units.add(new BlockUnit(x + (-2 + i) * BlockUnit.UNIT_SIZE, y, color));
                }
                break;
            case 2:
                units.add(new BlockUnit(x + (-1 + 1) * BlockUnit.UNIT_SIZE, y - BlockUnit.UNIT_SIZE, color));
                for (int i = 0; i < 3; i++) {
                    units.add(new BlockUnit(x + (-1 + i) * BlockUnit.UNIT_SIZE, y, color));
                }
                break;
            case 3:
                for (int i = 0; i < 2; i++) {
                    units.add(new BlockUnit(x + (i - 1) * BlockUnit.UNIT_SIZE, y - BlockUnit.UNIT_SIZE, color));
                    units.add(new BlockUnit(x + (i - 1) * BlockUnit.UNIT_SIZE, y, color));
                }
                break;
            case 4:
                units.add(new BlockUnit(x + (-1 + 0) * BlockUnit.UNIT_SIZE, y - BlockUnit.UNIT_SIZE, color));
                for (int i = 0; i < 3; i++) {
                    units.add(new BlockUnit(x + (-1 + i) * BlockUnit.UNIT_SIZE, y, color));
                }
                break;
            case 5:
                units.add(new BlockUnit(x + (-1 + 2) * BlockUnit.UNIT_SIZE, y - BlockUnit.UNIT_SIZE, color));
                for (int i = 0; i < 3; i++) {
                    units.add(new BlockUnit(x + (-1 + i) * BlockUnit.UNIT_SIZE, y, color));
                }
                break;
            case 6:
                for (int i = 0; i < 2; i++) {
                    units.add(new BlockUnit(x + (-1 + i) * BlockUnit.UNIT_SIZE, y - BlockUnit.UNIT_SIZE, color));
                    units.add(new BlockUnit(x + i * BlockUnit.UNIT_SIZE, y, color));
                }
                break;
            case 7:
                for (int i = 0; i < 2; i++) {
                    units.add(new BlockUnit(x + i * BlockUnit.UNIT_SIZE, y - BlockUnit.UNIT_SIZE, color));
                    units.add(new BlockUnit(x + (-1 + i) * BlockUnit.UNIT_SIZE, y, color));
                }
                break;
        }
        return units;
    }
}