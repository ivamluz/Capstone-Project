package ivamluz.marvelshelf.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.MarvelShelfContract;

/**
 * Created by iluz on 5/3/16.
 * <p/>
 * Credits: https://gist.github.com/skyfishjy/443b7448f59be978bc59#file-mylistcursoradapter-java
 */
public class CharactersCursorAdapter extends AbstractCursorRecyclerViewAdapter<CharactersCursorAdapter.ViewHolder> {
    private static final String LOG_TAG = CharactersCursorAdapter.class.getSimpleName();

    private Context mContext;
    Picasso mPicasso;

    private OnItemClickListener mOnItemClickListener;

    private OnToogleBookmarkListener mOnToogleBookmarkListener;

    public CharactersCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public long mCharacterId;

        @BindView(R.id.image_details_thumb)
        public ImageView mImageViewCharacterThumbnail;

        @BindView(R.id.text_name)
        public TextView mTxtViewCharacterName;

        @BindView(R.id.text_description)
        public TextView mTxtViewCharacterDescription;

        @BindView(R.id.card_toolbar)
        protected Toolbar mCardToolbar;

        @BindView(R.id.action_toggle_bookmark)
        public ImageButton mButtomToggleBookmark;

        private OnItemClickListener mOnItemClickListener;

        private OnToogleBookmarkListener mOnToogleBookmarkListener;


        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            mCardToolbar.setVisibility(View.VISIBLE);

            view.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        public void setOnToogleBookmarkListener(OnToogleBookmarkListener onToogleBookmarkListener) {
            mOnToogleBookmarkListener = onToogleBookmarkListener;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition(), view);
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.action_toggle_bookmark)
        public void toggleBookmark(View view) {
            if (mOnToogleBookmarkListener != null) {
                mOnToogleBookmarkListener.onToogleBookmark(mCharacterId);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character_main_info, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        vh.setOnItemClickListener(mOnItemClickListener);
        vh.setOnToogleBookmarkListener(mOnToogleBookmarkListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.mCharacterId = cursor.getLong(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_ID));

        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_THUMBNAIL));
        mPicasso.load(thumbnailUrl)
                .placeholder(R.drawable.character_placeholder_landscape)
                .fit()
                .centerCrop()
                .error(R.drawable.character_placeholder_landscape)
                .into(viewHolder.mImageViewCharacterThumbnail);

        String name = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_NAME));
        viewHolder.mTxtViewCharacterName.setText(name);

        String description = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_DESCRIPTION));
        if (TextUtils.isEmpty(description)) {
            description = mContext.getString(R.string.not_available_description);
        }
        viewHolder.mTxtViewCharacterDescription.setText(Html.fromHtml(description));


        boolean isBookmarked = cursor.getInt(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_IS_BOOKMARK)) > 0;
        int bookmarkIconId = R.drawable.ic_bookmark_border;
        if (isBookmarked) {
            bookmarkIconId = R.drawable.ic_bookmark_filled;
        }
        viewHolder.mButtomToggleBookmark.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), bookmarkIconId, mContext.getTheme()));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnToogleBookmarkListener(OnToogleBookmarkListener onToogleBookmarkListener) {
        mOnToogleBookmarkListener = onToogleBookmarkListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    public interface OnToogleBookmarkListener {
        void onToogleBookmark(long characterId);
    }
}