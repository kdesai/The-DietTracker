package diet.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DietServiceException extends Exception implements Serializable {
    public DietServiceException() {
        super();
    }
    
    public DietServiceException(String message) {
        super(message);
    }
    
    public DietServiceException(String message, Throwable th) {
        super(message, th);
    }
}