package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.karumi.marvelapiclient.model.ComicDto;
import com.karumi.marvelapiclient.model.MarvelImage;
import com.karumi.marvelapiclient.model.SeriesDto;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.AbstractCharacterRelatedItemsAdapter;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.data.model.MarvelComic;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.managers.SeenCharactersManager;
import ivamluz.marvelshelf.ui.activities.ComicDetailsActivity;
import ivamluz.marvelshelf.ui.decorators.MarginItemDecoration;
import ivamluz.marvelshelf.ui.fragments.workers.ComicsLoaderWorkerFragment;
import ivamluz.marvelshelf.ui.fragments.workers.SeriesLoaderWorkerFragment;

import static butterknife.ButterKnife.findById;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ComicsLoaderWorkerFragment.TaskCallbacks, SeriesLoaderWorkerFragment.TaskCallbacks, AbstractCharacterRelatedItemsAdapter.OnItemClickListener {
    public static final String TAG = CharacterDetailsFragment.class.getSimpleName();
    private static final String LOG_TAG = TAG;

    private static final int CHARACTER_LOADER = 100;

    private static final String KEY_CHARACTER_ID = String.format("%s.character_id", BuildConfig.APPLICATION_ID);
    private static final String KEY_CHARACTER = String.format("%s.character", BuildConfig.APPLICATION_ID);
    private static final String KEY_SHOW_CHARACTER_NAME = String.format("%s.show_name", BuildConfig.APPLICATION_ID);
    private static final String KEY_SHOW_CHARACTER_THUMBNAIL = String.format("%s.show_thumbnail", BuildConfig.APPLICATION_ID);

    private static final String KEY_CHARACTER_REGISTERED_AS_SEEN = String.format("%s.character_seen", BuildConfig.APPLICATION_ID);

    private static final String KEY_COMICS_LIST_LAYOUT_STATE = String.format("%s.comics_layout_list_state", BuildConfig.APPLICATION_ID);

    private Picasso mPicasso;

    private AbstractCharacterRelatedItemsAdapter mAdapterCharacterComics;
    private AbstractCharacterRelatedItemsAdapter mAdapterCharacterSeries;

    @BindView(R.id.text_name)
    protected TextView mTextCharacterName;

    @BindView(R.id.text_description)
    protected TextView mTextCharacterDescription;

    @BindView(R.id.image_details_thumb)
    protected ImageView mImageCharacterThumbnail;

    @BindView(R.id.recycler_view_character_comics)
    protected RecyclerView mRecyclerViewCharacterComics;

    @BindView(R.id.recycler_view_character_series)
    protected RecyclerView mRecyclerViewCharacterSeries;

    @BindView(R.id.card_character_comics)
    protected CardView mCardCharacterComics;

    private View mViewEmptyComics;
    private ProgressBar mProgressComics;

    private ComicsLoaderWorkerFragment mComicsLoaderWorkerFragment;
    private SeriesLoaderWorkerFragment mSeriesLoaderWorkerFragment;

    private long mCharacterId;
    private boolean mShowThumbnail;
    private boolean mShowCharacterName;

    private boolean mRegisteredAsSeen = false;

    private MarvelCharacter mMarvelCharacter;

    private Unbinder mUnbinder;
    private Button mButtonRetryLoadingComics;

    private Parcelable mRecyclerViewCharacterComicsState;

    public CharacterDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param characterId Parameter 1.
     * @return A new instance of fragment CharacterDetailsFragment.
     */
    public static CharacterDetailsFragment newInstance(long characterId, boolean showName, boolean showThumbnail) {
        CharacterDetailsFragment fragment = new CharacterDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_CHARACTER_ID, characterId);
        args.putBoolean(KEY_SHOW_CHARACTER_NAME, showName);
        args.putBoolean(KEY_SHOW_CHARACTER_THUMBNAIL, showThumbnail);

        fragment.setArguments(args);

        return fragment;
    }

    public static CharacterDetailsFragment newInstance(long characterId) {
        return newInstance(characterId, false, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MarvelShelfLogger.debug(LOG_TAG, MarvelShelfLogger.SEPARATOR);
        MarvelShelfLogger.debug(LOG_TAG, "onCreate()");
        MarvelShelfLogger.debug(LOG_TAG, MarvelShelfLogger.SEPARATOR);

        if (savedInstanceState == null && getArguments() != null) {
            mCharacterId = getArguments().getLong(KEY_CHARACTER_ID);
            mShowThumbnail = getArguments().getBoolean(KEY_SHOW_CHARACTER_THUMBNAIL);
            mShowCharacterName = getArguments().getBoolean(KEY_SHOW_CHARACTER_NAME);
        }

        logState();

        registerSeenCharacter();

        setupComicsLoaderFragment();
        setupSeriesLoaderFragment();

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        restoreInstaceState(savedInstanceState);


    }

    private void logState() {
        MarvelShelfLogger.debug(LOG_TAG, "mCharacterId: " + mCharacterId);
        MarvelShelfLogger.debug(LOG_TAG, "mShowThumbnail: " + mShowThumbnail);
        MarvelShelfLogger.debug(LOG_TAG, "mShowCharacterName: " + mShowCharacterName);
        MarvelShelfLogger.debug(LOG_TAG, "mRegisteredAsSeen: " + mRegisteredAsSeen);
        MarvelShelfLogger.debug(LOG_TAG, "mRecyclerViewCharacterComicsState: " + mRecyclerViewCharacterComicsState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_CHARACTER_ID, mCharacterId);
        outState.putParcelable(KEY_CHARACTER, mMarvelCharacter);
        outState.putBoolean(KEY_SHOW_CHARACTER_THUMBNAIL, mShowThumbnail);
        outState.putBoolean(KEY_SHOW_CHARACTER_NAME, mShowCharacterName);

        outState.putParcelable(KEY_COMICS_LIST_LAYOUT_STATE, mRecyclerViewCharacterComics.getLayoutManager().onSaveInstanceState());

        outState.putBoolean(KEY_CHARACTER_REGISTERED_AS_SEEN, mRegisteredAsSeen);
    }

    private void registerSeenCharacter() {
        if (mRegisteredAsSeen) {
            MarvelShelfLogger.debug(LOG_TAG, String.format("Character %s has already been registered as seen. Skipping it.", mCharacterId));
            return;
        }

        MarvelShelfLogger.debug(LOG_TAG, String.format("Registering character %s as seen.", mCharacterId));

        new SeenCharactersManager(getContext()).registrySeenCharacter(mCharacterId, new SeenCharactersManager.OnCharacterRegisteredAsSeen() {
            @Override
            public void onRegisteredAsSeen(Uri uri) {
                CharacterDetailsFragment.this.mRegisteredAsSeen = true;
                MarvelShelfLogger.debug(LOG_TAG, "Register callback. Uri: " + uri.toString());
            }
        });
    }

    private void restoreInstaceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        mCharacterId = savedInstanceState.getLong(KEY_CHARACTER_ID);
        mMarvelCharacter = savedInstanceState.getParcelable(KEY_CHARACTER);
        mShowThumbnail = savedInstanceState.getBoolean(KEY_SHOW_CHARACTER_THUMBNAIL);
        mShowCharacterName = savedInstanceState.getBoolean(KEY_SHOW_CHARACTER_NAME);

        mRegisteredAsSeen = savedInstanceState.getBoolean(KEY_CHARACTER_REGISTERED_AS_SEEN, false);

        mRecyclerViewCharacterComicsState = savedInstanceState.getParcelable(KEY_COMICS_LIST_LAYOUT_STATE);
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
        View rootView = inflater.inflate(R.layout.fragment_character_details, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mImageCharacterThumbnail.setVisibility(mShowThumbnail ? View.VISIBLE : View.GONE);
        mTextCharacterName.setVisibility(mShowCharacterName ? View.VISIBLE : View.GONE);

        mProgressComics = findById(mCardCharacterComics, R.id.progress_bar);
        mViewEmptyComics = findById(mCardCharacterComics, R.id.view_blank_state);
        setupRetryButton();

        setupComicsAdapterAndRecyclerView(rootView);
        setupSeriesAdapterAndRecyclerView(rootView);

        if (mMarvelCharacter == null) {
            getActivity().getSupportLoaderManager().initLoader(CHARACTER_LOADER, null, this);
        } else {
            bindValues();
        }

        return rootView;
    }

    private void setupRetryButton() {
        mButtonRetryLoadingComics = findById(mViewEmptyComics, R.id.button_blank_state_action);
        mButtonRetryLoadingComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mComicsLoaderWorkerFragment.isLoading()) {
                    mComicsLoaderWorkerFragment.load();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }

    private void setupComicsAdapterAndRecyclerView(View view) {
        List<ComicDto> comics = new LinkedList<>();
        if (!mComicsLoaderWorkerFragment.isLoading() && mComicsLoaderWorkerFragment.hasCachedResults()) {
            comics = mComicsLoaderWorkerFragment.getCachedResults();
        }

        mAdapterCharacterComics = new AbstractCharacterRelatedItemsAdapter<ComicDto>(ComicDto.class, comics) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);

                ComicDto comic = mItems.get(position);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mImageViewThumbnail.setTransitionName(getString(R.string.shared_transition_comic_thumb));
                }

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

        mRecyclerViewCharacterComics.setLayoutManager(layoutManager);
        mRecyclerViewCharacterComics.setAdapter(mAdapterCharacterComics);
        int marginRight = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mRecyclerViewCharacterComics.addItemDecoration(new MarginItemDecoration(0, marginRight, 0, 0));

        if (mRecyclerViewCharacterComicsState != null) {
            mRecyclerViewCharacterComics.getLayoutManager().onRestoreInstanceState(mRecyclerViewCharacterComicsState);
        }

        showOrHideComicsList(comics);
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mImageViewThumbnail.setTransitionName(getString(R.string.shared_transition_series_image));
                }

                setTitle(holder, series.getTitle());
            }
        };
        mAdapterCharacterSeries.setOnItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewCharacterSeries.setLayoutManager(layoutManager);
        mRecyclerViewCharacterSeries.setAdapter(mAdapterCharacterSeries);
        int marginRight = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mRecyclerViewCharacterSeries.addItemDecoration(new MarginItemDecoration(0, marginRight, 0, 0));
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

        mMarvelCharacter = MarvelCharacter.fromCursor(cursor);

        bindValues();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void bindValues() {
        if (mShowThumbnail) {
            mPicasso.load(mMarvelCharacter.getThumbnailUrl())
                    .placeholder(R.drawable.character_placeholder_landscape)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.character_placeholder_landscape)
                    .into(mImageCharacterThumbnail);
        }

        if (mShowCharacterName) {
            String name = mMarvelCharacter.getName();
            mTextCharacterName.setText(name);
        }

        String description = mMarvelCharacter.getDescription();
        if (TextUtils.isEmpty(description)) {
            description = getString(R.string.not_available_description);
        }
        mTextCharacterDescription.setText(Html.fromHtml(description));
    }

    @Override
    public void onComicsLoadingPreExecute() {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoadingPreExecute");

        mViewEmptyComics.setVisibility(View.INVISIBLE);
        mRecyclerViewCharacterComics.setVisibility(View.GONE);
        mProgressComics.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComicsLoadingCancelled() {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoadingCancelled");

        mViewEmptyComics.setVisibility(View.VISIBLE);
        mRecyclerViewCharacterComics.setVisibility(View.GONE);
        mProgressComics.setVisibility(View.GONE);
    }

    @Override
    public void onComicsLoaded(List<ComicDto> comics) {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoaded - comics: " + comics);

        mAdapterCharacterComics.setItems(comics);
        mAdapterCharacterComics.notifyDataSetChanged();

        showOrHideComicsList(comics);
    }

    private void showOrHideComicsList(List<ComicDto> comics) {
        mViewEmptyComics.setVisibility(comics.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        mRecyclerViewCharacterComics.setVisibility(comics.isEmpty() ? View.GONE : View.VISIBLE);
        mProgressComics.setVisibility(View.GONE);
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

        String sharedTransitionName = getContext().getString(R.string.shared_transition_comic_thumb);
        startDetailsActivity(view, intent, sharedTransitionName);
    }

    private void startDetailsActivity(View view, Intent intent, String sharedTransitionName) {
        ImageView imageView = findById(view, R.id.image_item_thumb);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                imageView,
                sharedTransitionName
        );

        startActivity(intent, options.toBundle());
    }

    private void showSeriesDetails(SeriesDto series, View view) {
        MarvelShelfLogger.debug(LOG_TAG, "Series: " + series);
    }
}

