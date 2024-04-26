package Test;

import java.io.IOException;
import AdventureModel.AdventureGame;
import AdventureModel.Settings;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import AdventureModel.Settings.*;

/**
 * Test to see if the settings still function.
 */
public class SettingsTest {
    /**
     * Constructor
     */
    public SettingsTest() {
    }

    @Test
    void getSettingsOnceTest(){
        Settings a = Settings.getSettings();
        Settings b = Settings.getSettings();
        assertEquals(a, b);
    }

    @Test
    void getTtsGetter(){
        boolean a = Settings.getSettings().getTextToSpeech();
        assertTrue(a);
    }
}