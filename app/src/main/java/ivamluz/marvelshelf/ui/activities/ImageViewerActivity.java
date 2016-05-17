package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.helpers.ImageHelper;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerActivity extends AppCompatActivity {
    private static final int DOWNLOAD_TAG = 101;
    private static final int SHARE_TAG = 102;

    private static final String EXTRA_IMAGE_ID = "ivamluz.marvelshelf.image_id";
    private static final String EXTRA_IMAGE_URL = "ivamluz.marvelshelf.image_url";
    private static final String EXTRA_IMAGE_TRANSITION_NAME = "ivamluz.marvelshelf.image_transition_namel";
    private static final String LOG_TAG = ImageViewerActivity.class.getSimpleName();

    //    @Bind(R.id.photo)
    protected PhotoView mPhotoView;
    //    @Bind(R.id.photo_loading)
    protected ProgressBar mLoadingView;
    //    @Bind(R.id.toolbar)
    private Toolbar mToolbar;

    private Picasso mPicasso;

    private String mId = "";
    private String mUrl = "";

    private String mTransitionName = null;

    private PhotoViewAttacher mPhotoViewAttacher;

    public static Intent newIntent(Context context, String id, String url, String transitionName) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_IMAGE_ID, id);
        intent.putExtra(EXTRA_IMAGE_URL, url);
        intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, transitionName);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
//        ButterKnife.bind(this);

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mPhotoView = (PhotoView) findViewById(R.id.photo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mTransitionName != null) {
                mPhotoView.setTransitionName(mTransitionName);
            }
        }

        mPhotoViewAttacher = new PhotoViewAttacher(mPhotoView);
        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

            @Override
            public void onViewTap(View view, float x, float y) {

                toggleVisibility();
            }
        });

        mLoadingView = (ProgressBar) findViewById(R.id.photo_loading);

        configToolbar();
        setupPhoto(savedInstanceState);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mPhotoViewAttacher.cleanup();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_IMAGE_ID, mId);
        outState.putString(EXTRA_IMAGE_URL, mUrl);
        outState.putString(EXTRA_IMAGE_TRANSITION_NAME, mTransitionName);
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_download_photo:
                downloadPhoto();
                break;

            case R.id.action_share_photo:
                sharePhoto();
                break;

            case R.id.action_open_in_browser:
                openInBrowser();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void setupPhoto(Bundle savedInstanceBundle) {

        if (savedInstanceBundle != null) {
            mId = savedInstanceBundle.getString(EXTRA_IMAGE_ID);
            mUrl = savedInstanceBundle.getString(EXTRA_IMAGE_URL);
            mTransitionName = savedInstanceBundle.getString(EXTRA_IMAGE_TRANSITION_NAME);
        } else if (getIntent().hasExtra(EXTRA_IMAGE_URL)) {
            mId = getIntent().getStringExtra(EXTRA_IMAGE_ID);
            mUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
            mTransitionName = getIntent().getStringExtra(EXTRA_IMAGE_TRANSITION_NAME);
        }

        // Glide.with(this).load(mUrl).into(mPhotoView);

        mPicasso.load(mUrl).fit().into(mPhotoView);
    }

    private void downloadPhoto() {
        downloadMedia();
    }

    private void downloadMedia() {

        download(false);
    }

    private void downloadAndShare() {

        download(true);
    }

    private void download(boolean share) {

        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getString(R.string.app_name)
        );

        AsyncTaskCompat.executeParallel(new DownloadImageTask(this, storageDir, mId + ".png", share), mUrl);
    }

    private void sharePhoto() {
        downloadAndShare();
    }

    private void openInBrowser() {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mUrl));
            startActivity(intent);
        } catch (Exception e) {
            MarvelShelfLogger.error(LOG_TAG, e);
        }
    }

    private synchronized void toggleVisibility() {

        int newVisibility = (mToolbar.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;

        mToolbar.setVisibility(newVisibility);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Boolean> {

        private File mDirectory;
        private String mFilename;
        private Context mContext;
        private boolean mShareAfter;
        private File mSavedImage;

        public DownloadImageTask(Context context, File directory, String filename, Boolean shareAfterDownload) {

            this.mDirectory = directory;
            this.mFilename = filename;
            this.mContext = context;
            this.mShareAfter = shareAfterDownload;
        }

        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                for (int i = 0; i < urls.length; i++) {
                    String url = urls[i];

                    if (url != null) {
                        InputStream inputStream = (InputStream) new URL(url).getContent();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();

                        mSavedImage = ImageHelper.saveBitmapToPNG(bitmap, mDirectory, mFilename);

                        ImageHelper.addImageToGallery(mContext, mSavedImage.getAbsolutePath(), mFilename);
                    }
                }

                return true;
            } catch (Exception e) {
                MarvelShelfLogger.error(LOG_TAG, e);

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(ImageViewerActivity.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                return;
            }

            if (mShareAfter) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mSavedImage));
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_with)));
            } else {
                Toast.makeText(ImageViewerActivity.this, getString(R.string.save_image_success), Toast.LENGTH_SHORT).show();
            }
        }
    }
}