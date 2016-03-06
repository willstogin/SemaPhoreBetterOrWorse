package semaphore.com.semaphorebetterorworse;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by ziggypop on 3/5/16.
 */
public class MyInputMethodService extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private static final String TAG = "InputMethodService";
    private BandDataHandler dataHandler;

    private View mainView;

    private static final int DELAY = 500;
    private static final int QUEUE_SIZE = 4;


    @Override
    public View onCreateInputView() {


        mainView = getLayoutInflater().inflate(R.layout.dumb_keyboard, null);
        new BluetoothTask().execute();

        commitCharacter("G");

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyQueue queue = new MyQueue(QUEUE_SIZE);
                Log.v(TAG, "Polling to update the visualizer and the input");
                while (true) {
                    if (dataHandler != null) {
                        setVisualizer(dataHandler.leftBand.position, dataHandler.rightBand.position);
                        final String character = dataHandler.convertPositionsToLetter(
                                dataHandler.leftBand.position,
                                dataHandler.rightBand.position);

                        setVisualizerCharcter(character);

                        queue.add(character);

                        if (queue.isHomogeneous()){
                            Log.v(TAG, "queue is homogeneous: \""+character+"\"");
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    commitCharacter(character);
                                }
                            });
                        }
                    }
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        return mainView;
    }

    private class BluetoothTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "BluetoothTask";

        @Override
        protected Void doInBackground(Void... params) {
            // Get bluetooth permission
            Log.v(TAG, "checking bluetooth");
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                Log.v(TAG, "Adapter exists");
                // Device does not support Bluetooth
                if (!mBluetoothAdapter.isEnabled()) {
                    Log.v(TAG, "Bluetooth is not enabled");
                    Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    btIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyInputMethodService.this.startActivity(btIntent);

                }
            }
            dataHandler = new BandDataHandler(MyInputMethodService.this);
            return null;
        }
    }


    private boolean caps = false;


    public void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }

    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
    }

    public void commitCharacter(String character) {
        try {
            InputConnection ic = getCurrentInputConnection();
            ic.commitText(character, 1);
            Log.v(TAG, "committing character");
        } catch (NullPointerException e) {
            Log.e(TAG, "HA! Can't fool us! we caught you");
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public void setVisualizer(BandDataHandler.BandPosition left, BandDataHandler.BandPosition right) {
        ArrayList<BandDataHandler.BandPosition> positionz = new ArrayList<>();
        positionz.add(left);
        positionz.add(right);

        final ArrayList positions = positionz;
        final View l = mainView.findViewById(R.id.left_mask);
        final View r = mainView.findViewById(R.id.right_mask);
        final View tr = mainView.findViewById(R.id.top_right_mask);
        final View tl = mainView.findViewById(R.id.top_left_mask);
        final View t = mainView.findViewById(R.id.top_mask);
        final View b = mainView.findViewById(R.id.bottom_mask);
        final View bl = mainView.findViewById(R.id.bottom_left_mask);
        final View br = mainView.findViewById(R.id.bottom_right_mask);


        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (positions.contains(BandDataHandler.BandPosition.Left)) {
                    l.setVisibility(View.GONE);
                } else l.setVisibility(View.VISIBLE);
                if (positions.contains(BandDataHandler.BandPosition.Right)) {
                    r.setVisibility(View.GONE);
                } else r.setVisibility(View.VISIBLE);
                if (positions.contains(BandDataHandler.BandPosition.Top)) {
                    t.setVisibility(View.GONE);
                } else t.setVisibility(View.VISIBLE);
                if (positions.contains(BandDataHandler.BandPosition.Bottom)) {
                    b.setVisibility(View.GONE);
                } else b.setVisibility(View.VISIBLE);
                if (positions.contains(BandDataHandler.BandPosition.TopRight)) {
                    tr.setVisibility(View.GONE);
                } else tr.setVisibility(View.VISIBLE);
                if (positions.contains(BandDataHandler.BandPosition.LeftTop)) {
                    tl.setVisibility(View.GONE);
                } else tl.setVisibility(View.VISIBLE);
                if (positions.contains(BandDataHandler.BandPosition.LeftBottom)) {
                    bl.setVisibility(View.GONE);
                } else bl.setVisibility(View.VISIBLE);
                if (positions.contains(BandDataHandler.BandPosition.RightBottom)) {
                    br.setVisibility(View.GONE);
                } else br.setVisibility(View.VISIBLE);


            }
        });
    }

    public void setVisualizerCharcter(final String character) {


        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView visualizedCharacter = (TextView) mainView.findViewById(R.id.sample_letter);
                visualizedCharacter.setText(character);
            }
        });
    }

}
