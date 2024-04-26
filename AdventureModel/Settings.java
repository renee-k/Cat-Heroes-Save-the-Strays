package AdventureModel;

import java.util.HashMap;

/**
 * Settings Class
 */
public class Settings {
    /**
     * settings
     */
    public static Settings settings = null;
    private boolean tts = true;

    /**
     * Constructor for Settings
     */
    public Settings() {
    }

    /**
     * Creates only one setting and returns the instance of the settings when called.
     * @return settings
     */
    public static Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    /**
     * turns on the text to speech function.
     */
    public void turnOnTextToSpeech(){
        tts = true;
    }


    /**
     * turns off the text to speech function.
     */
    public void turnOffTextToSpeech(){
        tts = false;
    }

    /**
     * Gets whether text to speech is turned on or off.
     *
     * @return true if text to speech is on, false otherwise.
     */
    public boolean getTextToSpeech(){
        return this.tts;
    }
}
