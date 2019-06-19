package com.rendersoncs.reportform.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import com.rendersoncs.reportform.itens.Repo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rendersoncs.reportform.R;

import static com.android.volley.VolleyLog.TAG;

public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder> {
        private List<Repo> repos;
        public SparseBooleanArray expandState = new SparseBooleanArray();
        public ArrayList<String> listTxtRadio = new ArrayList<>();
        public ArrayList<String> listText = new ArrayList<>();
        public ArrayList<Integer> listIDRadio = new ArrayList<Integer>();
        public ArrayList<Integer> listId = new ArrayList<Integer>();
        public Context context;

        public RadioButton radioButton;

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        public ExpandableRecyclerAdapter(List<Repo> repos, Context context) {
            this.context = context;
            this.repos = repos;
            for (int i = 0; i < repos.size(); i++) {
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
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
            //Header
            final Repo repo = repos.get(position);

            if (viewHolder instanceof HeaderVh) {
                ((HeaderVh) viewHolder).headerTitle.setText(repo.getTitle());
            } else if (viewHolder instanceof ItemVh) {
                ((ItemVh) viewHolder).tvTitleList.setText(repo.getTitle());

                viewHolder.setIsRecyclable(false);
                viewHolder.tvTitleList.setText(repos.get(position).getTitle());
                viewHolder.tvTextList.setText(repos.get(position).getText());

                final boolean isExpanded = expandState.get(position);
                viewHolder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

                viewHolder.buttonLayoutArrow.setRotation(expandState.get(position) ? 180f : 0f);
                viewHolder.buttonLayoutArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onClickButton(viewHolder.expandableLayout, viewHolder.buttonLayoutArrow, position);

                        //Test onClick RadioButton
                        viewHolder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                int selectedRadioButtonID = viewHolder.mRadioGroup.getCheckedRadioButtonId();

                                //Test Salve in a ArrayList RadioButton Selected
                                radioButton = group.findViewById(selectedRadioButtonID);

                                String selectedText = (String) viewHolder.tvTitleList.getText();
                                String selectedRadioButtonText = radioButton.getText().toString();
                                int selectedRadioId = radioButton.getId();

                                listIDRadio.add(selectedRadioId);
                                listId.add(position);
                                listText.add(selectedText);
                                listTxtRadio.add(selectedRadioButtonText);

                                Log.i("log", "Item: " + listTxtRadio + " listRadio ");
                                Log.i("log", "Item: " + listId + " selectedIDList ");
                                Log.i("log", "Item: " + listIDRadio + " selectedIDRadio ");
                                Log.i("log", "Item: " + listText + " listText ");

                                //Del duplicate list
                                /*HashSet<String> hashSet = new HashSet<String>(listText);
                                hashSet.addAll(listText);
                                listText.clear();
                                listText.addAll(hashSet);
                                repo.setCheckList(listText);
                                Log.position("log", "Item: " + repo.getCheckList() + " getCheckList ");*/

                                if (checkedId == R.id.radio_conform) {
                                    viewHolder.mRadioButtonConform.setChecked(true);
                                    group.setTag(checkedId);
                                    //Log.position("log", "Item: " + listConformed + " listConformed ");

                                } else if (checkedId == R.id.radio_not_applicable) {
                                    viewHolder.mRadioButtonNotApplicable.setChecked(true);
                                    group.setTag(checkedId);
                                    //Log.position("log", "Item: " + listNotConformed + " listNotConformed ");

                                } else if (checkedId == R.id.radio_not_conform) {
                                    viewHolder.mRadioButtonNotConform.setChecked(true);
                                    group.setTag(checkedId);
                                    //Log.position("log", "Item: " + listNotApplicable + " listNotApplicable ");

                                } else {
                                    group.clearCheck();
                                }
                            }
                        });
                        // End RadioButton
                    }
                });
            }
            Log.i(TAG, "onBindViewHolder invoked" + position);
        }

        public int getItemViewType(int i) {
            if (isPositionHeader(i))
                return TYPE_HEADER;
            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int i) {
            return i == 0 || i == 21 || i == 30;
        }

        @Override
        public int getItemCount() {
            return repos.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitleList, tvTextList;
            ImageView mImageView;

            public RadioGroup mRadioGroup;
            public RadioButton mRadioButtonConform;
            public RadioButton mRadioButtonNotApplicable;
            public RadioButton mRadioButtonNotConform;

            RelativeLayout buttonLayoutArrow;
            public LinearLayout expandableLayout;

            public ViewHolder(View view) {
                super(view);

                tvTitleList = view.findViewById(R.id.textView_title);
                tvTextList = view.findViewById(R.id.textView_subTitle);
                mImageView = view.findViewById(R.id.photo);
                mRadioGroup = view.findViewById(R.id.radio_group);
                mRadioButtonConform = view.findViewById(R.id.radio_conform);
                mRadioButtonNotApplicable = view.findViewById(R.id.radio_not_applicable);
                mRadioButtonNotConform = view.findViewById(R.id.radio_not_conform);

                buttonLayoutArrow = view.findViewById(R.id.btnArrow);
                expandableLayout = view.findViewById(R.id.expandableLayout);
            }
        }

        //Header
        public class HeaderVh extends ExpandableRecyclerAdapter.ViewHolder {

            @BindView(R.id.header_id)
            public TextView headerTitle;

            public HeaderVh(@NonNull View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

        public class ItemVh extends ExpandableRecyclerAdapter.ViewHolder {

            @BindView(R.id.textView_title)
            public TextView itemContent;


            public ItemVh(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
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
}
