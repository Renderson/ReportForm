//package com.rendersoncs.reportform.viewHolder;
//
//import android.animation.ObjectAnimator;
//import android.content.Context;
//import android.util.Log;
//import android.util.SparseBooleanArray;
//import android.view.View;
//import android.view.animation.LinearInterpolator;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.rendersoncs.reportform.R;
//import com.rendersoncs.reportform.adapter.ReportCheckListAdapter;
//import com.rendersoncs.reportform.itens.ReportItems;
//import com.rendersoncs.reportform.listener.OnItemListenerClicked;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class ReportCheckListViewHolder extends RecyclerView.ViewHolder {
//
//    public SparseBooleanArray expandState = new SparseBooleanArray();
//    private TextView tvTitleList, tvDescription;
//    private ImageView takePhoto, resultPhoto, check, checkImage, note, checkNote;
//    private Context context;
//    private View rowView;
//
//    private RadioGroup mRadioGroup;
//    private RadioButton mRadioButtonConform, mRadioButtonNotApplicable, mRadioButtonNotConform;
//    private RelativeLayout buttonLayoutArrow;
//    private LinearLayout expandableLayout;
//
//    public ReportCheckListViewHolder(@NonNull View itemView, Context context) {
//        super(itemView);
//
//        rowView = itemView;
//        tvTitleList = itemView.findViewById(R.id.textView_title);
//        tvDescription = itemView.findViewById(R.id.textView_subTitle);
//        takePhoto = itemView.findViewById(R.id.photo);
//        resultPhoto = itemView.findViewById(R.id.result_photo);
//        check = itemView.findViewById(R.id.action_check);
//        //checkImage = view.findViewById(R.id.action_image);
//        note = itemView.findViewById(R.id.note);
//        //checkNote = view.findViewById(R.id.action_note);
//
//        buttonLayoutArrow = itemView.findViewById(R.id.btnArrow);
//        expandableLayout = itemView.findViewById(R.id.expandableLayout);
//
//        this.context = context;
//    }
//
//    public void bindData(@NonNull RecyclerView.ViewHolder viewHolder, int position, ReportItems repo, final OnItemListenerClicked onItemListenerClicked, SparseBooleanArray expandState) {
//        //Header
//        if (viewHolder instanceof ReportCheckListViewHolder.HeaderVh) {
//            ((ReportCheckListViewHolder.HeaderVh) viewHolder).headerTitle.setText(repo.getTitle());
//
//        } else if (viewHolder instanceof ReportCheckListViewHolder.ItemVh) {
//            ((ReportCheckListViewHolder.ItemVh) viewHolder).tvTitleList.setText(repo.getTitle());
//
//            viewHolder.setIsRecyclable(false);
//            tvTitleList.setText(reportItems.get(position).getTitle());
//            tvDescription.setText(reportItems.get(position).getDescription());
//
//            final boolean isExpanded = expandState.get(position);
//            expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//
//            buttonLayoutArrow.setRotation(expandState.get(position) ? 180f : 0f);
//            buttonLayoutArrow.setOnClickListener(v ->
//                    onClickButton(expandableLayout, buttonLayoutArrow, position));
//
//            tvTitleList.setOnClickListener(view ->
//                    onItemListenerClicked.updateList(position));
//
//            //((ItemVh) viewHolder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            tvTitleList.setOnLongClickListener(view -> {
//                onItemListenerClicked.removeItem(position);
//                return false;
//            });
//
//            note.setOnClickListener(view ->
//                    onItemListenerClicked.insertNote(position));
//
//            takePhoto.setOnClickListener(view ->
//                    onItemListenerClicked.takePhoto(position));
//
//            resultPhoto.setOnClickListener(view ->
//                    onItemListenerClicked.fullPhoto(position));
//
//            if (repo.getPhotoId() == null) {
//                resultPhoto.setImageAlpha(R.drawable.image);
//                check.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
//                //viewHolder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
//            } else {
//                mRadioButtonConform.setChecked(true);
//                //viewHolder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
//                Glide.with(context).load(repo.getPhotoId()).centerCrop().into(resultPhoto);
//                Log.i("LOG", "ImagePath3 " + repo.getPhotoId());
//            }
//
//            if (repo.getNote() == null || repo.getNote().isEmpty()) {
//                note.setImageAlpha(R.drawable.ic_action_note);
//                //viewHolder.checkNote.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite));
//            } else {
//                note.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
//                //viewHolder.checkNote.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
//            }
//
//            mRadioButtonConform.setChecked(repo.isOpt1());
//            mRadioButtonNotApplicable.setChecked(repo.isOpt2());
//            mRadioButtonNotConform.setChecked(repo.isOpt3());
//
//            if (mRadioButtonConform.isChecked()) {
//                //listIDRadio.add(1);
//                resultPhoto.setBackgroundResource(R.color.colorRadioC);
//                check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioC));
//
//            }
//            if (mRadioButtonNotApplicable.isChecked()) {
//                //listIDRadio.add(2);
//                resultPhoto.setBackgroundResource(R.color.colorRadioNA);
//                check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioNA));
//            } else if (mRadioButtonNotConform.isChecked()) {
//                //listIDRadio.add(3);
//                resultPhoto.setBackgroundResource(R.color.colorRadioNC);
//                check.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRadioNC));
//            }
//        }
//    }
//
//    //Header
//    public class HeaderVh extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.header_id)
//        public TextView headerTitle;
//
//        private HeaderVh(@NonNull View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//    public class ItemVh extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        @BindView(R.id.textView_title)
//        public TextView itemContent;
//
//        private ItemVh(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//            mRadioGroup = itemView.findViewById(R.id.radio_group);
//            mRadioButtonConform = itemView.findViewById(R.id.radio_conform);
//            mRadioButtonNotApplicable = itemView.findViewById(R.id.radio_not_applicable);
//            mRadioButtonNotConform = itemView.findViewById(R.id.radio_not_conform);
//
//            tvTitleList.setOnClickListener(this);
//            takePhoto.setOnClickListener(this);
//
//            mRadioButtonConform.setOnClickListener(this);
//            mRadioButtonNotApplicable.setOnClickListener(this);
//            mRadioButtonNotConform.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//
//            switch (v.getId()) {
//                case R.id.radio_conform:
//                    if (onItemListenerClicked != null)
//                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), 1);
//                    break;
//
//                case R.id.radio_not_applicable:
//                    if (onItemListenerClicked != null)
//                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), 2);
//                    break;
//
//                case R.id.radio_not_conform:
//                    if (onItemListenerClicked != null)
//                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), 3);
//                    break;
//            }
//        }
//    }
//
//    private void onClickButton(final LinearLayout expandableLayout, final RelativeLayout buttonLayout, final int i) {
//
//        //Expand CardView
//        if (expandableLayout.getVisibility() == View.VISIBLE) {
//            createRotateAnimator(buttonLayout, 180f, 0f).start();
//            expandableLayout.setVisibility(View.GONE);
//            expandState.put(i, false);
//        } else {
//            createRotateAnimator(buttonLayout, 0f, 180f).start();
//            expandableLayout.setVisibility(View.VISIBLE);
//            expandState.put(i, true);
//        }
//
//    }
//
//    //Animation Expand
//    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
//        animator.setDuration(300);
//        animator.setInterpolator(new LinearInterpolator());
//        return animator;
//
//    }
//}
