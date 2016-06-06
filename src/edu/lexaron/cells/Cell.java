/*
 *  Project name: CellSIM/Cell.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:28 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
abstract public class Cell {

    private int ID;
    int energy; // if > 0, alive
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

    public void hunt(World w) {
        // CELL TYPE DEPENDANT
        eat(w);
        int[] food = lookForFood(w);
        if (food != null) {
            System.out.println("Cell " + getID() + " found food at " + food[0] + "," + food[1] + ", hunting!");
            moveToFood(w, food);
        } else {
            switch (new Random().nextInt(5)) {
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
                case 4:
                    break;
                default:
                    System.out.println("Unexpected roll in HUNT method.");
                    break;
            }
        }
        mutateVision();
    }

    public void mutateVision() {
        if (getEnergy() >= 60) {
            System.out.println("    Cell " + getID() + " mutates: VISION +1");
            setVision(getVision() + 1);
        }
    }

    public void moveToFood(World w, int[] foodLocation) {
        // coord.calc idea
        int difX = foodLocation[0] - this.getX();
        int difY = foodLocation[1] - this.getY();
//        System.out.println("Cell " + getID() + " at " + this.getX() + "," + this.getY() + ", food distance: " + difX + "," + difY);

        if (difX > 0) {
            for (int i = 0; i < difX; i++) {
                moveRight(w);
            }
        }
        if (difX < 0) {
            for (int i = 0; i < Math.abs(difX); i++) {
                moveLeft(w);
            }
        }
        if (difY > 0) {
            for (int i = 0; i < difY; i++) {
                moveDown(w);
            }
        }
        if (difY < 0) {
            for (int i = 0; i < Math.abs(difY); i++) {
                moveUp(w);
            }
        }
        // path idea
//        Queue<int[]> path = new LinkedList();
//        if (Math.abs(difX) > Math.abs(difY)) {
//            for (int i = 0; i < Math.abs(difX); i++) {
//                
//            }
//        } else if (Math.abs(difX) < Math.abs(difY)) {
//            
//        } else {
//            
//        }
    }

    public int[] lookForFood(World w) {
        // CELL TYPE DEPENDANT
        int[] sugarLocation = new int[2];

        return null;
    }

    public void moveRight(World w) {
        if (this.isAlive() && (getX() + movement) < w.getWidth()) {
            if (this.energy - this.movement >= 0) {
                w.getTheWorld()[this.getX()][this.getY()].setCell(null);
                this.setX(this.getX() + this.movement);
                w.getTheWorld()[this.getX()][this.getY()].setCell(this);
                this.energy = this.energy - this.movement;
                System.out.println("Cell " + this.ID + " moved to " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
                eat(w);
            } else {
                this.setAlive(false);
                System.out.println("Cell " + this.ID + "  died on " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
            }
        }

    }

    public void moveLeft(World w) {
        if (this.isAlive() && (getX() - movement) >= 0) {
            if (this.energy - this.movement >= 0) {
                w.getTheWorld()[this.getX()][this.getY()].setCell(null);
                this.setX(this.getX() - this.movement);
                w.getTheWorld()[this.getX()][this.getY()].setCell(this);
                this.energy = this.energy - this.movement;
                System.out.println("Cell " + this.ID + " moved to " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
                eat(w);
            } else {
                this.setAlive(false);
                System.out.println("Cell " + this.ID + "  died on " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
            }
        }

    }

    public void moveDown(World w) {
        if (this.isAlive() && (getY() + movement) < w.getHeight()) {
            if (this.energy - this.movement >= 0) {
                w.getTheWorld()[this.getX()][this.getY()].setCell(null);
                this.setY(this.getY() + this.movement);
                w.getTheWorld()[this.getX()][this.getY()].setCell(this);
                this.energy = this.energy - this.movement;
                System.out.println("Cell " + this.ID + " moved to " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
                eat(w);
            } else {
                this.setAlive(false);
                System.out.println("Cell " + this.ID + "  died on " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
            }
        }
    }

    public void moveUp(World w) {
        if (this.isAlive() && (getY() - movement) >= 0) {
            if (this.energy - this.movement >= 0) {
                w.getTheWorld()[this.getX()][this.getY()].setCell(null);
                this.setY(this.getY() - this.movement);
                w.getTheWorld()[this.getX()][this.getY()].setCell(this);
                this.energy = this.energy - this.movement;
                System.out.println("Cell " + this.ID + " moved to " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
                eat(w);
            } else {
                this.setAlive(false);
                System.out.println("Cell " + this.ID + "  died on " + this.getX() + "," + this.getY() + ", energy: " + this.energy);
            }
        }
    }

    public void eat(World w) {
        if (w.getTheWorld()[this.getX()][this.getY()].getSugar() > 0) {
            this.setEnergy(this.getEnergy() + w.getTheWorld()[this.getX()][this.getY()].getSugar());
            System.out.println("Cell " + this.ID + "   ate on " + this.getX() + "," + this.getY() + ", energy +" + w.getTheWorld()[this.getX()][this.getY()].getSugar());
            w.getTheWorld()[this.getX()][this.getY()].setSugar(0);

        }
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
            cell.setFill(Color.web("#00ffff"));
        } else {
            cell.setRadius(5);
            cell.setFill(Color.web("#008080"));
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
