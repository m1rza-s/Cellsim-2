/*
 *  Project name: CellSIM/Tile.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:11 PM
 */
package edu.lexaron.world;

import edu.lexaron.cells.Cell;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Tile {
    private final int id;
    private Cell cell;
    private int sugar; // -5 to 5

    public Tile(int id, Cell cell, int sugar) {
        this.id = id;
        this.cell = cell;
        this.sugar = sugar;
    }

    public int getId() {
        return id;
    }

    public int getSugar() {
        return sugar;
    }

    public void setSugar(int sugar) {
        this.sugar = sugar;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
    
    
}
