/*
 *  Project name: CellSIM/Cell.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:28 PM
 */
package edu.lexaron.world;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public abstract class Cell {

    private String geneCode;
    private double energy; // if > 0, alive
    private boolean alive;
    private int x;
    private int y; // position
    private int vision;
    private int movement;
    private double efficiency;
    private final Queue<Integer> path = new LinkedList();
    private final String color;
    private int offspring = 1;
    int[] targetFood = null;

    public Cell(String ID, int x, int y, double energy, int vision, int movementCost, double efficiency, String color) {
        this.geneCode = ID;
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.vision = vision;
        this.movement = movementCost;
        this.efficiency = efficiency;
        if (energy > 0) {
            this.alive = true;
        }
        this.color = color;

    }

    public abstract int[] lookForFood(World w);

    public abstract void mutate(World w);

    public int[] findLocationFor(World w) {
        int[] location = new int[2];
        boolean found = false;
        outterloop:
        for (int i = (y - vision); i <= (y + vision); i++) {
            for (int j = (x - vision); j <= (x + vision); j++) {
                try {
                    if (w.getTheWorld()[y][x].getCell() == null) {
                        location[0] = y;
                        location[1] = x;
                        found = true;
                        break outterloop;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }
        }
        if (found) {
            return location;
        } else {
            return null;
        }

    }

    public void hunt(World w) {
        // CELL TYPE DEPENDANT
        if (this.isAlive()) {
            if (path.isEmpty()) {
                targetFood = lookForFood(w);
                if (targetFood != null) {
                    findPathTo(targetFood);
                } else {
                    randomStep(w);
                }
            }
            if (!path.isEmpty() && w.getTheWorld()[targetFood[0]][targetFood[1]].getSugar().getAmount() > 0) {
                usePath(w);
                if (path.isEmpty()) {
                    eat(w);
                    targetFood = null;
                }
            } else {
                path.clear();
            }

            if (energy >= 100) {
                mutate(w);
                setEnergy(energy / 2);
            }
        }
    }

    public void findPathTo(int[] targetLocation) {
        if (path.isEmpty()) {
            int difY = targetLocation[0] - y;
            int difX = targetLocation[1] - x;
//            System.out.println("Cell " + geneCode + " at " + x + "," + y + ", targetFood distance: " + difX + "," + difY);
            if (difX > 0) {
                for (int i = 0; i < Math.abs(difX); i++) {
                    path.offer(3);
                }
            }
            if (difX < 0) {
                for (int i = 0; i < Math.abs(difX); i++) {
                    path.offer(9);
                }
            }
            if (difY > 0) {
                for (int i = 0; i < Math.abs(difY); i++) {
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
        if (!path.isEmpty()) {
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

    }

    public void randomStep(World w) {
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

    public void moveUp(World w) { // code 12
        try {
            if (w.getTheWorld()[y - movement][x].getCell() == null) {
                if ((energy - (movement * efficiency)) >= 0) {
                    w.getTheWorld()[y][x].setCell(null);
                    setY(y - movement);
                    w.getTheWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved up...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    setAlive(false);
                    w.getTheWorld()[y][x].setCell(null);
                    w.getTheWorld()[y][x].setDeadCell(this);
                    System.out.println(geneCode + "  died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getTheWorld()[0][x].setCell(null);
            setY(w.getHeight() - 1);
            w.getTheWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }
    }

    public void moveRight(World w) { // code 3
        try {
            //(x + movement) < w.getWidth() && 
            if (w.getTheWorld()[y][x + movement].getCell() == null) {
                if ((energy - (movement * efficiency)) >= 0) {
                    w.getTheWorld()[y][x].setCell(null);
                    setX(x + movement);
                    w.getTheWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved right...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    setAlive(false);
                    w.getTheWorld()[y][x].setCell(null);
                    w.getTheWorld()[y][x].setDeadCell(this);
                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getTheWorld()[y][w.getWidth() - 1].setCell(null);
            setX(0);
            w.getTheWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }

    }

    public void moveDown(World w) { // code 6
        try {
            //(y + movement) < w.getHeight() &&
            if (w.getTheWorld()[y + movement][x].getCell() == null) {
                if ((energy - (movement * efficiency)) >= 0) {
                    w.getTheWorld()[y][x].setCell(null);
                    setY(y + movement);
                    w.getTheWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved down...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    setAlive(false);
                    w.getTheWorld()[y][x].setCell(null);
                    w.getTheWorld()[y][x].setDeadCell(this);
                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getTheWorld()[w.getHeight() - 1][x].setCell(null);
            setY(0);
            w.getTheWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }

    }

    public void moveLeft(World w) { // code 9
        try {
            //(x - movement) >= 0 && 
            if (w.getTheWorld()[y][x - movement].getCell() == null) {
                if ((energy - (movement * efficiency)) >= 0) {
                    w.getTheWorld()[y][x].setCell(null);
                    setX(x - movement);
                    w.getTheWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved left...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    setAlive(false);
                    w.getTheWorld()[y][x].setCell(null);
                    w.getTheWorld()[y][x].setDeadCell(this);
                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getTheWorld()[y][0].setCell(null);
            setX(w.getWidth() - 1);
            w.getTheWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }
    }

    public void eat(World w) {
        if (w.getTheWorld()[y][x].getSugar().getAmount() > 0) {
            w.getTheWorld()[y][x].setSmell(10);
            setEnergy(energy + w.getTheWorld()[y][x].getSugar().getAmount());
//            System.out.println(geneCode + "   ate on " + x + "," + y + ": energy +" + w.getTheWorld()[y][x].getSugar().getAmount());
            w.getTheWorld()[y][x].getSugar().setAmount(0);
        }
    }

    public int[] findFreeTile(World w) {
        int loc[] = new int[2];
        int rx = 0, ry = 0;
        try {
            do {
                rx = new Random().nextInt((((x + vision) - (x - vision)))) + (x - vision);
                ry = new Random().nextInt((((y + vision) - (y - vision)))) + (y - vision);
            } while (w.getTheWorld()[ry][rx].getCell() != null || (ry < 0 || rx < 0) || (ry > w.getHeight() - 1 || rx > w.getWidth() - 1));
//            System.out.println("FREE: " + rx + "," + ry + " for " + x + "," + y);
        } catch (ArrayIndexOutOfBoundsException ex) {

        }

        loc[0] = ry;
        loc[1] = rx;
        return loc;
    }

    // GET SET
    public String getGeneCode() {
        return geneCode;
    }

    public void setGeneCode(String geneCode) {
        this.geneCode = geneCode;
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
        return movement;
    }

    public void setMovementCost(int movement) {
        this.movement = movement;
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

    public int[] getTargetFood() {
        return targetFood;
    }

    public void setTargetFood(int[] targetFood) {
        this.targetFood = targetFood;
    }

    public String getColor() {
        return color;
    }

    public int getOffspring() {
        return offspring;
    }

    public void setOffspring(int offspring) {
        this.offspring = offspring;
    }

    public Queue<Integer> getPath() {
        return path;
    }

}
