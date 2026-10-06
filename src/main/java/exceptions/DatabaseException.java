package exceptions;

import java.sql.SQLException;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String errorMessage, SQLException exception)
    {
        super(errorMessage, exception);
    }

}
