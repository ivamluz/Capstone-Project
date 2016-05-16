package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.marvelapiclient.model.ComicDto;
import com.karumi.marvelapiclient.model.MarvelImage;
import com.karumi.marvelapiclient.model.SeriesDto;
import com.squareup.picasso.Picasso;

import java.util.List;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.AbstractCharacterRelatedItemsAdapter;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.model.MarvelComic;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.ui.activities.CharacterDetailsActivity;
import ivamluz.marvelshelf.ui.activities.ComicDetailsActivity;
import ivamluz.marvelshelf.ui.decorators.MarginItemDecoration;
import ivamluz.marvelshelf.ui.fragments.workers.ComicsLoaderWorkerFragment;
import ivamluz.marvelshelf.ui.fragments.workers.SeriesLoaderWorkerFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ComicsLoaderWorkerFragment.TaskCallbacks, SeriesLoaderWorkerFragment.TaskCallbacks, AbstractCharacterRelatedItemsAdapter.OnItemClickListener {
    private static final String LOG_TAG = CharacterDetailsFragment.class.getSimpleName();
    private static final int CHARACTER_LOADER = 100;

    private static final String ARG_CHARACTER_ID = "ivamluz.marvelshelf.character_id";
    private static final String ARG_SHOW_NAME = "ivamluz.marvelshelf.show_name";
    private static final String ARG_SHOW_THUMBNAIL = "ivamluz.marvelshelf.show_thumbnail";

    private Picasso mPicasso;

    private long mCharacterId;

    private AbstractCharacterRelatedItemsAdapter mAdapterCharacterComics;
    private AbstractCharacterRelatedItemsAdapter mAdapterCharacterSeries;

    //    @BindView(R.id.recycler_view_character_comics)
    private RecyclerView mRecyclerViewCharacterComics;
    private RecyclerView mRecyclerViewCharacterSeries;

    private ComicsLoaderWorkerFragment mComicsLoaderWorkerFragment;
    private SeriesLoaderWorkerFragment mSeriesLoaderWorkerFragment;

    private boolean mShowThumbnail;
    private boolean mShowCharacterName;

    //    @BindView(R.id.characterName)
    private TextView mTextCharacterName;
    private TextView mTextCharacterDescription;
    private ImageView mImageCharacterThumbnail;


    public CharacterDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param characterId Parameter 1.
     * @return A new instance of fragment BookmarksFragment.
     */
    public static CharacterDetailsFragment newInstance(long characterId, boolean showName, boolean showThumbnail) {
        CharacterDetailsFragment fragment = new CharacterDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_CHARACTER_ID, characterId);
        args.putBoolean(ARG_SHOW_NAME, showName);
        args.putBoolean(ARG_SHOW_THUMBNAIL, showThumbnail);

        fragment.setArguments(args);

        return fragment;
    }

    public static CharacterDetailsFragment newInstance(long characterId) {
        return newInstance(characterId, false, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCharacterId = getArguments().getLong(ARG_CHARACTER_ID);
            mShowThumbnail = getArguments().getBoolean(ARG_SHOW_THUMBNAIL);
            mShowCharacterName = getArguments().getBoolean(ARG_SHOW_NAME);

            MarvelShelfLogger.debug(LOG_TAG, "characterId: " + mCharacterId);
        }

        setupComicsLoaderFragment();
        setupSeriesLoaderFragment();

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();
    }

    private void setupComicsLoaderFragment() {
        FragmentManager fm = getFragmentManager();

        mComicsLoaderWorkerFragment = (ComicsLoaderWorkerFragment) fm.findFragmentByTag(ComicsLoaderWorkerFragment.TAG);
        if (mComicsLoaderWorkerFragment == null) {
            mComicsLoaderWorkerFragment = ComicsLoaderWorkerFragment.newInstance(mCharacterId);
            fm.beginTransaction().add(mComicsLoaderWorkerFragment, ComicsLoaderWorkerFragment.TAG).commit();
        }
    }

    private void setupSeriesLoaderFragment() {
        FragmentManager fm = getFragmentManager();
        mSeriesLoaderWorkerFragment = (SeriesLoaderWorkerFragment) fm.findFragmentByTag(SeriesLoaderWorkerFragment.TAG);
        if (mSeriesLoaderWorkerFragment == null) {
            mSeriesLoaderWorkerFragment = SeriesLoaderWorkerFragment.newInstance(mCharacterId);
            fm.beginTransaction().add(mSeriesLoaderWorkerFragment, SeriesLoaderWorkerFragment.TAG).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_character_details, container, false);
//        ButterKnife.bind(this, rootView);

        mImageCharacterThumbnail = (ImageView) rootView.findViewById(R.id.image_details_thumb);
        mTextCharacterName = (TextView) rootView.findViewById(R.id.text_name);
        mTextCharacterDescription = (TextView) rootView.findViewById(R.id.text_description);

        mImageCharacterThumbnail.setVisibility(mShowThumbnail ? View.VISIBLE : View.GONE);
        mTextCharacterName.setVisibility(mShowCharacterName ? View.VISIBLE : View.GONE);

        setupComicsAdapterAndRecyclerView(rootView);
        setupSeriesAdapterAndRecyclerView(rootView);

        return rootView;
    }

    private void setupComicsAdapterAndRecyclerView(View view) {
        mAdapterCharacterComics = new AbstractCharacterRelatedItemsAdapter<ComicDto>(ComicDto.class, null) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);

                ComicDto comic = mItems.get(position);

                if (!comic.getImages().isEmpty()) {
                    MarvelImage image = comic.getImages().get(0);
                    setThumbnail(holder, image);
                }

                setTitle(holder, comic.getTitle());
            }
        };
        mAdapterCharacterComics.setOnItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewCharacterComics = (RecyclerView) view.findViewById(R.id.recycler_view_character_comics);
        mRecyclerViewCharacterComics.setLayoutManager(layoutManager);
        mRecyclerViewCharacterComics.setAdapter(mAdapterCharacterComics);
        int marginRight = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mRecyclerViewCharacterComics.addItemDecoration(new MarginItemDecoration(0, marginRight, 0, 0));
    }

    private void setupSeriesAdapterAndRecyclerView(View view) {
        mAdapterCharacterSeries = new AbstractCharacterRelatedItemsAdapter<SeriesDto>(SeriesDto.class, null) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);

                SeriesDto series = mItems.get(position);

                if (series.getThumbnail() != null) {
                    setThumbnail(holder, series.getThumbnail());
                }

                setTitle(holder, series.getTitle());
            }
        };
        mAdapterCharacterSeries.setOnItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewCharacterSeries = (RecyclerView) view.findViewById(R.id.recycler_view_character_series);
        mRecyclerViewCharacterSeries.setLayoutManager(layoutManager);
        mRecyclerViewCharacterSeries.setAdapter(mAdapterCharacterSeries);
        int marginRight = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mRecyclerViewCharacterSeries.addItemDecoration(new MarginItemDecoration(0, marginRight, 0, 0));

        getActivity().getSupportLoaderManager().initLoader(CHARACTER_LOADER, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri characterUri = MarvelShelfContract.CharacterEntry.buildCharacterUri(mCharacterId);
        return new CursorLoader(getContext(), characterUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        cursor.moveToFirst();

        bindValues(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void bindValues(Cursor cursor) {
        if (mShowThumbnail) {
            String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_THUMBNAIL));
            mPicasso.load(thumbnailUrl)
                    .placeholder(R.drawable.character_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.character_placeholder)
                    .into(mImageCharacterThumbnail);
        }

        if (mShowCharacterName) {
            String name = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_NAME));
            mTextCharacterName.setText(name);
        }

        String description = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_DESCRIPTION));
        if (TextUtils.isEmpty(description)) {
            description = getString(R.string.not_available_description);
        }
        mTextCharacterDescription.setText(Html.fromHtml(description));
    }

    @Override
    public void onComicsLoadingPreExecute() {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoadingPreExecute");
    }

    @Override
    public void onComicsLoadingCancelled() {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoadingCancelled");
    }

    @Override
    public void onComicsLoaded(List<ComicDto> comics) {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoaded - comics: " + comics);

        mAdapterCharacterComics.setItems(comics);
        mAdapterCharacterComics.notifyDataSetChanged();
    }

    @Override
    public void onSeriesLoadingPreExecute() {
        MarvelShelfLogger.debug(LOG_TAG, "onSeriesLoadingPreExecute");
    }

    @Override
    public void onSeriesLoadingCancelled() {
        MarvelShelfLogger.debug(LOG_TAG, "onSeriesLoadingCancelled");
    }

    @Override
    public void onSeriesLoaded(List<SeriesDto> series) {
        MarvelShelfLogger.debug(LOG_TAG, "onSeriesLoaded - comics: " + series);
        mAdapterCharacterSeries.setItems(series);
        mAdapterCharacterSeries.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Class type, int position, View view) {
        MarvelShelfLogger.debug(LOG_TAG, "type: " + type + " | position: " + position);

        if (type == ComicDto.class) {
            ComicDto comic = (ComicDto) mAdapterCharacterComics.getItem(position);
            showComicDetails(comic, view);
        } else if (type == SeriesDto.class) {
            SeriesDto series = (SeriesDto) mAdapterCharacterSeries.getItem(position);
            showSeriesDetails(series, view);
        } else {
            MarvelShelfLogger.debug(LOG_TAG, "Unknown item type: " + type);
        }
    }

    private void showComicDetails(ComicDto comic, View view) {
        MarvelShelfLogger.debug(LOG_TAG, "Comic: " + comic);

        MarvelComic marvelComic = MarvelComic.fromComicDto(comic);
        Intent intent = ComicDetailsActivity.newIntent(getContext(), marvelComic);

        startDetailsActivity(view, intent);
    }

    private void startDetailsActivity(View view, Intent intent) {
        ImageView imageView = (ImageView) view.findViewById(R.id.image_item_thumb);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                imageView,
                getContext().getString(R.string.shared_transition_character_image)
        );

        startActivity(intent, options.toBundle());
    }

    private void showSeriesDetails(SeriesDto series, View view) {
        MarvelShelfLogger.debug(LOG_TAG, "Series: " + series);
    }
}

