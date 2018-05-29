package com.projects.anartem.cards.screens.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.projects.anartem.cards.R;
import com.projects.anartem.cards.models.Card;
import com.projects.anartem.cards.screens.selectiontracker.IdDetails;
import com.projects.anartem.cards.screens.touch.CardTouchListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.card_image)
    ImageView mImageView;

    @BindView(R.id.card_title)
    TextView mTitleView;

    private final CardTouchListener mListener;

    public CardHolder(final View view, @NonNull final CardTouchListener listener) {
        super(view);
        ButterKnife.bind(this, view);

        mListener = listener;
    }

    public void bind(final @NonNull Card card,
                     final boolean isSelected) {
        itemView.setActivated(isSelected);

        itemView.setScaleX(isSelected ? 0.9f : 1.0f);
        itemView.setScaleY(isSelected ? 0.9f : 1.0f);

        mTitleView.setText(card.getTitle());
        Glide.with(itemView)
                .load(card.getImage())
                .into(mImageView);
    }

    @OnClick(R.id.card_layout)
    public void onClick() {
        mListener.onClick(getAdapterPosition());
    }

    public IdDetails getDetails() {
        return new IdDetails(getAdapterPosition(), getItemId());
    }
}
