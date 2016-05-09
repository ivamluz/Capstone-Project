package ivamluz.marvelshelf.data.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import ivamluz.marvelshelf.data.MarvelShelfContract;

/**
 * Created by iluz on 5/8/16.
 */
public class MarvelCharacter implements Parcelable {
    private long mId;
    private String mName;
    private String mDescription;
    private String mThumbnailUrl;
    private String mDetailsUrl;
    private String mModified;

    public MarvelCharacter() {
        super();
    }

    public static MarvelCharacter fromCursor(Cursor cursor) {
        MarvelCharacter character = new MarvelCharacter();
        character.setId(cursor.getLong(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_KEY)));
        character.setName(cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_NAME)));
        character.setDescription(cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_DESCRIPTION)));
        character.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_THUMBNAIL)));
        character.setDetailsUrl(cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_DETAILS_URL)));
        character.setModified(cursor.getString(cursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_MODIFIED)));

        return character;
    }

    protected MarvelCharacter(Parcel parcel) {
        mId = parcel.readLong();
        mName = parcel.readString();
        mDescription = parcel.readString();
        mThumbnailUrl = parcel.readString();
        mDetailsUrl = parcel.readString();
        mModified = parcel.readString();
    }

    public static final Creator<MarvelCharacter> CREATOR = new Creator<MarvelCharacter>() {
        @Override
        public MarvelCharacter createFromParcel(Parcel in) {
            return new MarvelCharacter(in);
        }

        @Override
        public MarvelCharacter[] newArray(int size) {
            return new MarvelCharacter[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeString(mName);
        parcel.writeString(mDescription);
        parcel.writeString(mThumbnailUrl);
        parcel.writeString(mDetailsUrl);
        parcel.writeString(mModified);
    }

    public long getId() {
        return mId;
    }

    public MarvelCharacter setId(long id) {
        this.mId = id;
        return this;
    }

    public String getName() {
        return mName;
    }

    public MarvelCharacter setName(String name) {
        mName = name;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public MarvelCharacter setDescription(String description) {
        mDescription = description;
        return this;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public MarvelCharacter setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
        return this;
    }

    public String getDetailsUrl() {
        return mDetailsUrl;
    }

    public MarvelCharacter setDetailsUrl(String detailsUrl) {
        mDetailsUrl = detailsUrl;
        return this;
    }

    public String getModified() {
        return mModified;
    }

    public MarvelCharacter setModified(String modified) {
        mModified = modified;
        return this;
    }
}
