package com.projects.anartem.cards.screens.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public class CardItemAnimator extends DefaultItemAnimator {

    public enum Action {
        CARD_CLICKED,
        CARD_SELECTED
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull final RecyclerView.State state,
                                                     @NonNull final RecyclerView.ViewHolder viewHolder,
                                                     final int changeFlags,
                                                     @NonNull final List<Object> payloads) {
        if (changeFlags == FLAG_CHANGED) {
            for (Object payload : payloads) {
                if (payload instanceof Action) {
                    return new CardHolderInfo((Action) payload);
                }
            }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @Override
    public boolean animateChange(@NonNull final RecyclerView.ViewHolder oldHolder,
                                 @NonNull final RecyclerView.ViewHolder newHolder,
                                 @NonNull final ItemHolderInfo preInfo,
                                 @NonNull final ItemHolderInfo postInfo) {
        if (preInfo instanceof CardHolderInfo) {
            CardHolderInfo cardPreInfo = (CardHolderInfo) preInfo;
            CardHolder holder = (CardHolder) newHolder;

            switch (cardPreInfo.mAction) {
                case CARD_CLICKED:
                    animateClicked(holder);
                    break;

                case CARD_SELECTED:
                    animateSelected(holder);
                    break;
            }

            return true;
        }
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    private void animateClicked(CardHolder holder) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleImage = ObjectAnimator.ofPropertyValuesHolder(
                holder.mImageView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.5f, 1.5f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.5f, 1.5f, 1.0f));

        ObjectAnimator colorTitle = ObjectAnimator.ofInt(
                holder.mTitleView,
                "textColor",
                Color.BLACK,
                Color.WHITE,
                Color.WHITE,
                Color.BLACK);
        colorTitle.setEvaluator(new ArgbEvaluator());

        colorTitle.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(final Animator animation) {
                dispatchChangeStarting(holder, true);
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                holder.mTitleView.setTextColor(Color.BLACK);
                holder.mImageView.setScaleX(1);
                holder.mImageView.setScaleY(1);
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                dispatchChangeFinished(holder, true);

                if (!isRunning()) {
                    dispatchAnimationsFinished();
                }
            }
        });

        animatorSet.playTogether(scaleImage, colorTitle);
        animatorSet.start();
    }

    private void animateSelected(CardHolder holder) {
    }

    public static class CardHolderInfo extends ItemHolderInfo {
        private final Action mAction;

        CardHolderInfo(final Action action) {
            mAction = action;
        }
    }
}
