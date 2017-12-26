package com.keyu.fight2048.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by focus on 2017/12/26.
 */

public class Message2048 implements Serializable {
    private static final long serialVersionUID = 1L;
    private int type;//消息类型；
    private String userName;//用户名
    private int mColumns;//2048棋盘大小
    private int itemNums[];//2048棋盘上的数字


    public Message2048(int columns) {
        this.mColumns = columns;
        itemNums = new int[mColumns * mColumns];

    }


    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message2048{" +
                "type=" + type +
                ", userName='" + userName + '\'' +
                ", mColumns=" + mColumns +
                ", itemNums=" + Arrays.toString(itemNums) +
                '}';
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getmColumns() {
        return mColumns;
    }

    public void setmColumns(int mColumns) {
        this.mColumns = mColumns;
    }

    public int[] getItemNums() {
        return itemNums;
    }

    public void setItemNums(int[] itemNums) {
        this.itemNums = itemNums;
    }

}
