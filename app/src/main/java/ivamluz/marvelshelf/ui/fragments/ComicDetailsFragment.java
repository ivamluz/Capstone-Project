package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.ImagesAdapter;
import ivamluz.marvelshelf.data.model.MarvelComic;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.ui.activities.ImageViewerActivity;
import ivamluz.marvelshelf.ui.decorators.MarginItemDecoration;

import static butterknife.ButterKnife.findById;

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

    @BindView(R.id.text_name)
    protected TextView mTextTitle;

    @BindView(R.id.text_description)
    protected TextView mTextDescription;

    @BindView(R.id.image_details_thumb)
    protected ImageView mImageThumbnail;

    private Unbinder mUnbinder;

    private InterstitialAd mInterstitialAd;
    private String mSelectedImageUrl;
    private View mImageSelectedComic;

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

        setupAd();

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();
    }

    private void setupAd() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(BuildConfig.AD_UNIT_ID);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                showSelectedImage();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comic_details, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mImageThumbnail.setVisibility(mShowThumbnail ? View.VISIBLE : View.GONE);
        mTextTitle.setVisibility(mShowTitle ? View.VISIBLE : View.GONE);

        setupImagesRecyclerViewAndAdapter(rootView);

        bindValues();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }

    private void setupImagesRecyclerViewAndAdapter(View view) {
        mImagesAdapter = new ImagesAdapter(mComic.getImageUrls(), getString(R.string.shared_transition_comic_image));
        mImagesAdapter.setOnItemClickListener(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(LAYOUT_COLUMNS, StaggeredGridLayoutManager.VERTICAL);

        mImagesRecyclerView = findById(view, R.id.recycler_view_comics_images);
        mImagesRecyclerView.setLayoutManager(layoutManager);
        mImagesRecyclerView.setAdapter(mImagesAdapter);
        int margin = getResources().getDimensionPixelSize(R.dimen.default_spacing);
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
                    .placeholder(R.drawable.character_placeholder_landscape)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.character_placeholder_landscape)
                    .into(mImageThumbnail);

            mImageThumbnail.setContentDescription(getString(R.string.content_description_comic_image, mComic.getTitle()));
        }

        if (mShowTitle) {
            mTextTitle.setText(mComic.getTitle());
            mTextTitle.setContentDescription(getString(R.string.content_description_comic_title, mComic.getTitle()));
        }

        String description = mComic.getDescription();
        if (TextUtils.isEmpty(description)) {
            description = getString(R.string.not_available_description);
        }
        mTextDescription.setText(Html.fromHtml(description));
        mTextDescription.setContentDescription(getString(R.string.content_description_comic_description, mComic.getDescription()));
    }

    @Override
    public void onItemClick(int position, View view) {
        mSelectedImageUrl = mImagesAdapter.getItem(position);
        mImageSelectedComic = findById(view, R.id.image_thumbnail);

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            showSelectedImage();
        }
    }

    private void showSelectedImage() {
        if (mSelectedImageUrl == null) {
            return;
        }

        String id = String.valueOf(mSelectedImageUrl.hashCode());
        Intent intent = ImageViewerActivity.newIntent(getContext(), id, mSelectedImageUrl, getString(R.string.shared_transition_comic_image));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                mImageSelectedComic,
                getContext().getString(R.string.shared_transition_comic_image)
        );

        startActivity(intent, options.toBundle());
    }
}

