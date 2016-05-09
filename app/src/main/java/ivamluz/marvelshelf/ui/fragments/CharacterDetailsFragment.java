package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = CharacterDetailsFragment.class.getSimpleName();
    private static final int CHARACTER_LOADER = 100;

    private static final String ARG_CHARACTER_ID = "ivamluz.marvelshelf.character_id";

    private Picasso mPicasso;

    private long mCharacterId;

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
    // TODO: Rename and change types and number of parameters
    public static CharacterDetailsFragment newInstance(long characterId) {
        CharacterDetailsFragment fragment = new CharacterDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_CHARACTER_ID, characterId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCharacterId = getArguments().getLong(ARG_CHARACTER_ID);

            MarvelShelfLogger.debug(LOG_TAG, "characterId: " + mCharacterId);
        }

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_character_details, container, false);
//        ButterKnife.bind(this, rootView);

        mTextCharacterName = (TextView) rootView.findViewById(R.id.text_character_name);
        mTextCharacterDescription = (TextView) rootView.findViewById(R.id.text_character_description);
        mImageCharacterThumbnail = (ImageView) rootView.findViewById(R.id.image_character_thumb);

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

        String name = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_NAME));
        mTextCharacterName.setText(name);

        String description = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_DESCRIPTION));
        mTextCharacterDescription.setText(description);

//        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_THUMBNAIL));
//        mPicasso.load(thumbnailUrl)
//                .placeholder(R.drawable.character_placeholder)
//                .fit()
//                .centerCrop()
//                .error(R.drawable.character_placeholder)
//                .into(mImageCharacterThumbnail);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

