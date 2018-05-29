package com.projects.anartem.cards.screens.touch;

public interface CardTouchListener {
    void onClick(int position);
    void onMove(int from, int to);
    void onSwiped(int position);
}
