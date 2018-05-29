package com.projects.anartem.cards.screens.selectiontracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import androidx.recyclerview.selection.ItemKeyProvider;

public class IdProvider extends ItemKeyProvider<Long> {
    private final Selectable mSelectable;

    public IdProvider(@NonNull RecyclerView recyclerView) {
        super(SCOPE_MAPPED);
        mSelectable = (Selectable) recyclerView.getAdapter();
    }

    @Override
    public @Nullable Long getKey(int position) {
        return mSelectable.getItemId(position);
    }

    @Override
    public int getPosition(@NonNull Long id) {
        return mSelectable.getItemPosition(id);
    }
}
