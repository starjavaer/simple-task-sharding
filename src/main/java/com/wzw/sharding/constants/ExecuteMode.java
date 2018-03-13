package com.wzw.sharding.constants;

/**
 * @Author:WangZhiwen
 * @Description:
 * @Date:2018-3-13
 */
public enum ExecuteMode {

    RESTART(1),CONTINUE(2);

    private int value;

    private ExecuteMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static void main(String[] args) {
        System.out.println(ExecuteMode.RESTART.getValue());
    }

}
