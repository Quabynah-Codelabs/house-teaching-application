package io.codelabs.digitutor.core.base;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Base class for all {@link android.app.Activity}s
 */
public abstract class BaseActivity extends AppCompatActivity {


    /**
     * navigate to other activities
     *
     * @param target
     */
    public void intentTo(Class<? extends Activity> target) {
        startActivity(new Intent(getApplicationContext(), target));
    }

    public void intentTo(Class<? extends Activity> target, boolean finish) {
        startActivity(new Intent(getApplicationContext(), target));
        if (finish) finishAfterTransition();
    }
}
