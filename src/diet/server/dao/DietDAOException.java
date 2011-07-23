package diet.server.dao;

@SuppressWarnings("serial")
public class DietDAOException extends Error {
    public DietDAOException(String message) {
        super(message);
    }
    
    public DietDAOException(String message, Throwable th) {
        super(message, th);
    }
}