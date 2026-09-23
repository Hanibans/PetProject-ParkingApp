package domain;

public class ElCarParkingSpot extends Parking {

    public ElCarParkingSpot(String label, Zone zone) {
        super(label, zone);
    }

    @Override
    public String getSpotTypes() {
        return "El Car";
    }


}
