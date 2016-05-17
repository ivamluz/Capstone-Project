package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.ImagesAdapter;
import ivamluz.marvelshelf.data.model.MarvelComic;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.ui.activities.ImageViewerActivity;
import ivamluz.marvelshelf.ui.decorators.MarginItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComicDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicDetailsFragment extends Fragment implements ImagesAdapter.OnItemClickListener {
    private static final String LOG_TAG = ComicDetailsFragment.class.getSimpleName();

    private static final String ARG_COMIC = "ivamluz.marvelshelf.comic";
    private static final String ARG_SHOW_TITLE = "ivamluz.marvelshelf.show_title";
    private static final String ARG_SHOW_THUMBNAIL = "ivamluz.marvelshelf.show_thumbnail";
    private static final int LAYOUT_COLUMNS = 2;

    private ImagesAdapter mImagesAdapter;
    private RecyclerView mImagesRecyclerView;

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

        setupImagesRecyclerViewAndAdapter(rootView);

        bindValues();

        return rootView;
    }

    private void setupImagesRecyclerViewAndAdapter(View view) {
        mImagesAdapter = new ImagesAdapter(mComic.getImageUrls(), getString(R.string.shared_transition_comic_image));
        mImagesAdapter.setOnItemClickListener(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(LAYOUT_COLUMNS, StaggeredGridLayoutManager.VERTICAL);

        mImagesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_comics_images);
        mImagesRecyclerView.setLayoutManager(layoutManager);
        mImagesRecyclerView.setAdapter(mImagesAdapter);
        int margin = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mImagesRecyclerView.addItemDecoration(new MarginItemDecoration(margin, margin, margin, margin));
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
        mTextDescription.setText(Html.fromHtml(description));
    }

    @Override
    public void onItemClick(int position, View view) {
        String url = mImagesAdapter.getItem(position);
        Intent intent = ImageViewerActivity.newIntent(getContext(), "1", url, getString(R.string.shared_transition_comic_image));

        startActivity(intent);
    }
}

