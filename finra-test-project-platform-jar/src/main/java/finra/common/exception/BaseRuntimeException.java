package finra.common.exception;

import org.springframework.http.HttpStatus;

/**
 * The Class BaseRuntimeException.
 *
 */
public class BaseRuntimeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5694923907456453239L;

    /** The status code. */
    private final HttpStatus statusCode;

    /** The status text. */
    private final String statusText;

    /** The inner exception. */
    private final Throwable innerException;

    /**
     * Instantiates a new base runtime exception.
     *
     * @param httpStatus
     *            the http status
     * @param statusText
     *            the status text
     * @param throwable
     *            the throwable
     */
    public BaseRuntimeException(HttpStatus httpStatus, String statusText, Throwable throwable) {
        this.statusCode = httpStatus;
        this.statusText = statusText;
        this.innerException = throwable;
    }

    /**
     * Instantiates a new base runtime exception.
     *
     * @param statusText
     *            the status text
     */
    public BaseRuntimeException(String statusText) {
        this(null, statusText, null);
    }

    /**
     * Instantiates a new base runtime exception.
     *
     * @param statusText
     *            the status text
     * @param innerException
     *            the inner exception
     */
    public BaseRuntimeException(String statusText, Exception innerException) {
        this(null, statusText, innerException);
    }

    /**
     * Instantiates a new base runtime exception.
     *
     * @param statusCode
     *            the status code
     * @param statusText
     *            the status text
     */
    public BaseRuntimeException(HttpStatus statusCode, String statusText) {
        this(statusCode, statusText, null);
    }

    /**
     * Gets the status code.
     *
     * @return the status code
     */
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return statusText;
    }

    /**
     * Gets the inner exception.
     *
     * @return the inner exception
     */
    public Throwable getInnerException() {
        return innerException;
    }

}
