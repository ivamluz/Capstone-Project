package ivamluz.marvelshelf.ui.fragments.workers;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by iluz on 5/10/16.
 *
 * Reference: http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
 */
public class AbstractWorkerFragment extends Fragment {
    protected boolean mIsLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    public void load() {
        if (isLoading()) {
            String message = String.format("%s is already loading contents.", this.getClass().getSimpleName());
            throw new IllegalStateException(message);
        }
    }

    public boolean isLoading() {
        return mIsLoading;
    }
}
