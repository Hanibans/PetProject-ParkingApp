package entities;

public abstract class Parking {

    private String label;
    private Zone zone;
    private boolean available;

    public Parking(String label, Zone zone) {
        this.label = label;
        this.zone = zone;
        this.available = true;
    }

    public String getLabel() {
        return label;
    }

    public Zone getZone() {
        return zone;
    }

    public boolean isAvailable() {
        return available;
    }

    public void takeSpot() {
        available = false;
    }

    public void releaseSpot() {
        available = true;
    }

    public double getPricePerHour() {
        return zone.getPricePerHour();
    }

    public abstract String getSpotTypes();
}
