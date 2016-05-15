package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.model.MarvelComic;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicDetailsFragment extends Fragment {
    private static final String LOG_TAG = ComicDetailsFragment.class.getSimpleName();

    private static final String ARG_COMIC = "ivamluz.marvelshelf.comic";
    private static final String ARG_SHOW_TITLE = "ivamluz.marvelshelf.show_title";
    private static final String ARG_SHOW_THUMBNAIL = "ivamluz.marvelshelf.show_thumbnail";

    private Picasso mPicasso;

    private MarvelComic mComic;

    private boolean mShowThumbnail;
    private boolean mShowTitle;

    //    @BindView(R.id.characterName)
    private TextView mTextTitle;
    private TextView mTextDescription;
    private ImageView mImageThumbnail;


    public ComicDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param comic
     * @return A new instance of fragment ComicDetailsFragment.
     */
    public static ComicDetailsFragment newInstance(MarvelComic comic, boolean showName, boolean showThumbnail) {
        ComicDetailsFragment fragment = new ComicDetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_COMIC, comic);
        args.putBoolean(ARG_SHOW_TITLE, showName);
        args.putBoolean(ARG_SHOW_THUMBNAIL, showThumbnail);

        fragment.setArguments(args);

        return fragment;
    }

    public static ComicDetailsFragment newInstance(MarvelComic comic) {
        return newInstance(comic, false, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mComic = getArguments().getParcelable(ARG_COMIC);
            mShowThumbnail = getArguments().getBoolean(ARG_SHOW_THUMBNAIL);
            mShowTitle = getArguments().getBoolean(ARG_SHOW_TITLE);

            MarvelShelfLogger.debug(LOG_TAG, "comic: " + mComic);
        }

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comic_details, container, false);
//        ButterKnife.bind(this, rootView);

        mImageThumbnail = (ImageView) rootView.findViewById(R.id.image_details_thumb);
        mTextTitle = (TextView) rootView.findViewById(R.id.text_name);
        mTextDescription = (TextView) rootView.findViewById(R.id.text_description);

        mImageThumbnail.setVisibility(mShowThumbnail ? View.VISIBLE : View.GONE);
        mTextTitle.setVisibility(mShowTitle ? View.VISIBLE : View.GONE);

        bindValues();

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

    private void bindValues() {
        if (mShowThumbnail) {
            mPicasso.load(mComic.getThumbnailUrl())
                    .placeholder(R.drawable.character_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.character_placeholder)
                    .into(mImageThumbnail);
        }

        if (mShowTitle) {
            mTextTitle.setText(mComic.getTitle());
        }

        String description = mComic.getDescription();
        if (TextUtils.isEmpty(description)) {
            description = getString(R.string.not_available_description);
        }
        mTextDescription.setText(description);
    }
}

