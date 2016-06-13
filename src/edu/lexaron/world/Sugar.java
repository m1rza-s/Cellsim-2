/*
 *  Project name: CellSIM/Sugar.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 10, 2016, 3:15:02 PM
 */
package edu.lexaron.world;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Sugar {

    private int amount;
    private final int x;
    private final int y;

    public Sugar(int x, int y, int amount) {
        this.amount = amount;
        this.x = x;
        this.y = y;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
