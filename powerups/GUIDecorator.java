package powerups;

/**
 * GUIDecorator class
 */
public abstract class GUIDecorator implements GUISource {
    private GUISource gui;

    /**
     * GUIDecorator Constructor
     *
     * @param g the GUISource being wrapped
     */
    public GUIDecorator(GUISource g) {
        this.gui = g;
    }

    @Override
    public void usePowerup() {

    }

    /**
     * Get the wrapped gui source of this decorator.
     * @return GUISource
     */
    public GUISource getSource() {
        return this.gui;
    }
}
