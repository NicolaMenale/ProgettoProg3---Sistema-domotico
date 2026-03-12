package model;

public abstract class Sensor {
    protected String id;
    protected boolean active;
    protected String modeString = "OFFLINE"; // default collado

    public Sensor(String id) {
        this.id = id;
        this.active = false;
    }

    public void setModeString(String mode) {this.modeString = mode; }
    public String getModeString() { return modeString; }
    public String getId() { return id; }
    public boolean isActive() { return active; }
    public void activate() { active = true; }
    public void deactivate() { active = false; }
    public abstract void reset();
    public abstract String getStatistics();
}