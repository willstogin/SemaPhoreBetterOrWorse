package semaphore.com.semaphorebetterorworse;

import android.content.Context;
import android.os.AsyncTask;
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
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.util.EventListener;

/**
 * Created by William on 3/5/2016.
 */
public class BandDataHandler extends AsyncTask<Void, Void, Void> {
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
                    // Register accelerometer listener
                    try {
                        bandClient.getSensorManager().registerAccelerometerEventListener(
                                new BandAccelerometerEventListenerCustom(), SampleRate.MS128
                        );
                        bandClient.getSensorManager().registerGyroscopeEventListener(
                                new CustomBandGyroEventListener(), SampleRate.MS128.MS128
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


    @Override
    protected Void doInBackground(Void... params) {


        return null;
    }


    private void printPairedBands() {
        for (BandInfo info : pairedBands) {
            Log.v(TAG, "Paired band detected: " + info.getName());
        }

    }

    private class BandAccelerometerEventListenerCustom implements BandAccelerometerEventListener, EventListener {

        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent event) {
            // TODO Handle event
            Log.v(TAG, "Received accelerometer event");
            Log.v(TAG, "X: " + Float.toString(event.getAccelerationX()));
            Log.v(TAG, "Y: " + Float.toString(event.getAccelerationY()));
            Log.v(TAG, "Z: " + Float.toString(event.getAccelerationZ()));
        }
    }


    private class CustomBandGyroEventListener implements BandGyroscopeEventListener, EventListener {


        @Override
        public void onBandGyroscopeChanged(BandGyroscopeEvent event) {
            // TODO Handle event
            Log.v(TAG, "Received Gyro event");
            Log.v(TAG, "X: " + Float.toString(event.getAccelerationX()));
            Log.v(TAG, "Y: " + Float.toString(event.getAccelerationY()));
            Log.v(TAG, "Z: " + Float.toString(event.getAccelerationZ()));
        }
    }

    private class BandAbstraction {
        public boolean connected;
        public float accX;
        public float accY;
        public float accZ;
        public float gyroX;
        public float gyroY;
        public float gyroZ;

        public BandAbstraction() {
            connected = false;
            accX = 0;
            accY = 0;
            accZ = 0;
            gyroX = 0;
            gyroY = 0;
            gyroZ = 0;
        }


        /**
         * coverage includes:
         * A B C D E F G H I J K L M N P Q R S T U V W X Y Z
         * @param leftPosition The position of the left band
         * @param rightPosition The position of the right band
         * @return The desired string
         */
        public String convertPositionsToLetter(BandPosition leftPosition, BandPosition rightPosition){
            switch (leftPosition){
                case Bottom:
                    return "";
                case LeftBottom:
                    switch (rightPosition){
                        case Bottom:
                            return "A";
                        case LeftTop:
                            return "I";
                        case Top:
                            return "K";
                        case TopRight:
                            return "L";
                        case Right:
                            return "M";
                        case RightBottom:
                            return "N";
                        default:
                            return "";
                    }
                case Left:
                    switch (rightPosition){
                        case Bottom:
                            return "B";
                        case LeftBottom:
                            return "H";
                        case LeftTop:
                            return "O";
                        case Top:
                            return "P";
                        case TopRight:
                            return "Q";
                        case Right:
                            return "R";
                        case RightBottom:
                            return "S";
                        default:
                            return "";
                    }
                case LeftTop:
                    switch (rightPosition){
                        case Bottom:
                            return "C";
                        case Top:
                            return "T";
                        case TopRight:
                            return "U";
                        case Right:
                            return "Y";
                        default:
                            return "";
                    }
                case Top:
                    switch (rightPosition){
                        case Bottom:
                            return "D";
                        case Right:
                            return "J";
                        case RightBottom:
                            return "V";
                        default:
                            return "";
                    }
                case TopRight:
                    switch (rightPosition){
                        case Bottom: // Im not sure about this one
                            return "E";
                        case Right:
                            return "W";
                        case RightBottom:
                            return "X";
                        default:
                            return "";
                    }
                case Right:
                    switch (rightPosition) {
                        case Bottom:
                            return "F";
                        case RightBottom:
                            return "Z";
                    }
                case RightBottom:
                    switch (rightPosition){
                        case Bottom:
                            return "G";
                    }


                default:
                    //TODO maybe throw an error :/
                    return "";
            }

        }



    }

    enum BandPosition {
        Bottom,
        LeftBottom,
        Left,
        LeftTop,
        Top,
        TopRight,
        Right,
        RightBottom
    }

}
