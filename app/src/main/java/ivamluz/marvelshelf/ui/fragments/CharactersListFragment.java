package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.CharactersCursorAdapter;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.ui.activities.CharacterDetailsActivity;
import ivamluz.marvelshelf.ui.decorators.MarginItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link CharactersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharactersListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CharactersCursorAdapter.OnItemClickListener {
    public static int LIST_TYPE_ALL = 0;
    public static int LIST_TYPE_BOOKMARKS = 1;
    public static int LIST_TYPE_SEEN = 2;

    private static final String LOG_TAG = CharactersListFragment.class.getSimpleName();
    private static final String EXTRA_LIST_TYPE = "ivamluz.marvelshelf.list_type";
    private static final int LOADER_ID = 0;

    private int mListType;

    @BindView(R.id.recycler_view_characters_list)
    private RecyclerView mCharactersRecyclerView;

    private CharactersCursorAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_characters_list, container, false);
        ButterKnife.bind(this, rootView);

        mLayoutManager = new LinearLayoutManager(getContext());
        mCharactersRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_characters_list);
        mCharactersRecyclerView.setLayoutManager(mLayoutManager);
        mCharactersRecyclerView.setAdapter(mAdapter);

        int marginBottom = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mCharactersRecyclerView.addItemDecoration(new MarginItemDecoration(0, 0, marginBottom, 0));

        getActivity().getSupportLoaderManager().initLoader(mListType, null, this);

        return rootView;
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
        return new CursorLoader(getContext(), getUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        cursor.moveToFirst();

        mAdapter = new CharactersCursorAdapter(getContext(), cursor);
        mAdapter.setOnItemClickListener(this);

        mCharactersRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onItemClick(int position, View view) {
        mAdapter.getCursor().moveToPosition(position);

        MarvelCharacter character = MarvelCharacter.fromCursor(mAdapter.getCursor());
        Intent intent = CharacterDetailsActivity.newIntent(getContext(), character);

        ImageView characterImage = (ImageView) view.findViewById(R.id.image_details_thumb);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                characterImage,
                getContext().getString(R.string.shared_transition_character_image)
        );

        startActivity(intent, options.toBundle());
    }

    private Uri getUri() {
        if (LIST_TYPE_ALL == mListType) {
            return MarvelShelfContract.CharacterEntry.CONTENT_URI;
        } else if (LIST_TYPE_BOOKMARKS == mListType) {
            return MarvelShelfContract.BookmarkEntry.CONTENT_URI;
        } else if (LIST_TYPE_SEEN == mListType) {
            return null;
        } else {
            String message = String.format("listType should be one of %s.LIST_TYPE_* consts.", CharactersListFragment.class.getSimpleName());
            throw new IllegalArgumentException(message);
        }
    }
}
