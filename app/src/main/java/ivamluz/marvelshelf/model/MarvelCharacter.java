package ivamluz.marvelshelf.model;

import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by iluz on 5/3/16.
 */
public class MarvelCharacter {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MarvelCharacter fromCursor(Cursor cursor) {
        MarvelCharacter character = new MarvelCharacter();
        character.setName(cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

        //TODO return your MyListItem from cursor.
        return character;
    }
}
