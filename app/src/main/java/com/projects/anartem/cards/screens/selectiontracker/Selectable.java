package com.projects.anartem.cards.screens.selectiontracker;

import androidx.recyclerview.selection.SelectionTracker;

public interface Selectable {
    void setSelectionTracker(SelectionTracker<Long> tracker);
    long getItemId(int position);
    int getItemPosition(long id);
}
