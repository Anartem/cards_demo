package com.projects.anartem.cards.screens.selectiontracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import androidx.recyclerview.selection.ItemDetailsLookup;

public class IdDetails extends ItemDetailsLookup.ItemDetails<Long> {
    private final int mPosition;
    private final Long mId;

    public IdDetails(int position, Long id) {
        mPosition = position;
        mId = id;
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public Long getSelectionKey() {
        return mId;
    }

    @Override
    public boolean inSelectionHotspot(@NonNull final MotionEvent e) {
        return true;
    }
}
