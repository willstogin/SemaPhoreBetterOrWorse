package semaphore.com.semaphorebetterorworse;

import android.content.Context;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.util.EventListener;

/**
 * Created by William on 3/5/2016
 */
public class BandDataHandler {
    private BandInfo[] pairedBands;
    private final String TAG = "BandDataHandler";
    private Context context;


    // Band abstractions
    public BandAbstraction leftBand = new BandAbstraction();
    public BandAbstraction rightBand = new BandAbstraction();

    public BandDataHandler(Context context) {
        super();
        this.context = context;

        // Init, get the list of connected mBands
        pairedBands = BandClientManager.getInstance().getPairedBands();
        printPairedBands();
        connectPairedBands();
        Log.v(TAG, "Connecting to paired bands");


    }

    private void connectPairedBands() {
        BandClient bandClient;
        for (BandInfo band : pairedBands) {
            bandClient = BandClientManager.getInstance().create(context,
                    band);
            BandPendingResult<ConnectionState> pendingResult =
                    bandClient.connect();
            try {
                ConnectionState state = pendingResult.await();
                if (state == ConnectionState.CONNECTED) {
                    // do work on success
                    // Add to whatever abstraction is not connected
                    BandAbstraction abstraction;
                    if (!(leftBand.connected) ||
                            rightBand.connected) {
                        // Left is not connected or both are connected, assign to left
                        abstraction = leftBand;
                    } else {
                        abstraction = rightBand;
                    }

                    abstraction.connected = true;
                    // Register accelerometer listener
                    try {
                        bandClient.getSensorManager().registerAccelerometerEventListener(
                                new BandAccelerometerEventListenerCustom(abstraction), SampleRate.MS128
                        );
                    } catch (BandIOException e) {
                        Log.e(TAG, "Failed to register listener");
                        e.printStackTrace();
                    }

                } else {
                    // do work on failure
                    Log.e(TAG, "Failed to bind to " + band.getMacAddress());
                }
            } catch (InterruptedException ex) {
                // handle InterruptedException
                ex.printStackTrace();
            } catch (BandException ex) {
                // handle BandException
                ex.printStackTrace();
            }
        }
    }


    private void printPairedBands() {
        for (BandInfo info : pairedBands) {
            Log.v(TAG, "Paired band detected: " + info.getName());
        }

    }

    private class BandAccelerometerEventListenerCustom implements BandAccelerometerEventListener, EventListener {
        private BandAbstraction band;
        public BandAccelerometerEventListenerCustom(BandAbstraction band) {
            super();
            this.band = band;
        }

        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent event) {
            // TODO Handle event
            Log.v(TAG, "Received accelerometer event");
            Log.v(TAG, "X: " + Float.toString(event.getAccelerationX()));
            Log.v(TAG, "Y: " + Float.toString(event.getAccelerationY()));
            Log.v(TAG, "Z: " + Float.toString(event.getAccelerationZ()));

            // Update the band abstraction
            band.accX = event.getAccelerationX();
            band.accY = event.getAccelerationY();
            band.accZ = event.getAccelerationZ();
        }
    }

    private class BandAbstraction {
        public boolean connected;
        public float accX;
        public float accY;
        public float accZ;

        public BandAbstraction() {
            connected = false;
            accX = 0;
            accY = 0;
            accZ = 0;
        }
    }

}
