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
 * <p/>
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
        void onComicsLoadingPreExecute();

        void onComicsLoadingCancelled();

        void onComicsLoaded(List<ComicDto> comics);
    }

    private TaskCallbacks mListener;
    private LoadComicsTask mTask;

    public static ComicsLoaderWorkerFragment newInstance(long characterId) {
        ComicsLoaderWorkerFragment fragment = new ComicsLoaderWorkerFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_CHARACTER_ID, characterId);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (TaskCallbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCharacterId = getArguments().getLong(ARG_CHARACTER_ID);
        }

        load();
    }

    @Override
    public void load() {
        super.load();

        mTask = new LoadComicsTask();
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * An async task that fetches comics for a given character.
     */
    private class LoadComicsTask extends AsyncTask<Void, Integer, List<ComicDto>> {

        @Override
        protected void onPreExecute() {
            ComicsLoaderWorkerFragment.this.mIsLoading = true;

            if (mListener != null) {
                mListener.onComicsLoadingPreExecute();
            }
        }

        @Override
        protected List<ComicDto> doInBackground(Void... ignore) {
            ComicApiClient comicApiClient = new ComicApiClient(MarvelShelfApplication.getInstance().getMarvelApiConfig());
            ComicsQuery query = ComicsQuery.Builder.create().addCharacter((int) mCharacterId).withOffset(0).withLimit(100).build();

            MarvelResponse<ComicsDto> response = null;
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
            ComicsLoaderWorkerFragment.this.mIsLoading = false;

            if (mListener != null) {
                mListener.onComicsLoadingCancelled();
            }
        }

        @Override
        protected void onPostExecute(List<ComicDto> comics) {
            ComicsLoaderWorkerFragment.this.mIsLoading = false;

            if (mListener != null) {
                mListener.onComicsLoaded(comics);
            }
        }
    }
}
