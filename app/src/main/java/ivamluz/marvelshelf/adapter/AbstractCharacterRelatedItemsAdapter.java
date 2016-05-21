package ivamluz.marvelshelf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.marvelapiclient.model.MarvelImage;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/3/16.
 * <p/>
 */
public abstract class AbstractCharacterRelatedItemsAdapter<T> extends RecyclerView.Adapter<AbstractCharacterRelatedItemsAdapter.ViewHolder> {
    private static final String LOG_TAG = AbstractCharacterRelatedItemsAdapter.class.getSimpleName();
    Picasso mPicasso;

    protected List<T> mItems;

    private Class<T> mType;

    private OnItemClickListener mOnItemClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public Class mType;

        @BindView(R.id.image_item_thumb)
        public ImageView mImageViewThumbnail;

        @BindView(R.id.text_item_title)
        public TextView mTxtViewTitle;

        private OnItemClickListener mOnItemClickListener;


        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mType, getAdapterPosition(), view);
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AbstractCharacterRelatedItemsAdapter(Class<T> type, List<T> items) {
        mType = type;
        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        setItems(items);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AbstractCharacterRelatedItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_character_related_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        vh.setOnItemClickListener(mOnItemClickListener);

        return vh;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mType = mType;
    }

    protected void setThumbnail(ViewHolder holder, MarvelImage image) {
        String thumbnailUrl = image.getPath() + "." + image.getExtension();
        mPicasso.load(thumbnailUrl)
                .placeholder(R.drawable.character_placeholder)
                .fit()
                .centerCrop()
                .error(R.drawable.character_placeholder)
                .into(holder.mImageViewThumbnail);
    }

    protected void setTitle(ViewHolder holder, String title) {
        holder.mTxtViewTitle.setText(title);
    }

    public void setItems(List<T> items) {
        if (items == null) {
            mItems = new LinkedList<>();
        } else {
            mItems = items;
        }
    }

    public void addItems(List<T> items) {
        if (mItems == null) {
            mItems = new LinkedList<>();
        }

        mItems.addAll(items);
    }

    public void addItem(T item) {
        if (mItems == null) {
            mItems = new LinkedList<>();
        }

        mItems.add(item);
    }

    public T getItem(int position) {
        try {
            return mItems.get(position);
        } catch (Exception e) {
            MarvelShelfLogger.error(LOG_TAG, e);

            return null;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Class type, int position, View view);
    }
}