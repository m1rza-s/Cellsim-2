/*
 *  Project name: CellSIM/Cell.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:28 PM
 */
package classes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Cell {

    private int ID;
    private int energy; // if > 0, alive
    private boolean alive;
    private int x;
    private int y; // position
    private int vision;
    private int movement;
    private int efficiency;

    public Cell(int ID, int x, int y, int energy, int vision, int movement, int efficiency) {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.vision = vision;
        this.movement = movement;
        this.efficiency = efficiency;
        if (energy > 0) {
            this.alive = true;
        }
    }

    public void moveRight(World w) {
        if (this.isAlive()) {
            if (this.energy - this.movement >= 0) {
                w.getTheWorld()[this.getX()][this.getY()].setCell(null);
                this.setX(this.getX() + this.movement);
                w.getTheWorld()[this.getX()][this.getY()].setCell(this);
                this.energy = this.energy - this.movement;
                System.out.println("Cell " + this.ID + " moved to " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
                eat(w);
            } else {
                this.setAlive(false);
                System.out.println("Cell " + this.ID + "  died on  " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
            }
        }

    }

    public void moveDown(World w) {
        if (this.energy - this.movement >= 0) {
            w.getTheWorld()[this.getX()][this.getY()].setCell(null);
            this.setY(this.getY() + this.movement);
            w.getTheWorld()[this.getX()][this.getY()].setCell(this);
            this.energy = this.energy - this.movement;
            System.out.println("Cell " + this.ID + " moved to " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
            eat(w);
        } else {
            this.setAlive(false);
            System.out.println("Cell " + this.ID + "  died on  " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
        }
    }

    public void eat(World w) {
        if (w.getTheWorld()[this.getX()][this.getY()].getSugar() > 0) {            
            this.setEnergy(this.getEnergy() + w.getTheWorld()[this.getX()][this.getY()].getSugar());
            System.out.println("Cell " + this.ID + "   ate on " + this.getX() + "," + this.getY() + ", energy +" + w.getTheWorld()[this.getX()][this.getY()].getSugar());
            w.getTheWorld()[this.getX()][this.getY()].setSugar(0);
            
        }
    }

    public Tile[][] look(Tile[][] theWorld) {
        Tile[][] perception = new Tile[vision][vision];

        return perception;
    }

    public Circle drawCell() {
        Circle cell = new Circle();
        if (this.energy > 50) {
            cell.setRadius(5);
        } else if (this.energy > 40) {
            cell.setRadius(4);
        } else {
            cell.setRadius(3);
        } 

        if (this.isAlive()) {
            cell.setFill(Color.web("#66ff33"));
        } else {
            cell.setRadius(5);
            cell.setFill(Color.web("#134d00"));
        }
        return cell;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getVision() {
        return vision;
    }

    public void setVision(int vision) {
        this.vision = vision;
    }

    public int getMovement() {
        return movement;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

}
