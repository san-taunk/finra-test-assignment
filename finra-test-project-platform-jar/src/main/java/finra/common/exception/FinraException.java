package finra.common.exception;

import org.springframework.http.HttpStatus;

public class FinraException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5158903790768302682L;

    public static class FinraWebScriptException extends BaseRuntimeException {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 8073312251114248434L;

        /**
         * Instantiates a new evidence file does not exists exception.
         *
         * @param statusText
         *            the status text
         */
        public FinraWebScriptException(HttpStatus statusCode, String statusText) {
            super(statusCode, statusText);
        }

        public FinraWebScriptException(HttpStatus statusCode, String statusText, Throwable throwable) {
            super(statusCode, statusText, throwable);
        }
    }

}
