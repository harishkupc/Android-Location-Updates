package com.locationupdates.location;

import android.location.Location;

public interface LocationListener {

    void onLocationChanged(Location location);

    void locationUpdateStarted();

    void locationUpdateStopped();

}
