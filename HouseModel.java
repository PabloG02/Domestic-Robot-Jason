import java.util.Random;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;


enum DIRECTION {
    LEFT, RIGHT, DOWN, UP 
}

/** class that implements the Model of Domestic Robot application */
public class HouseModel extends GridWorldModel {

    // constants for the grid objects
    public static final int FRIDGE = 16;
    public static final int OWNER  = 32;
    public static final int PICKUP = 64;
    public static final int BIN    = 128;
    public static final int CAN    = 256;
    public static final int OBSTACLE = 512;

    // the grid size
    public static final int GSize = 7;

    boolean fridgeOpen   = false; // whether the fridge is open
    boolean carryingBeer = false; // whether the robot is carrying beer
    int sipCount        = 0; // how many sip the owner did
    int availableBeers  = 3; // how many beers are available
    int deliveredBeers  = 0;

    Location lFridge = new Location(0,0);
    Location lOwner  = new Location(GSize-1,GSize-1);
    Location lPickUp = new Location(0,GSize-1);
    Location lBin    = new Location(GSize-1,0);
    Location lBaseRobot = new Location(GSize/2, GSize/2);
    Location lCan;
    Location[] lObstacles = {new Location(1,0), new Location(1,2), new Location(6,5)};
    
    public HouseModel() {
        // create a 7x7 grid with one mobile agent
        super(GSize, GSize, 1);

        // initial location of robot (column 3, line 3)
        // ag code 0 means the robot
        setAgPos(0, lBaseRobot);

        // initial location of fridge and owner
        add(FRIDGE, lFridge);
        add(OWNER, lOwner);
        add(PICKUP, lPickUp);
        add(BIN, lBin);
        for (Location lObstacle: lObstacles) {
            add(OBSTACLE, lObstacle);
        }
    }

    boolean openFridge() {
        if (!fridgeOpen) {
            fridgeOpen = true;
            availableBeers += deliveredBeers;
            deliveredBeers = 0;
            return true;
        } else {
            return false;
        }
    }

    boolean closeFridge() {
        if (fridgeOpen) {
            fridgeOpen = false;
            return true;
        } else {
            return false;
        }
    }

    boolean moveTowards(Location dest) {
        Location r1 = getAgPos(0);
        if (r1.x < dest.x)        r1.x++;
        else if (r1.x > dest.x)   r1.x--;
        if (r1.y < dest.y)        r1.y++;
        else if (r1.y > dest.y)   r1.y--;
        setAgPos(0, r1); // move the robot in the grid

        // repaint the fridge and owner locations
        if (view != null) {
            view.update(lFridge.x,lFridge.y);
            view.update(lOwner.x,lOwner.y);
            view.update(lPickUp.x,lPickUp.y);
            view.update(lBin.x,lBin.y);
            if(lCan != null)
                view.update(lCan.x,lCan.y);
        }
        return true;
    }

    boolean moveTowardsAdjacent(Location dest) {
        Location r1 = getAgPos(0);
        boolean locNotValid = true;
        for(DIRECTION dir: DIRECTION.values()){
            switch (dir) {
                case RIGHT:
                    if (r1.x < dest.x)    r1.x++;
                    break;
                case LEFT:
                    if (r1.x > dest.x)    r1.x--;
                    break;
                case DOWN:
                    if (r1.y < dest.y-1)  r1.y++;
                    break;
                case UP:
                    if (r1.y > dest.y-1)  r1.y--;
                    break;
            }
            System.out.println("Trying X: " + r1.x + " Y: " + r1.y);
            
            if (validPosition(r1)) {
                locNotValid = false;
                break;
            } else {
                r1 = getAgPos(0);
            }
        }
        
        if(locNotValid){
            return false;
        }
        
        setAgPos(0, r1); // move the robot in the grid

        // repaint the fridge and owner locations
        if (view != null) {
            view.update(lFridge.x,lFridge.y);
            view.update(lOwner.x,lOwner.y);
            view.update(lPickUp.x,lPickUp.y);
            view.update(lBin.x,lBin.y);
            if(lCan != null)
                view.update(lCan.x,lCan.y);
        }
        return true;
    }

    private boolean validPosition (Location r1) {
        // If the position is going to change
        if(r1.equals(getAgPos(0))){
            return false;
        } else {
            /*boolean different = true;
            // Check if is different from the one of the obstacle
            for (Location lObstacle: lObstacles) {
                if(lObstacle.equals(r1))
                    different = false;
            }
            if (different){
                different = !(r1.equals(lFridge) || r1.equals(lOwner) || r1.equals(lPickUp) || r1.equals(lBin));
            }

            return different;*/
            return true;
        }
    }

    boolean getBeer() {
        if (fridgeOpen && availableBeers > 0 && !carryingBeer) {
            availableBeers--;
            carryingBeer = true;
            if (view != null)
                view.update(lFridge.x,lFridge.y);
            return true;
        } else {
            return false;
        }
    }

    boolean addBeer(int n) {
        availableBeers += n;
        if (view != null)
            view.update(lFridge.x,lFridge.y);
        return true;
    }

    boolean handInBeer() {
        if (carryingBeer) {
            sipCount = 10;
            carryingBeer = false;
            if (view != null)
                view.update(lOwner.x,lOwner.y);
            return true;
        } else {
            return false;
        }
    }

    boolean sipBeer() {
        if (sipCount > 0) {
            sipCount--;
            if (view != null)
                view.update(lOwner.x,lOwner.y);
            return true;
        } else {
            return false;
        }
    }

    boolean throwCan() {
        Random random = new Random();
        lCan = new Location(random.nextInt(GSize),random.nextInt(GSize));
        while(lCan.equals(lBaseRobot) || lCan.equals(lFridge) || lCan.equals(lFridge) || lCan.equals(lPickUp) || lCan.equals(lBin)){
            lCan = new Location(random.nextInt(GSize),random.nextInt(GSize));
        }

        add(CAN, lCan);

        return true;
    }

    boolean pickUpTrash() {
        remove(CAN, lCan);
        lCan = null;

        return true;
    }
}
