package ivamluz.marvelshelf.ui.fragments.workers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.karumi.marvelapiclient.MarvelApiException;
import com.karumi.marvelapiclient.SeriesApiClient;
import com.karumi.marvelapiclient.model.MarvelResponse;
import com.karumi.marvelapiclient.model.SeriesCollectionDto;
import com.karumi.marvelapiclient.model.SeriesDto;
import com.karumi.marvelapiclient.model.SeriesQuery;

import java.util.LinkedList;
import java.util.List;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/10/16.
 * <p/>
 * This Fragment manages a single background task responsible for loading series asyncly and retains
 * itself across configuration changes.
 */
public class SeriesLoaderWorkerFragment extends AbstractWorkerFragment {
    public static final String TAG = SeriesLoaderWorkerFragment.class.getSimpleName();

    private static final String LOG_TAG = TAG;

    private static final String ARG_CHARACTER_ID = "ivamluz.marvelshelf.character_id";

    private long mCharacterId;

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TaskCallbacks {
        void onSeriesLoadingPreExecute();

        void onSeriesLoadingCancelled();

        void onSeriesLoaded(List<SeriesDto> series);
    }

    private TaskCallbacks mListener;
    private LoadSeriesTask mTask;

    public static SeriesLoaderWorkerFragment newInstance(long characterId) {
        SeriesLoaderWorkerFragment fragment = new SeriesLoaderWorkerFragment();

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

        mTask = new LoadSeriesTask();
        mTask.execute();
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
     * An async task that fetches series for a given character.
     */
    private class LoadSeriesTask extends AsyncTask<Void, Integer, List<SeriesDto>> {

        @Override
        protected void onPreExecute() {
            if (mListener != null) {
                mListener.onSeriesLoadingPreExecute();
            }
        }

        @Override
        protected List<SeriesDto> doInBackground(Void... ignore) {
            SeriesApiClient seriesApiClient = new SeriesApiClient(MarvelShelfApplication.getInstance().getMarvelApiConfig());
            SeriesQuery query = SeriesQuery.Builder.create().addCharacter((int) mCharacterId).withOffset(0).withLimit(100).build();

            MarvelResponse<SeriesCollectionDto> response = null;
            try {
                response = seriesApiClient.getAll(query);
            } catch (MarvelApiException e) {
                MarvelShelfLogger.error(LOG_TAG, e);
            }

            if (response != null) {
                return response.getResponse().getSeries();
            } else {
                MarvelShelfLogger.debug(LOG_TAG, "response is null");
                return new LinkedList<>();
            }
        }

        @Override
        protected void onCancelled() {
            if (mListener != null) {
                mListener.onSeriesLoadingCancelled();
            }
        }

        @Override
        protected void onPostExecute(List<SeriesDto> series) {
            if (mListener != null) {
                mListener.onSeriesLoaded(series);
            }
        }
    }
}
