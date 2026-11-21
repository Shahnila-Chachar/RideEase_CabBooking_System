package com.rideease.service;

import com.rideease.model.Driver;

/**
 * DriverResponder — answers "does this driver accept the ride?".
 */
public interface DriverResponder {

    boolean accepts(Driver driver);
}