package semaphore.com.semaphorebetterorworse;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

/**
 * Created by ziggypop on 3/5/16.
 *
 */
public class MyKeyboardView extends KeyboardView {
    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setKeyboard(Keyboard keyboard){
        // IMPLEMENT ME

    }

}
