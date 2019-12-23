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

public class ReportCheckListAdapter extends RecyclerView.Adapter<ReportCheckListAdapter.ReportViewHolder> /*implements ItemMoveCallBack.ItemTouchHelperContract*/{
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
    public ReportCheckListAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
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
    public void onBindViewHolder(ReportCheckListAdapter.ReportViewHolder reportViewHolder, int i) {
        int position = reportViewHolder.getAdapterPosition();
        final ReportItems repo = reportItems.get(position);

        reportViewHolder.itemsListViewHolder(reportViewHolder, position, repo);

        Log.i(TAG, "onBindViewHolder invoked" + position);
    }

    public int getItemViewType(int i) {
        if (isPositionHeader(i))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        // Position Header
        return position == 0 ||
                position == 30 ||
                position == 45 ||
                position == 103 || //i == 0 || i == 21 || i == 30
                position == 122 || //i == 0 || i == 21 || i == 30
                position == 133; //i == 0 || i == 21 || i == 30
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitleList, tvDescription;
        ImageView takePhoto, resultPhoto, check, checkImage, note, checkNote, resetItem;
        View rowView;

        RadioGroup mRadioGroup;
        RadioButton mRadioButtonConform, mRadioButtonNotApplicable, mRadioButtonNotConform;
        RelativeLayout buttonLayoutArrow;
        private LinearLayout expandableLayout;

        private ReportViewHolder(View view) {
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

        private void itemsListViewHolder(ReportViewHolder reportViewHolder, int position, ReportItems repo) {
            //Header
            if (reportViewHolder instanceof HeaderVh) {
                ((HeaderVh) reportViewHolder).headerTitle.setText(repo.getTitle());

            } else if (reportViewHolder instanceof ItemVh) {
                ((ItemVh) reportViewHolder).tvTitleList.setText(repo.getTitle());

                reportViewHolder.setIsRecyclable(false);
                tvTitleList.setText(reportItems.get(position).getTitle());
                tvDescription.setText(reportItems.get(position).getDescription());

                final boolean isExpanded = expandState.get(position);
                expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

                buttonLayoutArrow.setRotation(expandState.get(position) ? 180f : 0f);
                buttonLayoutArrow.setOnClickListener(v ->
                        onClickButton(expandableLayout, buttonLayoutArrow, position));

                this.clickItemsListener(position);

                this.answersItems(repo);
            }
        }

        private void clickItemsListener(int position) {
            tvTitleList.setOnClickListener(view ->
                    onItemListenerClicked.updateList(position));

            //((ItemVh) reportViewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
            tvTitleList.setOnLongClickListener(view -> {
                onItemListenerClicked.removeItem(position);
                return false;
            });

            note.setOnClickListener(view ->
                    onItemListenerClicked.insertNote(position));

            takePhoto.setOnClickListener(view ->
                    onItemListenerClicked.takePhoto(position));

            resultPhoto.setOnClickListener(view ->
                    onItemListenerClicked.fullPhoto(position));

            resetItem.setOnClickListener(view ->
                    onItemListenerClicked.resetItem(position));
        }

        private void answersItems(ReportItems repo) {
            if (repo.getPhotoId() == null){
                resultPhoto.setImageAlpha(R.drawable.image);
                check.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
                resetItem.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
            } else {
                mRadioButtonConform.setChecked(true);
                /*reportViewHolder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));*/
                Glide.with(context).load(repo.getPhotoId()).centerCrop().into(resultPhoto);
                Log.i("LOG", "ImagePath3 " + repo.getPhotoId());
            }

            if (repo.getNote() == null || repo.getNote().isEmpty()){
                note.setImageAlpha(R.drawable.ic_action_note);
                /*reportViewHolder.checkNote.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));*/
            } else {
                note.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                /*reportViewHolder.checkNote.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));*/
            }

            mRadioButtonConform.setChecked(repo.isOpt1());
            mRadioButtonNotApplicable.setChecked(repo.isOpt2());
            mRadioButtonNotConform.setChecked(repo.isOpt3());

            if (mRadioButtonConform.isChecked()){
                resultPhoto.setBackgroundResource(R.color.colorRadioC);
                check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioC));
                resetItem.clearColorFilter();

            }
            if (mRadioButtonNotApplicable.isChecked()) {
                /*reportViewHolder.resultPhoto.setBackgroundResource(R.color.colorRadioNA);*/
                check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioNA));
                resetItem.clearColorFilter();
            }

            else if (mRadioButtonNotConform.isChecked()) {
                resultPhoto.setBackgroundResource(R.color.colorRadioNC);
                check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioNC));
                resetItem.clearColorFilter();
            }
        }
    }

    //Header
    class HeaderVh extends ReportViewHolder {

        @BindView(R.id.header_id)
        public TextView headerTitle;

        private HeaderVh(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ItemVh extends ReportViewHolder implements View.OnClickListener {

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
    public void onRowSelected(ReportViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRowSelected));
    }

    @Override
    public void onRowClear(ReportViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }*/
    // End ItemMoveCallBack
}
