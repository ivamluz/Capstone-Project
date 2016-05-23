package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.CharactersCursorAdapter;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.managers.BookmarksManager;
import ivamluz.marvelshelf.ui.activities.CharacterDetailsActivity;
import ivamluz.marvelshelf.ui.decorators.MarginItemDecoration;

import static butterknife.ButterKnife.findById;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link CharactersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharactersListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public final static String TAG = CharactersListFragment.class.getSimpleName();

    public static int LIST_TYPE_ALL = 0;
    public static int LIST_TYPE_BOOKMARKS = 1;
    public static int LIST_TYPE_SEEN = 2;
    public static final int LIST_TYPE_SEARCH = 4;

    private static final String LOG_TAG = CharactersListFragment.class.getSimpleName();
    private static final String EXTRA_LIST_TYPE = String.format("%s.list_type", BuildConfig.APPLICATION_ID);

    private int mListType;

    @BindView(R.id.recycler_view_characters_list)
    protected RecyclerView mCharactersRecyclerView;

    @BindView(R.id.progress_bar)
    protected ProgressBar mProgressBar;

    @BindView(R.id.view_blank_state)
    protected View mViewEmptyList;

    private CharactersCursorAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Unbinder mUnbinder;

    private BookmarksManager mBookmarksManager;

//    private LoaderManager mLoaderManager;

    private String mSearchTerm;

    public CharactersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CharactersListFragment.
     */
    public static CharactersListFragment newInstance(int listType) {
        CharactersListFragment fragment = new CharactersListFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mListType = getArguments().getInt(EXTRA_LIST_TYPE);

            MarvelShelfLogger.debug(LOG_TAG, "listType: " + mListType);
        }

        setupBookmarksManager();
    }

    private void setupBookmarksManager() {
        mBookmarksManager = new BookmarksManager(getContext(), new BookmarksManager.BookmarkCallbacks() {
            @Override
            public void onBookmarkToogled(boolean isBookmarked) {
                MarvelShelfLogger.debug(LOG_TAG, String.format("onBookmarkToogled: ", isBookmarked));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_characters_list, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        setupAdapter();

        mLayoutManager = new LinearLayoutManager(getContext());
        mCharactersRecyclerView.setLayoutManager(mLayoutManager);
        mCharactersRecyclerView.setAdapter(mAdapter);

        int marginBottom = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mCharactersRecyclerView.addItemDecoration(new MarginItemDecoration(0, 0, marginBottom, 0));

        mViewEmptyList.setVisibility(View.GONE);
        View retryButton = findById(mViewEmptyList, R.id.button_blank_state_action);
        retryButton.setVisibility(View.GONE);

        if (LIST_TYPE_SEARCH != mListType) {
            getActivity().getSupportLoaderManager().initLoader(mListType, null, this);
        }

        return rootView;
    }

    private void setupAdapter() {
        mAdapter = new CharactersCursorAdapter(getContext(), null);
        mAdapter.setOnItemClickListener(new CharactersCursorAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position, View view) {
                mAdapter.getCursor().moveToPosition(position);

                MarvelCharacter character = MarvelCharacter.fromCursor(mAdapter.getCursor());
                Intent intent = CharacterDetailsActivity.newIntent(getContext(), character);

                ImageView characterImage = findById(view, R.id.image_details_thumb);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        characterImage,
                        getContext().getString(R.string.shared_transition_character_image)
                );

                startActivity(intent, options.toBundle());
            }
        });

        mAdapter.setOnToogleBookmarkListener(new CharactersCursorAdapter.OnToogleBookmarkListener() {

            @Override
            public void onToogleBookmark(long characterId) {
                mBookmarksManager.toggleBookmark(characterId);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
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
        mViewEmptyList.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mCharactersRecyclerView.setVisibility(View.GONE);

        Pair<String, String[]> selectionArgs = getSelectionArgs();
        return new CursorLoader(getContext(), MarvelShelfContract.CharacterEntry.CONTENT_URI, null, selectionArgs.first, selectionArgs.second, getSortOrder());
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (!isAdded()) {
            return;
        }

        if (!isKnownLoader(loader.getId())) {
            return;
        }

        cursor.moveToFirst();

        mAdapter.swapCursor(cursor);
        mAdapter.notifyDataSetChanged();

        boolean hasItems = (cursor.getCount() > 0);
        mViewEmptyList.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mCharactersRecyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    }

    public void search(String searchTerm) {
        mSearchTerm = searchTerm;

        Loader loader = getActivity().getSupportLoaderManager().getLoader(LIST_TYPE_SEARCH);

        if (loader == null) {
            getActivity().getSupportLoaderManager().initLoader(LIST_TYPE_SEARCH, null, null);
            return;
        }

        if (loader.isStarted()) {
            loader.cancelLoad();
        }

        if (!loader.isReset()) {
            loader.isReset();
        }

        getActivity().getSupportLoaderManager().initLoader(LIST_TYPE_SEARCH, null, null);
    }

    private boolean isKnownLoader(int loaderId) {
        return Arrays.asList(LIST_TYPE_ALL, LIST_TYPE_BOOKMARKS, LIST_TYPE_SEEN).contains(loaderId);
    }


    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
        mAdapter.notifyDataSetChanged();
    }

    public String getSortOrder() {
        String sortOrder = null;

        if (LIST_TYPE_ALL == mListType) {
            sortOrder = String.format("%s ASC", MarvelShelfContract.CharacterEntry.COLUMN_NAME);
        } else if (LIST_TYPE_BOOKMARKS == mListType) {
            sortOrder = String.format("%s DESC", MarvelShelfContract.CharacterEntry.COLUMN_NAME);
        } else if (LIST_TYPE_SEEN == mListType) {
            sortOrder = String.format("%s DESC", MarvelShelfContract.CharacterEntry.COLUMN_LAST_SEEN);
        }

        return sortOrder;
    }

    public Pair<String, String[]> getSelectionArgs() {
        String selection = null;
        String[] selectionArgs = null;

        if (LIST_TYPE_ALL == mListType) {
            selection = null;
            selectionArgs = null;
        } else if (LIST_TYPE_BOOKMARKS == mListType) {
            selection = String.format("%s = ?", MarvelShelfContract.CharacterEntry.COLUMN_IS_BOOKMARK);
            selectionArgs = new String[]{"1"};
        } else if (LIST_TYPE_SEEN == mListType) {
            selection = String.format("%s IS NOT NULL", MarvelShelfContract.CharacterEntry.COLUMN_LAST_SEEN);
            selectionArgs = null;
        } else if (LIST_TYPE_SEARCH == mListType) {
            selection = String.format("%s like %%s%", MarvelShelfContract.CharacterEntry.COLUMN_LAST_SEEN, mSearchTerm);
            selectionArgs = null;
        }

        return new Pair<>(selection, selectionArgs);
    }
}
