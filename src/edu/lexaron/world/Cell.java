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
    private final int movement;
    private double speed;
    private double efficiency;
    private final Queue<Integer> path = new LinkedList();
    private final String color;
    private int trailSize;
    private int offspring = 1;
    private int[] targetFood = null;
    private int oppositeRandomStep;
    private int lastRandomStep;

    /**
     *
     * @param ID
     * @param x
     * @param y
     * @param energy
     * @param vision
     * @param speed
     * @param efficiency
     * @param color
     */
    public Cell(String ID, int x, int y, double energy, int vision, double speed, double efficiency, String color) {
        this.geneCode = ID;
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.vision = vision;
        this.movement = 1;
        this.speed = speed;
        this.efficiency = efficiency;
        if (energy > 0) {
            this.alive = true;
        }
        this.color = color;
        trailSize = 50;
    }

    /**
     *
     * @param w
     * @return
     */
    public abstract int[] lookForFood(World w);

    /**
     *
     * @param w
     */
    public abstract void mutate(World w);

    /**
     *
     * @param w
     */
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
            if (!path.isEmpty() && w.getWorld()[targetFood[0]][targetFood[1]].getSugar().getAmount() > 0) {
                for (int i = 0; i < speed; i++) {
                    usePath(w);
                }

                if (path.isEmpty()) {
                    eat(w);
                }
            } else {
                path.clear();
            }

            if (energy >= 100) {
                mutate(w);
                setEnergy(energy / 3);
            }
        }
    }

    /**
     *
     * @param targetLocation
     */
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

    /**
     *
     * @param w
     */
    public void usePath(World w) {
        for (int i = 0; i < (int) getSpeed(); i++) {
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
    }

    /**
     *
     * @param w
     */
    public void randomStep(World w) {
        int roll = new Random().nextInt(5);
        while (roll == oppositeRandomStep && roll == lastRandomStep) {
            roll = new Random().nextInt(5);
        }
        switch (roll) {
            case 0:
                oppositeRandomStep = 2;
                lastRandomStep = 0;
                moveUp(w);
                break;
            case 1:
                oppositeRandomStep = 3;
                lastRandomStep = 1;
                moveRight(w);
                break;
            case 2:
                oppositeRandomStep = 0;
                lastRandomStep = 2;
                moveDown(w);
                break;
            case 3:
                oppositeRandomStep = 1;
                lastRandomStep = 3;
                moveLeft(w);
                break;
            case 4:
                break;
            default:
                System.out.println("Unexpected roll in HUNT method.");
                break;
        }
    }

    /**
     *
     * @param w
     */
    public void moveUp(World w) { // code 12
        try {
            if (w.getWorld()[y - movement][x].getCell() == null) {
                if ((energy - (movement * efficiency)) > 0) {
                    w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
                    w.getWorld()[y][x].setCell(null);
                    setY(y - movement);
                    w.getWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved up...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    alive = false;
                    w.getWorld()[y][x].setDeadCell(this);
                    w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + "  died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getWorld()[0][x].setCell(null);
            setY(w.getHeight() - 1);
            w.getWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }
    }

    /**
     *
     * @param w
     */
    public void moveRight(World w) { // code 3
        try {
            //(x + movement) < w.getWidth() && 
            if (w.getWorld()[y][x + movement].getCell() == null) {
                if ((energy - (movement * efficiency)) > 0) {
                    w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
                    w.getWorld()[y][x].setCell(null);
                    setX(x + movement);
                    w.getWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved right...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    alive = false;
                    w.getWorld()[y][x].setDeadCell(this);
                    w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getWorld()[y][w.getWidth() - 1].setCell(null);
            setX(0);
            w.getWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }

    }

    /**
     *
     * @param w
     */
    public void moveDown(World w) { // code 6
        try {
            //(y + movement) < w.getHeight() &&
            if (w.getWorld()[y + movement][x].getCell() == null) {
                if ((energy - (movement * efficiency)) > 0) {
                    w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
                    w.getWorld()[y][x].setCell(null);
                    setY(y + movement);
                    w.getWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved down...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    alive = false;
                    w.getWorld()[y][x].setDeadCell(this);
                    w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getWorld()[w.getHeight() - 1][x].setCell(null);
            setY(0);
            w.getWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }

    }

    /**
     *
     * @param w
     */
    public void moveLeft(World w) { // code 9
        try {
            //(x - movement) >= 0 && 
            if (w.getWorld()[y][x - movement].getCell() == null) {
                if ((energy - (movement * efficiency)) > 0) {
                    w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
                    w.getWorld()[y][x].setCell(null);
                    setX(x - movement);
                    w.getWorld()[y][x].setCell(this);
                    setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved left...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);                
                } else {
                    alive = false;
                    w.getWorld()[y][x].setDeadCell(this);
                    w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
                }
            } else {
                path.clear();
                randomStep(w);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            w.getWorld()[y][0].setCell(null);
            setX(w.getWidth() - 1);
            w.getWorld()[y][x].setCell(this);
            setEnergy(energy - (movement * efficiency));
            eat(w);
        }
    }

    /**
     *
     * @param w
     */
    public void eat(World w) {
        if (w.getWorld()[y][x].getSugar().getAmount() > 0) {
            setEnergy(energy + w.getWorld()[y][x].getSugar().getAmount());
            w.getWorld()[y][x].getSugar().setAmount(0);
            path.clear();
            targetFood = null;
            //            System.out.println(geneCode + "   ate on " + x + "," + y + ": energy +" + w.getWorld()[y][x].getSugar().getAmount());

        }
    }

    /**
     *
     * @param w
     * @return
     */
    public int[] findFreeTile(World w) {
        int loc[] = new int[2];
        int rx = 0;
        int ry = 0;
        boolean found = false;
        while (!found) {
            rx = new Random().nextInt((((x + vision) - (x - vision)))) + (x - vision);
            ry = new Random().nextInt((((y + vision) - (y - vision)))) + (y - vision);
            if (!(ry < 0 || rx < 0 || ry >= w.getHeight() || rx >= w.getWidth())) {
                if (w.getWorld()[ry][rx].getCell() == null && w.getWorld()[ry][rx].getDeadCell() == null) {
                    loc[0] = ry;
                    loc[1] = rx;
                    found = true;
                }
            }
        }
        return loc;
    }

    public void evolve() {
        switch (new Random().nextInt(4)) {
            case 0:
//                    System.out.println(getGeneCode() + " MUTATION! +1 vision");
                mutateVision();
                break;
            case 1:
//                    System.out.println(getGeneCode() + " MUTATION! +0.01 efficiency");
                mutateEfficiency();
                break;
            case 2:
                mutateSpeed();
                break;
            case 3:
                mutateTrailSize();
                break;
        }
    }

    private void mutateVision() {
        vision++;
    }

    private void mutateEfficiency() {
        efficiency = efficiency * 0.95;
    }

    private void mutateSpeed() {
        speed = speed + 0.25;
    }

    private void mutateTrailSize() {
        if ((trailSize - 2) > 0) {
            trailSize = trailSize - 2;
        }
    }

    /**
     *
     * @return
     */
    public String getGeneCode() {
        return geneCode;
    }

    /**
     *
     * @param geneCode
     */
    public void setGeneCode(String geneCode) {
        this.geneCode = geneCode;
    }

    /**
     *
     * @return
     */
    public double getEnergy() {
        return energy;
    }

    /**
     *
     * @param energy
     */
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    /**
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     *
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @return
     */
    public int getVision() {
        return vision;
    }

    /**
     *
     * @param vision
     */
    public void setVision(int vision) {
        this.vision = vision;
    }

    /**
     *
     * @return
     */
    public double getEfficiency() {
        return efficiency;
    }

    /**
     *
     * @param efficiency
     */
    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    /**
     *
     * @return
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     *
     * @param alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     *
     * @return
     */
    public int[] getTargetFood() {
        return targetFood;
    }

    /**
     *
     * @param targetFood
     */
    public void setTargetFood(int[] targetFood) {
        this.targetFood = targetFood;
    }

    /**
     *
     * @return
     */
    public String getColor() {
        return color;
    }

    /**
     *
     * @return
     */
    public int getOffspring() {
        return offspring;
    }

    /**
     *
     * @param offspring
     */
    public void setOffspring(int offspring) {
        this.offspring = offspring;
    }

    /**
     *
     * @return
     */
    public Queue<Integer> getPath() {
        return path;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

}
