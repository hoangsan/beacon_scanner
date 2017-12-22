package com.sanvo.beacon.object;

/*
  Created by San Vo on 12/12/2017.
 */

/**
 * An area that can be monitored.
 */
public abstract class Region {
    //public enum CLRegionState {
    //    unknown,inside,outside
    //}

    protected String _identifier;
    //protected boolean _notifyOnEntry;
    //protected boolean _notifyOnExit;

    /**
     * Constructor with identifier.
     *
     * @param identifier The identifier for the region object.
     */
    public Region(String identifier) {
        _identifier = identifier;
    }

    /**
     * Get identifier for the region object.
     *
     * @return The identifier for the region object.
     */
    public String getIdentifier() {
        return _identifier;
    }
}
