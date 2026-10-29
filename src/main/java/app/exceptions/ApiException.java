package app.exceptions;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class ApiException extends RuntimeException {
    private final int statusCode;
    private static final Logger logger = LoggerFactory.getLogger(ApiException.class);

    public ApiException(int statusCode, String msg){
        super(msg);
        this.statusCode = statusCode;
        logger.error("StatusCode: {}, Message: {},",statusCode, msg);
    }

    public int getStatusCode() {
        return statusCode;
    }
}