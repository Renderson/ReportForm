package com.rendersoncs.reportform.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rendersoncs.reportform.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends BottomSheetFragment {

    private static final String NUMBER = "985700958";
    private static final String NUMBER_WHATS = "+5511985700958";
    private static final String EMAIL = "renderson.cs@gmail.com";
    private static final String LINKED_IN_ID = "renderson-cerqueira-14a91a53";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Element adsElement = new Element();
        adsElement.setTitle(getResources().getString(R.string.app_name));

        Element versionElement = new Element();
        versionElement.setTitle(getString(R.string.version));

        View aboutPage = (new AboutPage(this.getActivity()))
                .setImage(R.drawable.no_result)
                .setDescription(getString(R.string.label_about_app))
                .addItem(versionElement)
                .addItem(adsElement)
                .addGroup(getString(R.string.label_entry_contact))
                .addItem(getItemPhone())
                .addItem(getItemEmail())
                .addItem(getItemWhatsApp())
                .addGroup(getString(R.string.label_more_work))
                .addItem(getItemLinkedIn())
                .create();

        return aboutPage;
    }

    private Element getItemPhone() {
        return (new Element()).setTitle(getString(R.string.label_calling))
                .setIconTint(R.color.colorGrey)
                .setGravity(Gravity.START)
                .setIconDrawable(R.drawable.ic_phone_in_talk_black_24dp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_DIAL);
                        i.setData(Uri.parse("tel:$" + NUMBER));
                        startActivity(i);
                    }
                });
    }

    private Element getItemWhatsApp() {
        return (new Element()).setTitle(getString(R.string.label_calling_whatsapp))
                .setIconTint(R.color.colorWhatsAppLogo)
                .setGravity(Gravity.START)
                .setIconDrawable(R.drawable.ic_whatsapp_black_24dp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        whatsAppHelp();
                    }
                });
    }

    private Element getItemEmail() {
        return (new Element()).setTitle(getString(R.string.label_fedd_back))
                .setIconDrawable(R.drawable.ic_mail_in_box_black_24dp)
                .setIconTint(R.color.colorGrey)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_SENDTO);
                        i.setData(Uri.parse("mailto:"));
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
                        startActivity(i);
                    }
                });
    }

    private Element getItemLinkedIn() {
        return (new Element()).setTitle(getString(R.string.label_recommendation))
                .setIconTint(R.color.colorLinkedInLogo)
                .setGravity(Gravity.START)
                .setIconDrawable(R.drawable.ic_linkedin_box_black_24dp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/" + LINKED_IN_ID));
                        i.setPackage("com.linkedin.android");

                        if (i.resolveActivity(getContext().getPackageManager()) == null) {
                            i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=$" + LINKED_IN_ID));
                        }
                        startActivity(i);
                    }
                });
    }

    private void whatsAppHelp(){
        Uri whatsAppUri = Uri.parse("smsto:$"+NUMBER_WHATS);
        Intent i = new Intent(Intent.ACTION_SENDTO, whatsAppUri);
        i.setPackage("com.whatsapp");
        if (i.resolveActivity(getContext().getPackageManager()) != null){
            startActivity(i);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.label_not_whatsapp), Toast.LENGTH_SHORT).show();
        }
    }
}
