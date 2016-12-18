package com.goebl.david;

import javax.net.ssl.SSLException;
import java.net.SocketTimeoutException;

/**
 * Decides whether a request should be retried or not.
 * <br>
 * If you need other behaviour, just extend this class.
 *
 * @since 27.04.14.
 */
public class RetryManager {
    /** seconds to wait until next retry */
    protected static final int[] BACKOFF = { 1, 2, 4, 7, 12, 20, 30, 60, 120 };

    static final RetryManager DEFAULT = new RetryManager();

    /**
     * Indicates whether it would possibly make sense to retry the request.
     * @param response the outcome of the request
     * @return <tt>true</tt> only in case of temporary (server-side) errors where retrying the
     *         request could succeed.
     */
    public boolean isRetryUseful(Response response) {
        int statusCode = response.getStatusCode();
        return statusCode == 503 || statusCode == 504 || statusCode >= 520;
    }

    /**
     * Analyzes whether the cause of an exception is worth retrying the request.
     * <br>
     * This is not covering all situations and in case of doubt the exception is considered not
     * recoverable. @YOU: if you find a case where an exception is recoverable, create an issue!
     * @param webbException the exception to analyze
     * @return <tt>true</tt> if it makes sense for the request to be retried again.
     */
    public boolean isRecoverable(WebbException webbException) {
        Throwable cause = webbException.getCause();
        if (cause == null) {
            return false;
        }
        if (cause instanceof SSLException) {
            SSLException sslException = (SSLException) cause;
            if (sslException.toString().toLowerCase().contains("connection reset by peer")) {
                return true;
            }
        }
        if (cause instanceof SocketTimeoutException) {
            return true;
        }
        return false;
    }

    /**
     * Implementation for the actual wait (depends on the current retry sequence).
     * @param retry the retry sequence (starts with 0 for the first retry).
     */
    public void wait(int retry) {
        long sleepMillis = BACKOFF[Math.min(retry, BACKOFF.length - 1)] * 1000L;
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new WebbException(ie);
        }
    }
}
