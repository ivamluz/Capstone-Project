package ivamluz.marvelshelf.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.MarvelShelfContract;

/**
 * Created by iluz on 5/3/16.
 * <p/>
 * Credits: https://gist.github.com/skyfishjy/443b7448f59be978bc59#file-mylistcursoradapter-java
 */
public class MarvelCharactersCursorAdapter extends AbstractCursorRecyclerViewAdapter<MarvelCharactersCursorAdapter.ViewHolder> {
    private static final String LOG_TAG = MarvelCharactersCursorAdapter.class.getSimpleName();

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MarvelCharactersCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // TODO: fix problem with ButterKnife binding
//        @BindView(R.id.txt_view_character_name)
        public ImageView mImageViewCharacterThumbnail;
        public TextView mTxtViewCharacterName;
        public TextView mTxtViewCharacterDescription;

        private OnItemClickListener mOnItemClickListener;


        public ViewHolder(View view) {
            super(view);
//            ButterKnife.setDebug(true);
//            ButterKnife.bind(this, view);
            mImageViewCharacterThumbnail = (ImageView) view.findViewById(R.id.img_view_character_thumb);
            mTxtViewCharacterName = (TextView) view.findViewById(R.id.txt_view_character_name);
            mTxtViewCharacterDescription = (TextView) view.findViewById(R.id.txt_view_character_description);

            view.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition(), view);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        vh.setOnItemClickListener(mOnItemClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Picasso picasso = MarvelShelfApplication.getInstance().getPicasso();

        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_THUMBNAIL));
        picasso.load(thumbnailUrl)
                .placeholder(R.drawable.character_placeholder)
                .fit()
                .centerCrop()
                .error(R.drawable.character_placeholder)
                .into(viewHolder.mImageViewCharacterThumbnail);

        String name = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_NAME));
        viewHolder.mTxtViewCharacterName.setText(name);

        String description = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_DESCRIPTION));
        viewHolder.mTxtViewCharacterDescription.setText(description);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(int position, View v);
    }
}