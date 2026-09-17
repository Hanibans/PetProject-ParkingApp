package domain;

public class PrivateArea extends Parking {


    public PrivateArea(String label, Zone zone) {
        super(label, zone);
    }

    @Override
    public String getSpotTypes() {
        return "Private Area";
    }


}