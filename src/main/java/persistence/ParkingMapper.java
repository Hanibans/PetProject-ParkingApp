package persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParkingMapper {
    ConnectionPool connectionPool;
    private static final Logger logger =
            LoggerFactory.getLogger(ParkingMapper.class);

    public ParkingMapper(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }
}
