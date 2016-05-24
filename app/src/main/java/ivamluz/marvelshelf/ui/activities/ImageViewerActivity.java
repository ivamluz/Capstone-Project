package ivamluz.marvelshelf.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.helpers.ImageHelper;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

@RuntimePermissions
public class ImageViewerActivity extends BaseActivity {
    private static final String LOG_TAG = ImageViewerActivity.class.getSimpleName();

    private static final String EXTRA_IMAGE_ID = String.format("%s.image_id", BuildConfig.APPLICATION_ID);
    private static final String EXTRA_IMAGE_URL = String.format("%s.image_url", BuildConfig.APPLICATION_ID);
    private static final String EXTRA_IMAGE_TRANSITION_NAME = String.format("%s.image_transition_name", BuildConfig.APPLICATION_ID);

    @BindView(R.id.photo)
    protected PhotoView mPhotoView;

    @BindView(R.id.photo_loading)
    protected ProgressBar mProgressBar;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

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
        ButterKnife.bind(this);

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        mTransitionName = getIntent().getStringExtra(EXTRA_IMAGE_TRANSITION_NAME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mTransitionName != null) {
                mPhotoView.setTransitionName(mTransitionName);
            }
        }

        mPhotoViewAttacher = new PhotoViewAttacher(mPhotoView);
        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                toggleToolbarVisibility();
            }
        });

        configToolbar();
        setupPhoto(savedInstanceState);

        if (savedInstanceState == null) {
            String title = String.format("%s: %s", ImageViewerActivity.class.getSimpleName(), mUrl);
            trackScreenView(title);
        }
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
                ImageViewerActivityPermissionsDispatcher.downloadPhotoWithCheck(this);
                break;

            case R.id.action_share_photo:
                ImageViewerActivityPermissionsDispatcher.downloadAndShareWithCheck(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ImageViewerActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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

        loadPhoto();
    }

    private void loadPhoto() {
        mProgressBar.setVisibility(View.VISIBLE);
        mPicasso
                .load(mUrl)
                .fit()
                .centerInside()
                .error(R.drawable.character_placeholder_portrait)
                .into(mPhotoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    protected void downloadPhoto() {
        download(false);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    protected void downloadAndShare() {
        download(true);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForWriteExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.rationale_permission_write_external_storage)
                .setPositiveButton(R.string.action_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.action_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForExternalStorage() {
        Toast.makeText(this, R.string.permission_denied_write_external_storage, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForExternalStorage() {
        Toast.makeText(this, R.string.permission_never_ask_again_write_external_storage, Toast.LENGTH_SHORT).show();
    }


    private void download(boolean share) {
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getString(R.string.app_name)
        );

        AsyncTaskCompat.executeParallel(new DownloadImageTask(this, storageDir, mId + ".png", share), mUrl);
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

    private synchronized void toggleToolbarVisibility() {
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
        protected void onPreExecute() {
            super.onPreExecute();

            ImageViewerActivity.this.mProgressBar.setVisibility(View.VISIBLE);
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
        protected void onCancelled() {
            super.onCancelled();

            ImageViewerActivity.this.mProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            ImageViewerActivity.this.mProgressBar.setVisibility(View.GONE);

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
