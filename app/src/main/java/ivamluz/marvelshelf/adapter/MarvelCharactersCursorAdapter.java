package ivamluz.marvelshelf.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.model.MarvelCharacter;

/**
 * Created by iluz on 5/3/16.
 * <p/>
 * Credits: https://gist.github.com/skyfishjy/443b7448f59be978bc59#file-mylistcursoradapter-java
 */
public class MarvelCharactersCursorAdapter extends AbstractCursorRecyclerViewAdapter<MarvelCharactersCursorAdapter.ViewHolder> {

    public MarvelCharactersCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.txt_view_contact_name);
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
        MarvelCharacter character = MarvelCharacter.fromCursor(cursor);
        viewHolder.mTextView.setText(character.getName());
    }

    @Override
    public int getItemCount() {
        return getCursor().getCount();
    }
}