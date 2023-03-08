import jason.environment.grid.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


/** class that implements the View of Domestic Robot application */
public class HouseView extends GridWorldView {

    HouseModel hmodel;

    public HouseView(HouseModel model) {
        super(model, "Domestic Robot", 700);
        hmodel = model;
        defaultFont = new Font("Arial", Font.BOLD, 16); // change default font
        setVisible(true);
        repaint();
    }

    /** draw application objects */
    @Override
    public void draw(Graphics g, int x, int y, int object) {
        Location lRobot = hmodel.getAgPos(0);
        super.drawAgent(g, x, y, Color.lightGray, -1);
        switch (object) {
        case HouseModel.FRIDGE:
            super.drawAgent(g, x, y, Color.cyan, -1);
            if (lRobot.equals(hmodel.nearFridge[0])) {
                super.drawAgent(g, x, y, new Color(207, 159, 255), -1);
            }
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, "Fridge ("+hmodel.availableBeers+")");
            break;
        case HouseModel.OWNER:
            super.drawAgent(g, x, y, Color.pink, -1);
            if (lRobot.equals(hmodel.nearOwner[0])) {
                super.drawAgent(g, x, y, new Color(207, 159, 255), -1);
            }
            String o = "Owner";
            if (hmodel.sipCount > 0) {
                o +=  " ("+hmodel.sipCount+")";
            }
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, o);
            break;
        case HouseModel.PICKUP:
            super.drawAgent(g, x, y, Color.green, -1);
            if (lRobot.equals(hmodel.lPickUp)) {
                super.drawAgent(g, x, y, new Color(207, 159, 255), -1);
            }
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, "Pick up");
            break;
        }
        repaint();
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        Location lRobot = hmodel.getAgPos(0);
        if (!lRobot.equals(hmodel.lOwner) && !lRobot.equals(hmodel.lFridge)) {
            c = Color.yellow;
            if (hmodel.carryingBeer) c = Color.orange;
            if (hmodel.deliveredBeers != 0) c = new Color(250, 125, 0);
            super.drawAgent(g, x, y, c, -1);
            g.setColor(Color.black);
            super.drawString(g, x, y, defaultFont, "Robot");
        }
    }
}
