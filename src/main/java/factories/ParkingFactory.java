package factories;

import entities.*;

public class ParkingFactory {

    public static Parking createParking(String type, String label, Zone zone) {

        switch (type.toLowerCase()) {

            case "disabled":
                return new DisabledParkingSpot(label, zone);

            case "el":
                return new ElCarParkingSpot(label, zone);

            case "regular":
                return new RegularParkingSpot(label, zone);

            case "sharing":
                return new SharingCarParkingSpot(label, zone);

            case "private":
                return new PrivateArea(label, zone);

            default:
                throw new IllegalArgumentException(
                        "Ukendt parkeringstype: " + type);
        }
    }
}
