package domain;

public class DisabledParkingSpot extends Parking {

    public DisabledParkingSpot(String label, Zone zone) {
        super(label, zone);
    }

    @Override
    public String getSpotTypes() {
        return "Disabled Car";
    }



}