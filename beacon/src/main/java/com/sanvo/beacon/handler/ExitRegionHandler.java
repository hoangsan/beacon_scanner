package com.sanvo.beacon.handler;

/*
  Created by San Vo on 12/12/2017.
 */

class ExitRegionHandler implements Runnable {
    public interface ExitRegionHandlerDelegate {
        void onExitRegion(String uniqueKey);
    }

    private ExitRegionHandlerDelegate _delegate;
    private String _beaconUniqueKey;

    public ExitRegionHandler(ExitRegionHandlerDelegate delegate, String uniqueKey) {
        _delegate = delegate;
        _beaconUniqueKey = uniqueKey;
    }

    @Override
    public void run()
    {
        _delegate.onExitRegion(_beaconUniqueKey);
    }
}
