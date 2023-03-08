import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

/** class that implements the Model of Domestic Robot application */
public class HouseModel extends GridWorldModel {

    // constants for the grid objects
    public static final int FRIDGE = 16;
    public static final int OWNER  = 32;
    public static final int PICKUP = 64;

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
    Location lBaseRobot = new Location(GSize/2, GSize/2);

    Location[] nearFridge = {new Location(0,1), new Location(1,0)};
    Location[] nearOwner  = {new Location(GSize-1,GSize-2), new Location(GSize-2,GSize-1)};

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
        }
        return true;
    }

    boolean moveTowardsAdjacent(Location dest) {
        Location r1 = getAgPos(0);
        if (r1.x < dest.x)        r1.x++;
        else if (r1.x > dest.x)   r1.x--;
        if (r1.y < dest.y-1)      r1.y++;
        else if (r1.y > dest.y+1) r1.y--;
        setAgPos(0, r1); // move the robot in the grid

        // repaint the fridge and owner locations
        if (view != null) {
            view.update(lFridge.x,lFridge.y);
            view.update(lOwner.x,lOwner.y);
            view.update(lPickUp.x,lPickUp.y);
        }
        return true;
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
}
