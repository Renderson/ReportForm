package com.rendersoncs.reportform.util;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
    public View emptyView;
    public View floatingActionButton;
    private RecyclerView recyclerView;

    public RVEmptyObserver(RecyclerView rv, View ev, View fb){
        this.recyclerView = rv;
        this.emptyView = ev;
        this.floatingActionButton = fb;
        checkEmpty();
    }

    private void checkEmpty() {
        if (emptyView != null && recyclerView.getAdapter() != null){
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }

        if (floatingActionButton != null && recyclerView.getAdapter() != null){
            boolean emptyViewVisible5 = recyclerView.getAdapter().getItemCount() == 0;
            floatingActionButton.setVisibility(emptyViewVisible5 ? View.GONE : View.VISIBLE);
            recyclerView.setVisibility(emptyViewVisible5 ? View.GONE : View.VISIBLE);
        }
    }

    public void onChanged(){ checkEmpty(); }
    public void onItemRangeInserted(int positionStart, int itemCount) { checkEmpty(); }
    public void onItemRangeRemoved(int positionStart, int itemCount) { checkEmpty(); }
}
