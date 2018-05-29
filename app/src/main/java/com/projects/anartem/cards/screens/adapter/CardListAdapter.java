package com.projects.anartem.cards.screens.adapter;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.anartem.cards.R;
import com.projects.anartem.cards.models.Card;
import com.projects.anartem.cards.screens.selectiontracker.Selectable;
import com.projects.anartem.cards.screens.touch.CardTouchListener;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.selection.SelectionTracker;

import static com.projects.anartem.cards.screens.adapter.CardItemAnimator.Action.CARD_CLICKED;

public class CardListAdapter extends ListAdapter<Card, CardHolder> implements CardTouchListener, Selectable {
    private List<Card> mCards;
    private LongSparseArray<Integer> mPositions = new LongSparseArray<>();
    private SelectionTracker<Long> mSelectionTracker;

    public CardListAdapter() {
        super(new CardDiffCallback());
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_card, parent, false);
        return new CardHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        Card card = mCards.get(position);
        holder.bind(card, mSelectionTracker.isSelected((long) card.getId()));
    }

    @Override
    public long getItemId(final int position) {
        return mCards.get(position).getId();
    }

    @Override
    public int getItemPosition(final long id) {
        return mPositions.get(id);
    }

    @Override
    public void onClick(final int position) {
        notifyItemChanged(position, CARD_CLICKED);
    }

    @Override
    public void onMove(int from, int to) {
        for (int i = from; i < to; i++) {
            Collections.swap(mCards, i, i + 1);
        }
        setPositions();

        notifyItemMoved(from, to);
    }

    @Override
    public void onSwiped(final int position) {
        mCards.remove(position);
        setPositions();

        notifyItemRemoved(position);
    }

    @Override
    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        mSelectionTracker = selectionTracker;
    }

    @Override
    public void submitList(final List<Card> list) {
        mCards = list;
        setPositions();
        super.submitList(list);
    }

    private void setPositions() {
        mPositions.clear();
        int position = 0;
        for (Card card : mCards) {
            mPositions.put(card.getId(), position++);
        }
    }

    private static class CardDiffCallback extends DiffUtil.ItemCallback<Card> {
        @Override
        public boolean areItemsTheSame(@NonNull Card oldItem, @NonNull Card newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Card oldItem, @NonNull Card newItem) {
            return oldItem.equals(newItem);
        }
    }
}
