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

    public Tile(int id,Cell cell, Sugar sugar) {
        this.id = id;
        this.cell = cell;
        this.sugar = sugar;
        
        // Uništi potrebu za 2D nizom!
        // CILJ: Realno pomjeranje!
    }

    public int getId() {
        return id;
    }

    public Sugar getSugar() {
        return sugar;
    }

    public void setSugar(Sugar sugar) {
        this.sugar = sugar;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Cell getDeadCell() {
        return deadCell;
    }

    public void setDeadCell(Cell deadCell) {
        this.deadCell = deadCell;
    }
    
}
