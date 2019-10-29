//package com.rendersoncs.reportform.view;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.Base64;
//import android.util.Log;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.rendersoncs.reportform.R;
//import com.rendersoncs.reportform.adapter.ReportCheckListAdapter;
//import com.rendersoncs.reportform.business.ReportBusiness;
//import com.rendersoncs.reportform.constants.ReportConstants;
//import com.rendersoncs.reportform.itens.ReportItems;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//public class EditReportActivity extends ReportActivity{
//
//    private ArrayList<String> editConformity = new ArrayList<>();
//    private ArrayList<String> editNotes = new ArrayList<>();
//    private ArrayList<String> editPhoto = new ArrayList<>();
//    private ArrayList<ReportItems> reportItems = new ArrayList<>();
//
//    public void loadEditReportExt(int mReportId,
//                                  ReportCheckListAdapter mAdapter,
//                                  ReportBusiness mReportBusiness, RecyclerView recyclerView) {
//
//            ReportItems repoEntity = mReportBusiness.load(mReportId);
//
//            try {
//                JSONArray array = new JSONArray(repoEntity.getListJson());
//
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject jo = array.getJSONObject(i);
//
//                    String conformity = jo.getString(ReportConstants.ITEM.CONFORMITY);
//                    editConformity.add(conformity);
//                    Log.i("log", "TESTE: " + i + editConformity + " notes");
//
//                    String note = jo.getString(ReportConstants.ITEM.NOTE);
//                    editNotes.add(note);
//
//                    String photo = jo.getString(ReportConstants.ITEM.PHOTO);
//                    editPhoto.add(photo);
//
//                    ReportItems repoJson = new ReportItems(jo.getString(ReportConstants.ITEM.TITLE),
//                            jo.getString(ReportConstants.ITEM.DESCRIPTION), jo.getString("title"));
//                    reportItems.add(repoJson);
//                }
//
//                for (int i = 0; i < editConformity.size(); i++) {
//                    if (editConformity.get(i).equals(getResources().getString(R.string.according))) {
//                        this.radioItemChecked(i, 1);
//                    }
//                    if (editConformity.get(i).equals(getResources().getString(R.string.not_applicable))) {
//                        this.radioItemChecked(i, 2);
//                    }
//                    if (editConformity.get(i).equals(getResources().getString(R.string.not_according))) {
//                        this.radioItemChecked(i, 3);
//                    }
//                }
//
//                for (int i = 0; i < editNotes.size(); i ++){
//                    String notes = editNotes.get(i);
//                    mAdapter.insertNote(i, notes);
//                    Log.i("log", "NOTES: " + i + notes + " notes");
//                }
//
//                for (int i = 0; i < editPhoto.size(); i ++){
//                    String photos = editPhoto.get(i);
//
//                    byte[] decodedString = Base64.decode(photos, Base64.DEFAULT);
//                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//                    mAdapter.setImageInItem(i, decodedByte);
//                }
//
//                mAdapter = new ReportCheckListAdapter(reportItems, EditReportActivity.this);
//                mAdapter.setOnItemListenerClicked(EditReportActivity.this);
//                recyclerView.setAdapter(mAdapter);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//    }
//
//}
