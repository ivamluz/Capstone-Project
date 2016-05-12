package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.CharactersCursorAdapter;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.ui.activities.CharacterDetailsActivity;
import ivamluz.marvelshelf.ui.decorators.MarginItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AllCharactersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllCharactersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CharactersCursorAdapter.OnItemClickListener {
    private static final int CHARACTERS_LOADER = 0;

    @BindView(R.id.recycler_view_all_characters)
    private RecyclerView mAllCharactersRecyclerView;

    private CharactersCursorAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public AllCharactersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AllCharactersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllCharactersFragment newInstance() {
        AllCharactersFragment fragment = new AllCharactersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_characters, container, false);
        ButterKnife.bind(this, rootView);

        mLayoutManager = new LinearLayoutManager(getContext());
        mAllCharactersRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_all_characters);
        mAllCharactersRecyclerView.setLayoutManager(mLayoutManager);
        mAllCharactersRecyclerView.setAdapter(mAdapter);

        int marginBottom = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mAllCharactersRecyclerView.addItemDecoration(new MarginItemDecoration(0, 0, marginBottom, 0));

        getActivity().getSupportLoaderManager().initLoader(CHARACTERS_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri CHARACTER_URI = MarvelShelfContract.CharacterEntry.CONTENT_URI;
        return new CursorLoader(getContext(), CHARACTER_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        cursor.moveToFirst();

        mAdapter = new CharactersCursorAdapter(getContext(), cursor);
        mAdapter.setOnItemClickListener(this);

        mAllCharactersRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onItemClick(int position, View v) {
//        Toast.makeText(v.getContext(), "ID: " + mAdapter.getItemId(position), Toast.LENGTH_SHORT).show();

        mAdapter.getCursor().moveToPosition(position);

        MarvelCharacter character = MarvelCharacter.fromCursor(mAdapter.getCursor());


        Intent intent = CharacterDetailsActivity.newIntent(getContext(), character);
        getContext().startActivity(intent);
    }
}
