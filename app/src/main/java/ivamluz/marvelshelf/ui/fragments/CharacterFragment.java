package ivamluz.marvelshelf.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ivamluz.marvelshelf.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CharacterFragment extends Fragment {

    public CharacterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_character, container, false);
    }
}
