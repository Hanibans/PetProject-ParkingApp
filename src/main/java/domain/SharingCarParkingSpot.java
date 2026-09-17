package domain;

public class SharingCarParkingSpot extends Parking {

    public SharingCarParkingSpot(String label, Zone zone) {
        super(label, zone);
    }

    @Override
    public String getSpotTypes() {
        return "Sharing Car";
    }


}