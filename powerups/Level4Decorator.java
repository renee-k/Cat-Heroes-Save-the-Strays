package powerups;

import AdventureModel.AdventureObject;
import AdventureModel.PassageTable;
import AdventureModel.Room;
import views.AdventureGameView;

/**
 * Level4Decorator class
 */
public class Level4Decorator extends GUIDecorator {
    /**
     * Level4Decorator Constructor
     *
     * @param g the GUISource being wrapped
     */
    public Level4Decorator(GUISource g) {super(g);}

    /**
     * Use power-up in level 4
     */
    @Override
    public void usePowerup()
    {
        AdventureGameView gui = (AdventureGameView) this.getSource();
        PassageTable motionTable = gui.getModel().getPlayer().getCurrentRoom().getMotionTable();

        if (motionTable.optionExists("HINT")) {
            if (gui.getModel().player.checkIfObjectInInventory("BOXKEY")) {
                if (gui.getModel().player.checkIfObjectInInventory("MAGNIFIER")) {
                    String res = gui.getModel().interpretAction("USE HINT");
                        gui.updateScene("the passcode starts with 229");
                        Room num = gui.getModel().player.getCurrentRoom();
                        gui.showHintInfo("The first 3 digits of the lock are 229");
                } else {
                    gui.updateScene("cannot get hint");
                }
            }else {
                gui.updateScene("cannot get hint");
            }
        }
    }
}
