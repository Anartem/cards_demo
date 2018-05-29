package com.projects.anartem.cards.models;

public class Card {
    private long mId;
    private int mImage;
    private String mTitle;

    public int getImage() {
        return mImage;
    }

    public Card setImage(final int image) {
        mImage = image;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Card setTitle(final String title) {
        mTitle = title;
        return this;
    }

    public long getId() {
        return mId;
    }

    public Card setId(final long id) {
        mId = id;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Card card = (Card) o;

        if (mId != card.mId) return false;
        if (mImage != card.mImage) return false;
        return mTitle != null ? mTitle.equals(card.mTitle) : card.mTitle == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + mImage;
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        return result;
    }
}
