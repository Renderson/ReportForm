package com.rendersoncs.reportform.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.rendersoncs.reportform.adapter.checkListAdapter.ReportRecyclerView;

public class ItemMoveCallBack extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallBack(ItemTouchHelperContract adapter) {
        this.mAdapter = adapter;
    }

    public boolean isLongPressDragEnabled() {
        return true;
    }

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ReportRecyclerView.ReportViewHolder) {
                ReportRecyclerView.ReportViewHolder myReportViewHolder = (ReportRecyclerView.ReportViewHolder) viewHolder;
                mAdapter.onRowSelected(myReportViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof ReportRecyclerView.ReportViewHolder) {
            ReportRecyclerView.ReportViewHolder myReportViewHolder = (ReportRecyclerView.ReportViewHolder) viewHolder;
            mAdapter.onRowClear(myReportViewHolder);
        }
    }

    public interface ItemTouchHelperContract {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(ReportRecyclerView.ReportViewHolder myReportViewHolder);

        void onRowClear(ReportRecyclerView.ReportViewHolder myReportViewHolder);
    }
}
