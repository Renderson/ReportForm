package com.rendersoncs.reportform.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.listener.OnItemListenerClicked;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.volley.VolleyLog.TAG;

public class ReportCheckListAdapter extends RecyclerView.Adapter<ReportCheckListAdapter.ViewHolder> /*implements ItemMoveCallBack.ItemTouchHelperContract*/{
    private List<ReportItems> reportItems;
    public SparseBooleanArray expandState = new SparseBooleanArray();
    private Context context;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private OnItemListenerClicked onItemListenerClicked;

    public void setOnItemListenerClicked(OnItemListenerClicked onItemListenerClicked) {
        this.onItemListenerClicked = onItemListenerClicked;
    }

    public ReportCheckListAdapter(List<ReportItems> reportItems, Context context) {
        this.context = context;
        this.reportItems = reportItems;
        for (int i = 0; i < reportItems.size(); i++) {
            expandState.append(i, false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        if (i == TYPE_HEADER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout, viewGroup, false);
            return new HeaderVh(view);
        } else if (i == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_report_list, viewGroup, false);
            return new ItemVh(view);
        }
        throw new RuntimeException("No macth for" + i + ".");
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        final ReportItems repo = reportItems.get(position);

        this.itemsListViewHolder(viewHolder, position, repo);

        Log.i(TAG, "onBindViewHolder invoked" + position);
    }

    private void itemsListViewHolder(@NonNull ViewHolder viewHolder, int position, ReportItems repo) {
        //Header
        if (viewHolder instanceof HeaderVh) {
            ((HeaderVh) viewHolder).headerTitle.setText(repo.getTitle());

        } else if (viewHolder instanceof ItemVh) {
            ((ItemVh) viewHolder).tvTitleList.setText(repo.getTitle());

            viewHolder.setIsRecyclable(false);
            viewHolder.tvTitleList.setText(reportItems.get(position).getTitle());
            viewHolder.tvDescription.setText(reportItems.get(position).getDescription());

            final boolean isExpanded = expandState.get(position);
            viewHolder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            viewHolder.buttonLayoutArrow.setRotation(expandState.get(position) ? 180f : 0f);
            viewHolder.buttonLayoutArrow.setOnClickListener(v ->
                    onClickButton(viewHolder.expandableLayout, viewHolder.buttonLayoutArrow, position));

            viewHolder.tvTitleList.setOnClickListener(view ->
                    onItemListenerClicked.updateList(position));

            //((ItemVh) viewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
            viewHolder.tvTitleList.setOnLongClickListener(view -> {
                onItemListenerClicked.removeItem(position);
                return false;
            });

            viewHolder.note.setOnClickListener(view ->
                    onItemListenerClicked.insertNote(position));

            viewHolder.takePhoto.setOnClickListener(view ->
                    onItemListenerClicked.takePhoto(position));

            viewHolder.resultPhoto.setOnClickListener(view ->
                    onItemListenerClicked.fullPhoto(position));

            viewHolder.resetItem.setOnClickListener(view ->
                    onItemListenerClicked.resetItem(position));

            if (repo.getPhotoId() == null){
                viewHolder.resultPhoto.setImageAlpha(R.drawable.image);
                viewHolder.check.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
                viewHolder.resetItem.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
            } else {
                viewHolder.mRadioButtonConform.setChecked(true);
                /*viewHolder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));*/
                Glide.with(context).load(repo.getPhotoId()).centerCrop().into(viewHolder.resultPhoto);
                Log.i("LOG", "ImagePath3 " + repo.getPhotoId());
            }

            if (repo.getNote() == null || repo.getNote().isEmpty()){
                viewHolder.note.setImageAlpha(R.drawable.ic_action_note);
                /*viewHolder.checkNote.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));*/
            } else {
                viewHolder.note.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                /*viewHolder.checkNote.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));*/
            }

            viewHolder.mRadioButtonConform.setChecked(repo.isOpt1());
            viewHolder.mRadioButtonNotApplicable.setChecked(repo.isOpt2());
            viewHolder.mRadioButtonNotConform.setChecked(repo.isOpt3());

            if (viewHolder.mRadioButtonConform.isChecked()){
                viewHolder.resultPhoto.setBackgroundResource(R.color.colorRadioC);
                viewHolder.check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioC));
                viewHolder.resetItem.clearColorFilter();

            } if (viewHolder.mRadioButtonNotApplicable.isChecked()) {
                /*viewHolder.resultPhoto.setBackgroundResource(R.color.colorRadioNA);*/
                viewHolder.check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioNA));
                viewHolder.resetItem.clearColorFilter();
            }

            else if (viewHolder.mRadioButtonNotConform.isChecked()) {
                viewHolder.resultPhoto.setBackgroundResource(R.color.colorRadioNC);
                viewHolder.check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioNC));
                viewHolder.resetItem.clearColorFilter();
            }
        }
    }

    public int getItemViewType(int i) {
        if (isPositionHeader(i))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        // Position Header
        return position == -1;
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitleList, tvDescription;
        ImageView takePhoto, resultPhoto, check, checkImage, note, checkNote, resetItem;
        View rowView;

        RadioGroup mRadioGroup;
        RadioButton mRadioButtonConform, mRadioButtonNotApplicable, mRadioButtonNotConform;
        RelativeLayout buttonLayoutArrow;
        private LinearLayout expandableLayout;

        private ViewHolder(View view) {
            super(view);

            rowView = view;
            tvTitleList = view.findViewById(R.id.textView_title);
            tvDescription = view.findViewById(R.id.textView_subTitle);
            takePhoto = view.findViewById(R.id.photo);
            resultPhoto = view.findViewById(R.id.result_photo);
            check = view.findViewById(R.id.action_check);
            note = view.findViewById(R.id.note);
            resetItem = view.findViewById(R.id.action_reset_item);
            /*checkNote = view.findViewById(R.id.action_note);
            checkImage = view.findViewById(R.id.action_image);*/

            buttonLayoutArrow = view.findViewById(R.id.btnArrow);
            expandableLayout = view.findViewById(R.id.expandableLayout);
        }
    }

    //Header
    class HeaderVh extends ReportCheckListAdapter.ViewHolder {

        @BindView(R.id.header_id)
        public TextView headerTitle;

        private HeaderVh(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ItemVh extends ReportCheckListAdapter.ViewHolder implements View.OnClickListener {

        @BindView(R.id.textView_title)
        public TextView itemContent;

        private ItemVh(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mRadioGroup = itemView.findViewById(R.id.radio_group);
            mRadioButtonConform = itemView.findViewById(R.id.radio_conform);
            mRadioButtonNotApplicable = itemView.findViewById(R.id.radio_not_applicable);
            mRadioButtonNotConform = itemView.findViewById(R.id.radio_not_conform);

            tvTitleList.setOnClickListener(this);
            takePhoto.setOnClickListener(this);

            mRadioButtonConform.setOnClickListener(this);
            mRadioButtonNotApplicable.setOnClickListener(this);
            mRadioButtonNotConform.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.radio_conform:
                    if (onItemListenerClicked != null)
                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), ReportConstants.ITEM.OPT_NUM1);
                    break;

                case R.id.radio_not_applicable:
                    if (onItemListenerClicked != null)
                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), ReportConstants.ITEM.OPT_NUM2);
                    break;

                case R.id.radio_not_conform:
                    if (onItemListenerClicked != null)
                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), ReportConstants.ITEM.OPT_NUM3);
                    break;
            }
        }
    }

    private void onClickButton(final LinearLayout expandableLayout, final RelativeLayout buttonLayout, final int i) {

        //Expand CardView
        if (expandableLayout.getVisibility() == View.VISIBLE) {
            createRotateAnimator(buttonLayout, 180f, 0f).start();
            expandableLayout.setVisibility(View.GONE);
            expandState.put(i, false);
        } else {
            createRotateAnimator(buttonLayout, 0f, 180f).start();
            expandableLayout.setVisibility(View.VISIBLE);
            expandState.put(i, true);
        }

    }

    //Animation Expand
    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;

    }

    // Set result image
    public void setImageInItem(int position, Bitmap imageSrc) {
        ReportItems dataSet = reportItems.get(position);
        dataSet.setPhotoId(imageSrc);
        Log.i("LOG", "ImagePath2 " + imageSrc);
        notifyDataSetChanged();
    }

    // Set result note
    public void insertNote(int position, String note){
        ReportItems dataSet = reportItems.get(position);
        dataSet.setNote(note);
        Log.i("LOG", "Note " + note);
        notifyDataSetChanged();
    }

    // ItemMoveCallBack
    /*@Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition){
            for (int i = fromPosition; i < toPosition; i++){
                Collections.swap(reportItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--){
                Collections.swap(reportItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

        new Thread((Runnable) () ->{
            ReportItems item1 = reportItems.get(fromPosition);
            ReportItems item2 = reportItems.get(toPosition);

            item1.setPosition(toPosition);
            item2.setPosition(toPosition);

        }).start();
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRowSelected));
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }*/
    // End ItemMoveCallBack
}
