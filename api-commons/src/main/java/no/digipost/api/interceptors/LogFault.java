package no.digipost.api.interceptors;

import org.slf4j.Logger;

public interface LogFault {
    public void log(Exception ex);

    public static class LogFaultsAsWarn implements LogFault {

        private Logger logger;

        public LogFaultsAsWarn(Logger logger) {
            this.logger = logger;
        }

        public void log(Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not handle request: " + ex.getMessage());
            }
        }
    }
}
