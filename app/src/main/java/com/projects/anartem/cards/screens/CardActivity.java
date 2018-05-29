package com.projects.anartem.cards.screens;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.projects.anartem.cards.R;
import com.projects.anartem.cards.models.Card;
import com.projects.anartem.cards.screens.adapter.CardAdapter;
import com.projects.anartem.cards.screens.adapter.CardItemAnimator;
import com.projects.anartem.cards.screens.adapter.CardListAdapter;
import com.projects.anartem.cards.screens.selectiontracker.IdLookup;
import com.projects.anartem.cards.screens.selectiontracker.IdProvider;
import com.projects.anartem.cards.screens.selectiontracker.Selectable;
import com.projects.anartem.cards.screens.touch.CardTouchCallback;
import com.projects.anartem.cards.screens.touch.CardTouchListener;
import com.projects.anartem.cards.widgets.GridRecyclerManager;
import com.projects.anartem.cards.widgets.itemdecorations.SelectionItemDecoration;
import com.projects.anartem.cards.widgets.itemdecorations.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Collections;

import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CardActivity extends AppCompatActivity {
    @BindView(R.id.noir_recycler_view)
    RecyclerView mRecyclerView;

    private CardAdapter mAdapter;
    private CardListAdapter mListAdapter;
    private SelectionTracker<Long> mTracker;

    private static final int COLUMN_COUNT = 5;

    private static final boolean USE_LIST_ADAPTER = false;
    private static final boolean USE_TRACKER = true;
    private static final boolean USE_CUSTOM_MANAGER = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ButterKnife.bind(this);
        
        initRecyclerView();

        if (USE_LIST_ADAPTER) {
            initListAdapter();
        } else {
            initAdapter();
        }
    }

    @SuppressWarnings("DesignForExtension")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.action_clear).setVisible(USE_TRACKER);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                if (USE_LIST_ADAPTER) {
                    mListAdapter.submitList(getList());
                } else {
                    mAdapter.setList(getList());
                }
                break;

            case R.id.action_clear:
                if (USE_TRACKER) {
                    mTracker.clearSelection();
                }
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState, final PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mTracker.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTracker.onRestoreInstanceState(savedInstanceState);
    }

    private void initRecyclerView() {
        if (USE_CUSTOM_MANAGER) {
            GridRecyclerManager manager = new GridRecyclerManager(COLUMN_COUNT);
            mRecyclerView.setLayoutManager(manager);
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new CardItemAnimator());

        int offset = getResources().getDimensionPixelOffset(R.dimen.offset_size);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(offset));
        mRecyclerView.addItemDecoration(new SelectionItemDecoration(this, R.drawable.ic_check));
    }

    private void initAdapter() {
        ArrayList<Card> list = getList();

        mAdapter = new CardAdapter();
        mAdapter.setHasStableIds(true);
        mAdapter.setList(list);

        mRecyclerView.setAdapter(mAdapter);

        if (USE_TRACKER) {
            initTracker(mAdapter);
        } else {
            initTouchHelper(mAdapter);
        }
    }

    private void initListAdapter() {
        ArrayList<Card> list = getList();

        mListAdapter = new CardListAdapter();
        mListAdapter.setHasStableIds(true);
        mListAdapter.submitList(list);

        mRecyclerView.setAdapter(mListAdapter);

        if (USE_TRACKER) {
            initTracker(mListAdapter);
        } else {
            initTouchHelper(mListAdapter);
        }
    }

    private void initTouchHelper(CardTouchListener listener) {
        CardTouchCallback callback = new CardTouchCallback(listener);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);
    }
    
    private void initTracker(Selectable listener) {
        ItemKeyProvider<Long> idProvider = USE_CUSTOM_MANAGER ?
                new IdProvider(mRecyclerView) :
                new StableIdKeyProvider(mRecyclerView);

        mTracker = new SelectionTracker.Builder<>(
                    "CARD_SELECTED",
                    mRecyclerView,
                    idProvider,
                    new IdLookup(mRecyclerView),
                    StorageStrategy.createLongStorage())
                .build();

        listener.setSelectionTracker(mTracker);
    }
    
    private ArrayList<Card> getList() {
        ArrayList<Card> list = new ArrayList<>();
        String[] names = getResources().getStringArray(R.array.card_names);

        for (int i = 0; i < COLUMN_COUNT * 6; i++) {
            int image = getResources().getIdentifier(
                    "ic_avatar" + (i + 1),
                    "drawable",
                    getPackageName());

            list.add(new Card()
                    .setId(i)
                    .setImage(image)
                    .setTitle(names[i]));
        }

        Collections.shuffle(list);

        return list;
    }
}
