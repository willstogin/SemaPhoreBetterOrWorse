package semaphore.com.semaphorebetterorworse;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.util.ArrayList;


/**
 * Created by ziggypop on 3/5/16.
 *
 */
public class MyInputMethodService extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private BandDataHandler dataHandler;

    private View mainView;

    @Override
    public View onCreateInputView(){
        //implement mev
        mainView = getLayoutInflater().inflate(R.layout.dumb_keyboard, null);
//        setVisualizer(BandDataHandler.BandPosition.Left, BandDataHandler.BandPosition.Right);
        new BluetoothTask().execute();
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

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean caps = false;


    public void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
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
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
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
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }

    public void commitText(){
        InputConnection ic = getCurrentInputConnection();
//        ic.commitText()
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

    public void setVisualizer(BandDataHandler.BandPosition left, BandDataHandler.BandPosition right){
        ArrayList<BandDataHandler.BandPosition> positions = new ArrayList<>();
        positions.add(left);
        positions.add(right);

        View l = mainView.findViewById(R.id.left_mask);
        View r = mainView.findViewById(R.id.right_mask);
        View tr = mainView.findViewById(R.id.top_right_mask);
        View tl = mainView.findViewById(R.id.top_left_mask);
        View t = mainView.findViewById(R.id.top_mask);
        View b = mainView.findViewById(R.id.bottom_mask);
        View bl = mainView.findViewById(R.id.bottom_left_mask);
        View br = mainView.findViewById(R.id.bottom_right_mask);

        if (positions.contains(BandDataHandler.BandPosition.Left)){
            l.setVisibility(View.GONE);
        } else l.setVisibility(View.VISIBLE);
        if (positions.contains(BandDataHandler.BandPosition.Right)){
            r.setVisibility(View.GONE);
        } else r.setVisibility(View.VISIBLE);
        if (positions.contains(BandDataHandler.BandPosition.Top)){
            t.setVisibility(View.GONE);
        } else t.setVisibility(View.VISIBLE);
        if (positions.contains(BandDataHandler.BandPosition.Bottom)){
            b.setVisibility(View.GONE);
        } else b.setVisibility(View.VISIBLE);
        if (positions.contains(BandDataHandler.BandPosition.TopRight)){
            tr.setVisibility(View.GONE);
        } else tr.setVisibility(View.VISIBLE);
        if (positions.contains(BandDataHandler.BandPosition.LeftTop)){
            tl.setVisibility(View.GONE);
        } else tl.setVisibility(View.VISIBLE);
        if (positions.contains(BandDataHandler.BandPosition.LeftBottom)){
            bl.setVisibility(View.GONE);
        } else bl.setVisibility(View.VISIBLE);
        if (positions.contains(BandDataHandler.BandPosition.RightBottom)){
            br.setVisibility(View.GONE);
        } else br.setVisibility(View.VISIBLE);
    }


}
