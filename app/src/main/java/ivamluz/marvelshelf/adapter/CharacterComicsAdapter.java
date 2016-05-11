package ivamluz.marvelshelf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.marvelapiclient.model.ComicDto;
import com.karumi.marvelapiclient.model.MarvelImage;
import com.squareup.picasso.Picasso;

import java.util.List;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;

/**
 * Created by iluz on 5/3/16.
 * <p/>
 * Credits: https://gist.github.com/skyfishjy/443b7448f59be978bc59#file-mylistcursoradapter-java
 */
public class CharacterComicsAdapter extends RecyclerView.Adapter<CharacterComicsAdapter.ViewHolder> {
    Picasso mPicasso;

    private List<ComicDto> mComics;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageViewThumbnail;
        public TextView mTxtViewTitle;

//        private OnItemClickListener mOnItemClickListener;


        public ViewHolder(View view) {
            super(view);
//            ButterKnife.setDebug(true);
//            ButterKnife.bind(this, view);
            mImageViewThumbnail = (ImageView) view.findViewById(R.id.image_comic_item_thumb);
            mTxtViewTitle = (TextView) view.findViewById(R.id.text_comic_item_name);

//            view.setOnClickListener(this);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CharacterComicsAdapter(List<ComicDto> comics) {
        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        mComics = comics;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CharacterComicsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_character_comic, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ComicDto comic = mComics.get(position);

        if (!comic.getImages().isEmpty()) {
            MarvelImage image = comic.getImages().get(0);
            String thumbnailUrl = image.getPath() + "." + image.getExtension();
            mPicasso.load(thumbnailUrl)
                    .placeholder(R.drawable.character_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.character_placeholder)
                    .into(holder.mImageViewThumbnail);
        }

        holder.mTxtViewTitle.setText(comic.getTitle());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mComics.size();
    }
}