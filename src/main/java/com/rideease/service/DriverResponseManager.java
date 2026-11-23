package com.rideease.service;

import com.rideease.dao.DriverDAO;
import com.rideease.model.Driver;

/**
 * DriverResponseManager — the production DriverResponder for RideEase.
 */
public class DriverResponseManager implements DriverResponder {

    private final DriverResponder delegate;   // the real answer source
    private final DriverDAO driverDAO;        // optional: persist online/offline

    /** runtime-only mode: online changes are not persisted */
    public DriverResponseManager(DriverResponder delegate) {
        this(delegate, null);
    }

    /** full mode: online changes are saved to DRIVERS via updateAvailability */
    public DriverResponseManager(DriverResponder delegate, DriverDAO driverDAO) {
        this.delegate = delegate;
        this.driverDAO = driverDAO;
    }

    /**
     * true = this driver accepts the booking (and is marked busy at once).
     * AssignmentRetryManager calls this for every candidate, in pool order.
     */
    @Override
    public boolean accepts(Driver driver) {
        if (driver == null || delegate == null) {
            return false;                      // EDGE: nothing to work with
        }
        if (!driver.isOnline()) {
            return false;                      // offline drivers are never asked
        }
        if (driver.isBusy()) {
            return false;                      // one active ride per driver
        }
        if (!delegate.accepts(driver)) {
            return false;                      // driver said no — stays free
        }
        driver.setBusy(true);                  // accepted: busy from this moment
        return true;
    }

    /** ride finished: free the driver for the next booking */
    public boolean completeRide(Driver driver) {
        if (driver == null || !driver.isBusy()) {
            return false;                      // EDGE: nothing to complete
        }
        driver.setBusy(false);
        return true;
    }

    /**
     * Take a driver offline / back online. Persisted to DRIVERS when a
     * DriverDAO is configured; runtime-only otherwise.
     */
    public boolean setOnline(Driver driver, boolean online) {
        if (driver == null) {
            return false;
        }
        driver.setOnline(online);
        if (driverDAO != null) {
            return driverDAO.updateAvailability(driver.getDriverId(), online);
        }
        return true;
    }
}