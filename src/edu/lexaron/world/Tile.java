/*
 *  Project name: CellSIM/Tile.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:11 PM
 */
package edu.lexaron.world;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Tile {

    private final int id;
    private Cell cell;
    private Cell deadCell;
    private Sugar sugar;
    private Trail trail;

    /**
     *
     * @param id
     * @param cell
     * @param sugar
     * @param trail
     */
    public Tile(int id, Cell cell, Sugar sugar, Trail trail) {
        this.id = id;
        this.cell = cell;
        this.sugar = sugar;
        this.trail = trail;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public Sugar getSugar() {
        return sugar;
    }

    /**
     *
     * @param sugar
     */
    public void setSugar(Sugar sugar) {
        this.sugar = sugar;
    }

    /**
     *
     * @return
     */
    public Cell getCell() {
        return cell;
    }

    /**
     *
     * @param cell
     */
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    /**
     *
     * @return
     */
    public Cell getDeadCell() {
        return deadCell;
    }

    /**
     *
     * @param deadCell
     */
    public void setDeadCell(Cell deadCell) {
        this.deadCell = deadCell;
    }

    /**
     *
     * @return
     */
    public Trail getTrail() {
        return trail;
    }

    /**
     *
     * @param trail
     */
    public void setTrail(Trail trail) {
        this.trail = trail;
    }
}
