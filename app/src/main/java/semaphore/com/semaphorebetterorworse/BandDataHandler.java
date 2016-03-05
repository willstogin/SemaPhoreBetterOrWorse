package semaphore.com.semaphorebetterorworse;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandInfo;

/**
 * Created by William on 3/5/2016.
 */
public class BandDataHandler extends AsyncTask<Void,Void,Void> {
    private BandInfo[] pairedBands;
    private final String TAG = "BandDataHandler";
    @Override
    protected Void doInBackground(Void... params) {
        // Init, get the list of connected mBands
        pairedBands = BandClientManager.getInstance().getPairedBands();


        // This is a test




        return null;
    }


    private void printPairedBands() {
        for (BandInfo info :pairedBands) {
            Log.v(TAG, "Paired band detected: " + info.getName());
        }

    }




}
