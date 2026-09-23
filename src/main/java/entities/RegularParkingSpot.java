package domain;

public class RegularParkingSpot extends Parking {

    private double regularPrice;

    public RegularParkingSpot(String label, Zone zone) {
        super(label, zone);
    }

    @Override
    public String getSpotTypes() {
        return "Regular Car";
    }

    public double getRegularPrice() {
        return getPricePerHour();
    }

    @Override
    public String toString() {
        return "RegularParkingSpot: "+ regularPrice;
    }


}