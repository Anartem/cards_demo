package com.projects.anartem.cards.screens.adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.anartem.cards.R;
import com.projects.anartem.cards.models.Card;
import com.projects.anartem.cards.screens.selectiontracker.Selectable;
import com.projects.anartem.cards.screens.touch.CardTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.selection.SelectionTracker;

import static com.projects.anartem.cards.screens.adapter.CardItemAnimator.Action.CARD_CLICKED;

public class CardAdapter extends RecyclerView.Adapter<CardHolder> implements CardTouchListener, Selectable {

    private ArrayList<Card> mCards = new ArrayList<>();
    private LongSparseArray<Integer> mPositions = new LongSparseArray<>();
    private SelectionTracker<Long> mSelectionTracker;
    
    public CardAdapter() {}

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                      final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_card, parent,false);
        return new CardHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardHolder holder,
                                 final int position) {
        Card card = mCards.get(position);
        holder.bind(card, mSelectionTracker != null && mSelectionTracker.isSelected((long) card.getId()));
    }

    @Override
    public void onClick(final int position) {
        notifyItemChanged(position, CARD_CLICKED);
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
    public int getItemCount() {
        return mCards.size();
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

    public void setList(@NonNull ArrayList<Card> cards) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CardDiffCallback(mCards, cards));
        result.dispatchUpdatesTo(this);

        mCards = cards;
        setPositions();
    }

    private void setPositions() {
        mPositions.clear();
        int position = 0;
        for (Card card : mCards) {
            mPositions.put(card.getId(), position++);
        }
    }

    private static class CardDiffCallback extends DiffUtil.Callback {
        private List<Card> mOldList;
        private List<Card> mNewList;

        CardDiffCallback(@NonNull List<Card> oldList,
                         @NonNull List<Card> newList) {
            mOldList = oldList;
            mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(final int oldItemPosition,
                                       final int newItemPosition) {
            long oldId = mOldList.get(oldItemPosition).getId();
            long newId = mNewList.get(newItemPosition).getId();
            return oldId == newId;
        }

        @Override
        public boolean areContentsTheSame(final int oldItemPosition,
                                          final int newItemPosition) {
            Card oldCard = mOldList.get(oldItemPosition);
            Card newCard = mNewList.get(newItemPosition);
            return oldCard.equals(newCard);
        }
    }
}
