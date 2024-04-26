package AdventureModel;

import java.io.*;
import java.util.*;

/**
 * Class AdventureGame.  Handles all the necessary tasks to run the Adventure game.
 */
public class AdventureGame implements Serializable {
    /**
     * Store the Introductory text of the game.
     */
    private final String directoryName;

    /**
     * Store the Help text of the game.
     */
    private String helpText;

    /**
     * A list of all the rooms in the game.
     */
    private final HashMap<Integer, Room> rooms;

    /**
     * A HashMap to store synonyms of commands.
     */
    private HashMap<String,String> synonyms = new HashMap<>();

    /**
     * List of action verbs (other than motions) that exist in all games.
     */
    private final String[] actionVerbs = {"QUIT","INVENTORY","TAKE","DROP"};

    /**
     * The Player of the game.
     */
    public Player player;

    /**
     * Adventure Game Constructor
     *
     * initializes attributes
     *
     * @param name the name of the adventure
     */
    public AdventureGame(String name){
        this.synonyms = new HashMap<>();
        this.rooms = new HashMap<>();
        this.directoryName = "Games/" + name; //all games files are in the Games directory!
        try {
            setUpGame();
        } catch (IOException e) {
            throw new RuntimeException("An Error Occurred: " + e.getMessage());
        }
    }

    /**
     * Save the current state of the game to a file
     *
     * @param file pointer to file to write to
     */
    public void saveModel(File file) {
        try {
            FileOutputStream outfile = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(outfile);
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * setUpGame
     *
     * @throws IOException in the case of a file I/O error
     */
    public void setUpGame() throws IOException {

        String directoryName = this.directoryName;
        AdventureLoader loader = new AdventureLoader(this, directoryName);
        loader.loadGame();

        // set up the player's current location
        this.player = new Player(this.rooms.get(1));
    }

    /**
     * tokenize
     *
     * @param input string from the command line
     * @return a string array of tokens that represents the command.
     */
    public String[] tokenize(String input){

        input = input.toUpperCase();
        String[] commandArray = input.split(" ");

        int i = 0;
        while (i < commandArray.length) {
            if(this.synonyms.containsKey(commandArray[i])){
                commandArray[i] = this.synonyms.get(commandArray[i]);
            }
            i++;
        }
        return commandArray;

    }

    /**
     * movePlayer
     *
     * moves the player in the given direction, if possible.
     *
     * @param direction the move command
     * @return false, if move results in death or a win (and game is over).  Else, true.
     */
    public boolean movePlayer(String direction) {

        direction = direction.toUpperCase();
        PassageTable motionTable = this.player.getCurrentRoom().getMotionTable(); //where can we move?
        if (!motionTable.optionExists(direction)) return true; //no move

        ArrayList<Passage> possibilities = new ArrayList<>();
        for (Passage entry : motionTable.getDirection()) {
            if (entry.getDirection().equals(direction)) { //this is the right direction
                possibilities.add(entry); // are there possibilities?
            }
        }

        //try the blocked passages first
        Passage chosen = null;
        for (Passage entry : possibilities) {
            System.out.println(entry.getIsBlocked());
            System.out.println(entry.getKeyName());

            if (chosen == null && entry.getIsBlocked()) {
                if (this.player.getInventory().contains(entry.getKeyName())) {
                    chosen = entry; //we can make it through, given our stuff
                    break;
                }
            } else { chosen = entry; } //the passage is unlocked
        }

        if (chosen == null) return true; //doh, we just can't move.

        int roomNumber = chosen.getDestinationRoom();
        if (roomNumber == 0){
            return false;
        }
        Room room = this.rooms.get(roomNumber);
        this.player.setCurrentRoom(room);

        if (roomNumber == 50){
            return false;
        }

        return !this.player.getCurrentRoom().getMotionTable().getDirection().get(0).getDirection().equals("FORCED");
    }

    /**
     * interpretAction
     * interprets the user's action.
     *
     * @param command String representation of the command.
     * @return the result of the command
     */
    public String interpretAction(String command){

        String[] inputArray = tokenize(command); //look up synonyms

        PassageTable motionTable = this.player.getCurrentRoom().getMotionTable(); //where can we move?
        int roomNumber = 99999;

        if (motionTable.optionExists(inputArray[0])) {
            if (!movePlayer(inputArray[0])) {
                command = command.toUpperCase();
                for (Passage entry : motionTable.getDirection()) {
                    if (entry.getDirection().equals(command)) {
                        roomNumber = entry.getDestinationRoom();
                    }
                }

                if (roomNumber == 0){
                    return "EXIT";
                } else if (this.player.getCurrentRoom().getRoomNumber() == 50) {
                    return "ENDING";
                } else if (this.player.getCurrentRoom().getMotionTable().getDirection().get(0).getDestinationRoom() == 0)
                    return "GAME OVER";
                else return "FORCED";
            } //something is up here! We are dead or we won.
            return null;
        } else if(Arrays.asList(this.actionVerbs).contains(inputArray[0])) {
            if(inputArray[0].equals("QUIT")) { return "GAME OVER"; } //time to stop!
            else if(inputArray[0].equals("INVENTORY") && this.player.getInventory().size() == 0) return "INVENTORY IS EMPTY";
            else if(inputArray[0].equals("INVENTORY") && this.player.getInventory().size() > 0) return "THESE OBJECTS ARE IN YOUR INVENTORY:\n" + this.player.getInventory().toString();
            else if(inputArray[0].equals("TAKE") && inputArray.length < 2) return "THE TAKE COMMAND REQUIRES AN OBJECT";
            else if(inputArray[0].equals("DROP") && inputArray.length < 2) return "THE DROP COMMAND REQUIRES AN OBJECT";
            else if(inputArray[0].equals("TAKE") && inputArray.length == 2) {
                if(this.player.getCurrentRoom().checkIfObjectInRoom(inputArray[1])) {
                    this.player.takeObject(inputArray[1]);
                    return "YOU HAVE TAKEN:\n " + inputArray[1];
                } else {
                    return "THIS OBJECT IS NOT HERE:\n " + inputArray[1];
                }
            }
            else if(inputArray[0].equals("DROP") && inputArray.length == 2) {
                if(this.player.checkIfObjectInInventory(inputArray[1])) {
                    this.player.dropObject(inputArray[1]);
                    return "YOU HAVE DROPPED:\n " + inputArray[1];
                } else {
                    return "THIS OBJECT IS NOT IN YOUR INVENTORY:\n " + inputArray[1];
                }
            }
        }
        return "INVALID COMMAND.";
    }

    /**
     * Get the name of this directory
     *
     * @return directoryName
     */
    public String getDirectoryName() {
        return this.directoryName;
    }

    /**
     * Get the instructions
     *
     * @return helpText
     */
    public String getInstructions() {
        return helpText;
    }

    /**
     * Gets the player
     *
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the rooms
     *
     * @return map of key value pairs (integer to room)
     */
    public HashMap<Integer, Room> getRooms() {
        return this.rooms;
    }

    /**
     * Gets the synonyms
     *
     * @return map of key value pairs (synonym to command)
     */
    public HashMap<String, String> getSynonyms() {
        return this.synonyms;
    }

    /**
     * Sets the helpText
     *
     * @param help which is text to set
     */
    public void setHelpText(String help) {
        this.helpText = help;
    }


}
