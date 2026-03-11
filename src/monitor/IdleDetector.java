package monitor;

import java.awt.MouseInfo;
import java.awt.Point;

public class IdleDetector {

    private Point lastLocation;
    private long idleStartTime;

    public IdleDetector() {
        this.idleStartTime = System.currentTimeMillis();
        try {
            this.lastLocation = MouseInfo.getPointerInfo().getLocation();
        } catch (Exception e) {
            this.lastLocation = null;
        }
    }

    public long getIdleMinutes() {
        try {
            Point currentLocation = MouseInfo.getPointerInfo().getLocation();
            
            if (lastLocation != null && currentLocation.equals(lastLocation)) {
                return (System.currentTimeMillis() - idleStartTime) / (60 * 1000);
            } else {
                this.lastLocation = currentLocation;
                this.idleStartTime = System.currentTimeMillis();
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}