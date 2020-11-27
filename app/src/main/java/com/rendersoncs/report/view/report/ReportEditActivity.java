/*
package com.rendersoncs.reportform.view.activitys;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.view.adapter.checkListAdapter.EditCheckListReportAdapter;
import com.rendersoncs.reportform.view.adapter.checkListAdapter.ReportRecyclerView;
import com.rendersoncs.reportform.repository.dao.business.ReportBusiness;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.view.adapter.listener.OnItemListenerClicked;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditReportActivity {

    private ArrayList editConformity = new ArrayList();
    private ArrayList editNotes = new ArrayList();
    private ArrayList editPhoto = new ArrayList();
    private OnItemListenerClicked listenerClicked;

    public void loadEditReportExt(Activity activity,
                                  int mReportId,
                                  ReportRecyclerView mAdapter,
                                  ReportBusiness mReportBusiness,
                                  ArrayList<ReportItems> reportItems,
                                  RecyclerView recyclerView) {

            ReportItems repoEntity = mReportBusiness.load(mReportId);

            try {
                JSONArray array = new JSONArray(repoEntity.getListJson());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jo = array.getJSONObject(i);

                    String conformity = jo.getString(ReportConstants.ITEM.CONFORMITY);
                    editConformity.add(conformity);
                    Log.i("log", "TESTE: " + i + editConformity + " notes");

                    String note = jo.getString(ReportConstants.ITEM.NOTE);
                    editNotes.add(note);

                    String photo = jo.getString(ReportConstants.ITEM.PHOTO);
                    editPhoto.add(photo);

                    ReportItems repoJson = new ReportItems(jo.getString(ReportConstants.ITEM.TITLE),
                            jo.getString(ReportConstants.ITEM.DESCRIPTION), null, null);
                    reportItems.add(repoJson);
                }

                for (int i = 0; i < editConformity.size(); i++) {
                    if (editConformity.get(i).equals(activity.getResources().getString(R.string.according))) {
                        listenerClicked.radioItemChecked(i, 1);
                    }
                    if (editConformity.get(i).equals(activity.getResources().getString(R.string.not_applicable))) {
                        listenerClicked.radioItemChecked(i, 2);
                    }
                    if (editConformity.get(i).equals(activity.getResources().getString(R.string.not_according))) {
                        listenerClicked.radioItemChecked(i, 3);
                    }
                }

                for (int i = 0; i < editNotes.size(); i ++){
                    String notes = (String) editNotes.get(i);
                    mAdapter.insertNote(i, notes);
                    Log.i("log", "NOTES: " + i + notes + " notes");
                }

                for (int i = 0; i < editPhoto.size(); i ++){
                    String photos = (String) editPhoto.get(i);

                    byte[] decodedString = Base64.decode(photos, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    mAdapter.setImageInItem(i, decodedByte);
                }

                mAdapter = new EditCheckListReportAdapter(reportItems, activity);
                mAdapter.setOnItemListenerClicked((OnItemListenerClicked) activity);
                recyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

}
*/
