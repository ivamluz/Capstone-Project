package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.marvelapiclient.model.ComicDto;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.CharacterComicsAdapter;
import ivamluz.marvelshelf.adapter.CharactersCursorAdapter;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.ui.fragments.workers.ComicsLoaderWorkerFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ComicsLoaderWorkerFragment.TaskCallbacks {
    private static final String LOG_TAG = CharacterDetailsFragment.class.getSimpleName();
    private static final int CHARACTER_LOADER = 100;

    private static final String ARG_CHARACTER_ID = "ivamluz.marvelshelf.character_id";
    private static final String ARG_SHOW_NAME = "ivamluz.marvelshelf.show_name";
    private static final String ARG_SHOW_THUMBNAIL = "ivamluz.marvelshelf.show_thumbnail";

    private Picasso mPicasso;

    private long mCharacterId;


//    @BindView(R.id.recycler_view_character_comics)
    private RecyclerView mRecyclerViewCharacterComics;

    private CharacterComicsAdapter mAdapterCharacterComics;

    private ComicsLoaderWorkerFragment mComicsLoaderWorkerFragment;


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
            mShowThumbnail= getArguments().getBoolean(ARG_SHOW_THUMBNAIL);
            mShowCharacterName = getArguments().getBoolean(ARG_SHOW_NAME);

            MarvelShelfLogger.debug(LOG_TAG, "characterId: " + mCharacterId);
        }

        FragmentManager fm = getFragmentManager();
        mComicsLoaderWorkerFragment = (ComicsLoaderWorkerFragment) fm.findFragmentByTag(ComicsLoaderWorkerFragment.TAG);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mComicsLoaderWorkerFragment == null) {
            mComicsLoaderWorkerFragment = ComicsLoaderWorkerFragment.newInstance(mCharacterId);
            fm.beginTransaction().add(mComicsLoaderWorkerFragment, ComicsLoaderWorkerFragment.TAG).commit();
        }

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_character_details, container, false);
//        ButterKnife.bind(this, rootView);

        mImageCharacterThumbnail = (ImageView) rootView.findViewById(R.id.image_character_thumb);
        mTextCharacterName = (TextView) rootView.findViewById(R.id.text_character_name);
        mTextCharacterDescription = (TextView) rootView.findViewById(R.id.text_character_description);

        mImageCharacterThumbnail.setVisibility(mShowThumbnail ? View.VISIBLE : View.GONE);
        mTextCharacterName.setVisibility(mShowCharacterName ? View.VISIBLE : View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewCharacterComics = (RecyclerView) rootView.findViewById(R.id.recycler_view_character_comics);
        mRecyclerViewCharacterComics.setLayoutManager(layoutManager);
        mRecyclerViewCharacterComics.setAdapter(mAdapterCharacterComics);

        getActivity().getSupportLoaderManager().initLoader(CHARACTER_LOADER, null, this);

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
        mTextCharacterDescription.setText(description);
    }

    @Override
    public void onPreExecute() {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoaded - onPreExecute");
    }

    @Override
    public void onCancelled() {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoaded - onCancelled");
    }

    @Override
    public void onComicsLoaded(List<ComicDto> comics) {
        MarvelShelfLogger.debug(LOG_TAG, "onComicsLoaded - comics: " + comics);
        mAdapterCharacterComics = new CharacterComicsAdapter(comics);
        mRecyclerViewCharacterComics.setAdapter(mAdapterCharacterComics);
    }
}

