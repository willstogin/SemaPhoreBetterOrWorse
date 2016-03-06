package semaphore.com.semaphorebetterorworse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.HorizontalAlignment;
import com.microsoft.band.tiles.pages.Margins;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.PageRect;
import com.microsoft.band.tiles.pages.ScrollFlowPanel;
import com.microsoft.band.tiles.pages.VerticalAlignment;
import com.microsoft.band.tiles.pages.WrappedTextBlock;
import com.microsoft.band.tiles.pages.WrappedTextBlockData;
import com.microsoft.band.tiles.pages.WrappedTextBlockFont;

import java.util.EventListener;
import java.util.UUID;

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
                        // TODO Create notification to this band saying "this should be left"
//                        sendBandUpdate(bandClient, "THIS SHOULD BE ON YOUR INNER LEFT WRIST");

                    } else {
                        abstraction = rightBand;
                        // TODO Create notification to this band saying "this should be right"
//                        sendBandUpdate(bandClient, "THIS SHOULD BE ON YOUR INNER RIGHT WRIST");
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


        //TODO implement this
        public BandPosition determinePosition(float accX, float accY, float acZ){
            if (accX > .75) { // Always top
                return BandPosition.Top;
            } else if (accX < .75 && accX > .25) { // May be top left or top right
                return BandPosition.TopRight;
            } else if (accX < .25 && accX > -.25) { // May be left or right
                return BandPosition.Right;
            } else if (accX < -.25 && accX > -.75) { // May be bottom left or bottom right
                return BandPosition.RightBottom;
            } else { // Always bottom
                return BandPosition.Bottom;

            }
        }


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


    enum TileLayoutIndex {
        MessagesLayout
    }

    enum TileMessagesPageElementId {
        Message1,
        Message2
    }

    private void sendBandUpdate(BandClient client, String text) {
        ScrollFlowPanel panel = new ScrollFlowPanel(new PageRect(0, 0, 245, 102));
        panel.setFlowPanelOrientation(FlowPanelOrientation.VERTICAL);
        panel.setHorizontalAlignment(HorizontalAlignment.LEFT);
        panel.setVerticalAlignment(VerticalAlignment.TOP);

        WrappedTextBlock textBlock1 = new WrappedTextBlock(new PageRect(0, 0, 245, 102),
                WrappedTextBlockFont.MEDIUM);
        textBlock1.setId(1);//.ordinal());
        textBlock1.setMargins(new Margins(15, 0, 15, 0));
        textBlock1.setColor(Color.WHITE);
        textBlock1.setAutoHeightEnabled(true);
        textBlock1.setHorizontalAlignment(HorizontalAlignment.CENTER);
        textBlock1.setVerticalAlignment(VerticalAlignment.CENTER);

        panel.addElements(textBlock1);

        PageLayout layout = new PageLayout(panel);
        Drawable myDrawable = context.getDrawable(R.drawable.circle);
        BandTile tile;
        UUID titleId = new UUID((long) 0, (long) 0);
        try {
            Bitmap mBitmap = ((BitmapDrawable) myDrawable).getBitmap();
            if (mBitmap != null) {
                tile = new BandTile.Builder(titleId, "MyTile", mBitmap).setPageLayouts(layout).build();
                if (!client.getTileManager().addTile(new Activity(), tile).await()) {
                    Log.e(TAG, "Failed to add tile");
                } else if (client.getTileManager().setPages(titleId,
                        new PageData(new UUID((long) 1, (long) 1),
                                TileLayoutIndex.MessagesLayout.ordinal())
                .update(new
                        WrappedTextBlockData(TileMessagesPageElementId.Message1.ordinal(),
                        "First Message"))
                ).await()) {
                    Log.e(TAG, "Failed to do something");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (BandIOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BandException e) {
            e.printStackTrace();
        }
    }

}
