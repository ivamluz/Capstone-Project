package ivamluz.marvelshelf.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import ivamluz.marvelshelf.adapter.MarvelCharactersCursorAdapter;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.MarvelShelfProvider;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AllCharactersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllCharactersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int sMarvelCharacterLoader = 0;

//    @BindView(R.id.recycler_view_all_characters)
    private RecyclerView mAllCharactersRecyclerView;

    private MarvelCharactersCursorAdapter mAdapter;
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
//        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mAllCharactersRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_all_characters);
        mAllCharactersRecyclerView.setLayoutManager(mLayoutManager);
        mAllCharactersRecyclerView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(sMarvelCharacterLoader, null, this);

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
        mAdapter = new MarvelCharactersCursorAdapter(getContext(), cursor);
        mAllCharactersRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
