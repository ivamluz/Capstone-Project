package ivamluz.marvelshelf.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/3/16.
 * <p/>
 * Credits: https://gist.github.com/skyfishjy/443b7448f59be978bc59#file-mylistcursoradapter-java
 */
public class MarvelCharactersCursorAdapter extends AbstractCursorRecyclerViewAdapter<MarvelCharactersCursorAdapter.ViewHolder> {
    private static final String LOG_TAG = MarvelCharactersCursorAdapter.class.getSimpleName();

    private Context mContext;

    public MarvelCharactersCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TODO: fix problem with ButterKnife binding
//        @BindView(R.id.txt_view_character_name)
        public TextView mTxtViewCharacterName;
        public ImageView mImageViewCharacterThumbnail;

        public ViewHolder(View view) {
            super(view);
//            ButterKnife.setDebug(true);
//            ButterKnife.bind(this, view);
            mTxtViewCharacterName = (TextView) view.findViewById(R.id.txt_view_character_name);
            mImageViewCharacterThumbnail = (ImageView) view.findViewById(R.id.img_view_character_thumb);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false);
        ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_NAME));
        viewHolder.mTxtViewCharacterName.setText(name);

        Picasso picasso = MarvelShelfApplication.getInstance().getPicasso();

        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_THUMBNAIL));
        picasso.load(thumbnailUrl)
                .placeholder(android.R.drawable.presence_away)
                .fit()
                .centerCrop()
                .error(android.R.drawable.presence_offline)
                .into(viewHolder.mImageViewCharacterThumbnail);

    }

    @Override
    public int getItemCount() {
        return getCursor().getCount();
    }
}