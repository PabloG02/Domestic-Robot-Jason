import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.Location;
import java.util.logging.Logger;

public class HouseEnv extends Environment {

    // common literals
    public static final Literal of  = Literal.parseLiteral("open(fridge)");
    public static final Literal clf = Literal.parseLiteral("close(fridge)");
    public static final Literal gb  = Literal.parseLiteral("get(beer)");
    public static final Literal hb  = Literal.parseLiteral("hand_in(beer)");
    public static final Literal sb  = Literal.parseLiteral("sip(beer)");
    public static final Literal hob = Literal.parseLiteral("has(owner,beer)");

    public static final Literal af = Literal.parseLiteral("at(robot,fridge)");
    public static final Literal ao = Literal.parseLiteral("at(robot,owner)");
    public static final Literal ap = Literal.parseLiteral("at(robot,pickup)");
    public static final Literal ab = Literal.parseLiteral("at(robot,base)");

    static Logger logger = Logger.getLogger(HouseEnv.class.getName());

    HouseModel model; // the model of the grid

    @Override
    public void init(String[] args) {
        model = new HouseModel();

        if (args.length == 1 && args[0].equals("gui")) {
            HouseView view  = new HouseView(model);
            model.setView(view);
        }

        updatePercepts();
    }

    /** creates the agents percepts based on the HouseModel */
    void updatePercepts() {
        // clear the percepts of the agents
        clearPercepts("robot");
        clearPercepts("owner");

        // get the robot location
        Location lRobot = model.getAgPos(0);

        // add agent location to its percepts
        if (lRobot.equals(model.nearFridge[0])) {
            addPercept("robot", af);
        }
        if (lRobot.equals(model.nearOwner[0])) {
            addPercept("robot", ao);
        }
        if (lRobot.equals(model.lPickUp)) {
            addPercept("robot", ap);
        }
        if (lRobot.equals(model.lBaseRobot)) {
            addPercept("robot", ab);
        }

        // add beer "status" the percepts
        if (model.fridgeOpen) {
            addPercept("robot", Literal.parseLiteral("stock(beer,"+model.availableBeers+")"));
        }

        if (model.sipCount > 0) {
            addPercept("robot", hob);
            addPercept("owner", hob);
        }
    }


    @Override
    public boolean executeAction(String ag, Structure action) {
        System.out.println("["+ag+"] doing: "+action);
        boolean result = false;
        if (action.equals(of) && ag.equals("robot")) { // of = open(fridge)
            result = model.openFridge();

        } else if (action.equals(clf) && ag.equals("robot")) { // clf = close(fridge)
            result = model.closeFridge();

        } else if (action.getFunctor().equals("move_towards") && ag.equals("robot")) {
            String l = action.getTerm(0).toString();
            Location dest = null;
            boolean adjacent = false;
            if (l.equals("fridge")) {
                dest = model.lFridge;
                adjacent = true;
            } else if (l.equals("owner")) {
                dest = model.lOwner;
                adjacent = true;
            } else if (l.equals("pickup")) {
                dest = model.lPickUp;
            } else if (l.equals("base")) {
                dest = model.lBaseRobot;
            }

            try {
                result = adjacent? model.moveTowardsAdjacent(dest) : model.moveTowards(dest);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (action.equals(gb) && ( ag.equals("robot") || ag.equals("robot") )) {
            result = model.getBeer();

        } else if (action.equals(hb) && ag.equals("robot")) {
            result = model.handInBeer();

        } else if (action.equals(sb) && ag.equals("owner")) {
            result = model.sipBeer();

        } else if (action.getFunctor().equals("deliver") && ag.equals("supermarket")) {
            // wait 4 seconds to finish "deliver"
            System.out.println("Acción:  " + action);
            try {
                Thread.sleep(4000);
                model.deliveredBeers = (int)((NumberTerm)action.getTerm(1)).solve();
                result = true;
            } catch (Exception e) {
                logger.info("Failed to execute action deliver!"+e);
            }

        } else {
            logger.info("Failed to execute action "+action);
        }

        if (result) {
            updatePercepts();
            try {
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        return result;
    }
}
