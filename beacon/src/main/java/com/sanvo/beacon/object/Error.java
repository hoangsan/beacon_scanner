package com.sanvo.beacon.object;

/*
  Created by San Vo on 12/12/2017.
 */

/**
 * A type representing an error value that can be thrown.
 */
public class Error {
    private String _errorMessage;

    public Error(String errorMessage) {
        _errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return _errorMessage;
    }
}
