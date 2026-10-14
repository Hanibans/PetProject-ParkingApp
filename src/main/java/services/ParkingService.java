package services;

import entities.Parking;
import entities.Zone;
import persistence.ConnectionPool;
import persistence.ParkingMapper;

import java.util.List;

public class ParkingService {

    private final ParkingMapper parkingMapper;
    private ConnectionPool connectionPool;

    public ParkingService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.parkingMapper = new ParkingMapper(connectionPool);
    }


}
