package com.projects.anartem.cards.screens.selectiontracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.projects.anartem.cards.screens.adapter.CardHolder;

import androidx.recyclerview.selection.ItemDetailsLookup;

public class IdLookup extends ItemDetailsLookup<Long> {
    private final RecyclerView mRecyclerView;
    
    public IdLookup(final RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }
    
    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull final MotionEvent e) {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

        if (view != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);

            if (holder instanceof CardHolder) {
                return ((CardHolder) holder).getDetails();
            }
        }
        
        return null;
    }
}
