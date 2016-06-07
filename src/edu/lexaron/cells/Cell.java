/*
 *  Project name: CellSIM/Cell.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:28 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public abstract class Cell {

    private int ID;
    private double energy; // if > 0, alive
    private boolean alive;
    private int x;
    private int y; // position
    private int vision;
    private int movementCost;
    private double efficiency;
    private final Queue<Integer> path = new LinkedList();

    public Cell(int ID, int x, int y, double energy, int vision, int movementCost, double efficiency) {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.vision = vision;
        this.movementCost = movementCost;
        this.efficiency = efficiency;
        if (energy > 0) {
            this.alive = true;
        }
    }

    public abstract int[] lookForFood(World w);

    public abstract void mutate(World w);

    public abstract Circle drawCell();

    public void hunt(World w) {
        // CELL TYPE DEPENDANT
        int[] food = null;
        if (path.isEmpty()) {
            food = lookForFood(w);
            if (food != null) {
                findPath(w, food);
            } else {
                randomStep(w);
            }
        } else if (!path.isEmpty()) {
            usePath(w);
            eat(w);
        }

        if (energy >= 100) {
            mutate(w);
            setEnergy(energy / 2);
        }

    }

    public void findPath(World w, int[] foodLocation) {
        if (path.isEmpty()) {
            int difX = foodLocation[0] - x;
            int difY = foodLocation[1] - y;
//            System.out.println("Cell " + ID + " at " + x + "," + y + ", food distance: " + difX + "," + difY);
            if (difX > 0) {
                for (int i = 0; i < difX; i++) {
                    path.offer(3);
                }
            }
            if (difX < 0) {
                for (int i = 0; i < Math.abs(difX); i++) {
                    path.offer(9);
                }
            }
            if (difY > 0) {
                for (int i = 0; i < difY; i++) {
                    path.offer(6);
                }
            }
            if (difY < 0) {
                for (int i = 0; i < Math.abs(difY); i++) {
                    path.offer(12);
                }
            }
        }
    }

    public void usePath(World w) {
        int move = path.poll();
        switch (move) {
            case 3:
                moveRight(w);
                break;
            case 9:
                moveLeft(w);
                break;
            case 6:
                moveDown(w);
                break;
            case 12:
                moveUp(w);
                break;
            default:
                System.out.println("    Unknown move direction in MOVETOFOOD!");
                break;
        }
    }

    public void randomStep(World w) {
        switch (new Random().nextInt(4)) {
            case 0:
                moveUp(w);
                break;
            case 1:
                moveRight(w);
                break;
            case 2:
                moveDown(w);
                break;
            case 3:
                moveLeft(w);
                break;
            default:
                System.out.println("Unexpected roll in HUNT method.");
                break;
        }
    }

    public void moveRight(World w) { // code 3
        if ((x + movementCost) < w.getWidth() && w.getTheWorld()[x + movementCost][y].getCell() == null) {
            if (energy - movementCost >= 0) {
                w.getTheWorld()[x][y].setCell(null);
                setX(x + movementCost);
                w.getTheWorld()[x][y].setCell(this);
                setEnergy(Math.round(energy - (movementCost * efficiency)));
//                System.out.println("Cell " + ID + " moved to " + x + "," + y + ", energy: " + energy);                
            } else {
                setAlive(false);
                System.out.println("Cell " + ID + "  died on " + x + "," + y + ", energy: " + energy);
            }
        } else {
            moveLeft(w);
        }
    }

    public void moveLeft(World w) { // code 9
        if ((x - movementCost) >= 0 && w.getTheWorld()[x - movementCost][y].getCell() == null) {
            if (energy - movementCost >= 0) {
                w.getTheWorld()[x][y].setCell(null);
                setX(x - movementCost);
                w.getTheWorld()[x][y].setCell(this);
                setEnergy(Math.round(energy - (movementCost * efficiency)));
//                System.out.println("Cell " + ID + " moved to " + x + "," + y + ", energy: " + energy);                
            } else {
                setAlive(false);
                System.out.println("Cell " + ID + "  died on " + x + "," + y + ", energy: " + energy);
            }
        } else {
            moveRight(w);
        }
    }

    public void moveDown(World w) { // code 6
        if ((y + movementCost) < w.getHeight() && w.getTheWorld()[x][y + movementCost].getCell() == null) {
            if (energy - movementCost >= 0) {
                w.getTheWorld()[x][y].setCell(null);
                setY(y + movementCost);
                w.getTheWorld()[x][y].setCell(this);
                setEnergy(Math.round(energy - (movementCost * efficiency)));
//                System.out.println("Cell " + ID + " moved to " + x + "," + y + ", energy: " + energy);                
            } else {
                setAlive(false);
                System.out.println("Cell " + ID + "  died on " + x + "," + y + ", energy: " + energy);
            }
        } else {
            moveUp(w);
        }
    }

    public void moveUp(World w) { // code 12
        if ((y - movementCost) >= 0 && w.getTheWorld()[x][y - movementCost].getCell() == null) {
            if (energy - movementCost >= 0) {
                w.getTheWorld()[x][y].setCell(null);
                setY(y - movementCost);
                w.getTheWorld()[x][y].setCell(this);
                setEnergy(Math.round(energy - (movementCost * efficiency)));
//                System.out.println("Cell " + ID + " moved to " + x + "," + y + ", energy: " + energy);                
            } else {
                setAlive(false);
                System.out.println(ID + "  died on " + x + "," + y + ", energy: " + energy);
            }
        } else {
            moveDown(w);
        }
    }

    public void eat(World w) {
        if (w.getTheWorld()[x][y].getSugar() > 0) {
            setEnergy(energy + w.getTheWorld()[x][y].getSugar());
            System.out.println(ID + "   ate on " + x + "," + y + ": energy +" + w.getTheWorld()[x][y].getSugar());
            w.getTheWorld()[x][y].setSugar(0);
        }
    }

    // GET SET
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
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

    public int getMovementCost() {
        return movementCost;
    }

    public void setMovementCost(int movement) {
        this.movementCost = movement;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

}
