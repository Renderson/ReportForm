package com.rendersoncs.reportform.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.fragment.NewItemListFireBase;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.listener.OnItemListenerClicked;

import static com.android.volley.VolleyLog.TAG;

public class ReportCheckListAdapter extends RecyclerView.Adapter<ReportCheckListAdapter.ViewHolder> {
    private List<ReportItems> reportItems;
    public SparseBooleanArray expandState = new SparseBooleanArray();
    public ArrayList<Integer> listIDRadio = new ArrayList<Integer>();
    public Context context;

    public ArrayList<String> listTxtRadio = new ArrayList<>();
    public ArrayList<String> listText = new ArrayList<>();
    public ArrayList<Integer> listId = new ArrayList<Integer>();

    public RadioButton radioButton;

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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        //Header
        final ReportItems repo = reportItems.get(position);

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
            viewHolder.buttonLayoutArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onClickButton(viewHolder.expandableLayout, viewHolder.buttonLayoutArrow, position);
                }
            });

            viewHolder.tvTitleList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListenerClicked.updateList(position);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", viewHolder.tvTitleList.getText().toString());
//                    NewItemListFireBase newItemListFirebase = new NewItemListFireBase();
////                    Intent intent = new Intent(context, newItemListFirebase.getClass());
////                    intent.putExtra("title", viewHolder.tvTitleList.getText().toString());
////                    intent.putExtra("desc", viewHolder.tvDescription.getText().toString());
//                    newItemListFirebase.setArguments(bundle);
                }
            });
            
            viewHolder.takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListenerClicked.takePhoto(position);
                }
            });
//            if (viewHolder.resultPhoto != null){
//                Glide.with(context).load(repo.getPhotoId()).centerCrop().into(viewHolder.resultPhoto);
//            } else {
//                Glide.with(context).load(repo.getPhotoId()).centerCrop().into(viewHolder.resultPhoto2);
//            }

            Glide.with(context).load(repo.getPhotoId()).centerCrop().into(viewHolder.resultPhoto);
            Log.i("LOG", "ImagePath3 " + repo.getPhotoUri());

            viewHolder.mRadioButtonConform.setChecked(repo.isOpt1());
            viewHolder.mRadioButtonNotConform.setChecked(repo.isOpt2());

            //Test onClick RadioButton
            viewHolder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    int selectedRadioButtonID = viewHolder.mRadioGroup.getCheckedRadioButtonId();
                    //Test Salve in a ArrayList RadioButton Selected
                    radioButton = group.findViewById(selectedRadioButtonID);
                    int selectedRadioId = radioButton.getId();
                    listIDRadio.add(selectedRadioId);
                }
            });
            // End RadioButton
        }
        Log.i(TAG, "onBindViewHolder invoked" + position);
    }

    public int getItemViewType(int i) {
        if (isPositionHeader(i))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitleList, tvDescription;
        ImageView takePhoto, resultPhoto, resultPhoto2;

        RadioGroup mRadioGroup;
        RadioButton mRadioButtonConform, mRadioButtonNotConform;
        RelativeLayout buttonLayoutArrow;
        private LinearLayout expandableLayout;

        private ViewHolder(View view) {
            super(view);

            tvTitleList = view.findViewById(R.id.textView_title);
            tvDescription = view.findViewById(R.id.textView_subTitle);
            takePhoto = view.findViewById(R.id.photo);
            resultPhoto = view.findViewById(R.id.result_photo);
            resultPhoto2 = view.findViewById(R.id.result_photo2);

            buttonLayoutArrow = view.findViewById(R.id.btnArrow);
            expandableLayout = view.findViewById(R.id.expandableLayout);
        }
    }

    //Header
    public class HeaderVh extends ReportCheckListAdapter.ViewHolder {

        @BindView(R.id.header_id)
        public TextView headerTitle;

        private HeaderVh(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ItemVh extends ReportCheckListAdapter.ViewHolder implements View.OnClickListener {

        @BindView(R.id.textView_title)
        public TextView itemContent;

        private ItemVh(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mRadioGroup = itemView.findViewById(R.id.radio_group);
            mRadioButtonConform = itemView.findViewById(R.id.radio_conform);
            mRadioButtonNotConform = itemView.findViewById(R.id.radio_not_conform);

            tvTitleList.setOnClickListener(this);
            takePhoto.setOnClickListener(this);
            mRadioButtonConform.setOnClickListener(this);
            mRadioButtonNotConform.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.radio_conform:
                    if (onItemListenerClicked != null)
                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), 1);
                    break;

                case R.id.radio_not_conform:
                    if (onItemListenerClicked != null)
                        onItemListenerClicked.radioItemChecked(getAdapterPosition(), 2);
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

    public void setImageInItem(int position, Bitmap imageSrc) {
        ReportItems dataSet = reportItems.get(position);
        dataSet.setPhotoId(imageSrc);
        Log.i("LOG", "ImagePath2 " + imageSrc);
        notifyDataSetChanged();
    }
}
