package ivamluz.marvelshelf.ui.fragments.workers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.karumi.marvelapiclient.ComicApiClient;
import com.karumi.marvelapiclient.MarvelApiException;
import com.karumi.marvelapiclient.model.ComicDto;
import com.karumi.marvelapiclient.model.ComicsDto;
import com.karumi.marvelapiclient.model.ComicsQuery;
import com.karumi.marvelapiclient.model.MarvelResponse;

import java.util.LinkedList;
import java.util.List;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/10/16.
 *
 * This Fragment manages a single background task responsible for loading comics asyncly and retains
 * itself across configuration changes.
 */
public class ComicsLoaderWorkerFragment extends AbstractWorkerFragment {
    public static final String TAG = ComicsLoaderWorkerFragment.class.getSimpleName();

    private static final String LOG_TAG = TAG;

    private static final String ARG_CHARACTER_ID = "ivamluz.marvelshelf.character_id";

    private long mCharacterId;

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TaskCallbacks {
        void onPreExecute();
        void onCancelled();
        void onComicsLoaded(List<ComicDto> comics);
    }

    private TaskCallbacks mCallbacks;
    private LoadComicsTask mTask;

    public static ComicsLoaderWorkerFragment newInstance(long characterId) {
        ComicsLoaderWorkerFragment fragment = new ComicsLoaderWorkerFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_CHARACTER_ID, characterId);

        fragment.setArguments(args);

        return fragment;
    }


    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (TaskCallbacks) context;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCharacterId = getArguments().getLong(ARG_CHARACTER_ID);
        }

        // Create and execute the background task.
        mTask = new LoadComicsTask();
        mTask.execute();
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * A dummy task that performs some (dumb) background work and
     * proxies progress updates and results back to the Activity.
     *
     * Note that we need to check if the callbacks are null in each
     * method in case they are invoked after the Activity's and
     * Fragment's onDestroy() method have been called.
     */
    private class LoadComicsTask extends AsyncTask<Void, Integer, List<ComicDto>> {

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected List<ComicDto> doInBackground(Void... ignore) {
            MarvelResponse<ComicsDto> response = null;
            ComicApiClient comicApiClient = new ComicApiClient(MarvelShelfApplication.getInstance().getMarvelApiConfig());
            ComicsQuery query = ComicsQuery.Builder.create().addCharacter((int)mCharacterId).withOffset(0).withLimit(100).build();
            try {
                response = comicApiClient.getAll(query);
            } catch (MarvelApiException e) {
                MarvelShelfLogger.error(LOG_TAG, e);
            }

            if (response != null) {
                return response.getResponse().getComics();
            } else {
                MarvelShelfLogger.debug(LOG_TAG, "response is null");
                return new LinkedList<>();
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(List<ComicDto> comics) {
            if (mCallbacks != null) {
                mCallbacks.onComicsLoaded(comics);
            }
        }
    }
}
