package AdventureModel;

import java.io.Serializable; //you will need this to save the game!

/**
 * AdventureGameObject class keeps track of the props or the objects in the game.
 */
public class AdventureObject implements Serializable {
    /**
     * The name of the object.
     */
    private String objectName;

    /**
     * The description of the object.
     */
    private String description;

    /**
     * The location of the object.
     */
    private Room location = null;

    /**
     * Adventure Object Constructor
     *
     * Sets the name, description, and location of the object.
     *
     * @param name The name of the Object in the game.
     * @param description One line description of the Object.
     * @param location The location of the Object in the game.
     */
    public AdventureObject(String name, String description, Room location){
        this.objectName = name;
        this.description = description;
        this.location = location;
    }

    /**
     * Getter method for the name attribute.
     *
     * @return name of the object
     */
    public String getName(){
        return this.objectName;
    }

    /**
     * Getter method for the description attribute.
     *
     * @return description of the game
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * This method returns the location of the object if the object is still in
     * the room. If the object has been picked up by the player, it returns null.
     *
     * @return returns the location of the object if the objects is still in
     * the room otherwise, returns null.
     */
    public Room getLocation(){
        return this.location;
    }

}
