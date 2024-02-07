package com.rendersoncs.report.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rendersoncs.report.BuildConfig
import com.rendersoncs.report.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.lang.Exception

class AboutFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val adsElement = Element()
        adsElement.title = resources.getString(R.string.app_name)
        val versionElement = Element()
        versionElement.title = BuildConfig.VERSION_NAME
        return AboutPage(this.activity)
                .setImage(R.mipmap.ic_launcher_round)
                .setDescription(getString(R.string.label_about_app))
                .addItem(versionElement)
                .addItem(adsElement)
                .addGroup(getString(R.string.label_entry_contact))
                //.addItem(itemPhone)
                .addItem(itemEmail)
                //.addItem(itemWhatsApp)
                .addGroup(getString(R.string.label_more_work))
                .addItem(itemLinkedIn)
                .create()
    }

    private val itemPhone: Element
        get() = Element().setTitle(getString(R.string.label_calling))
                .setIconTint(R.color.colorGrey)
                .setGravity(Gravity.START)
                .setIconDrawable(R.drawable.ic_phone_in_talk_black_24dp)
                .setOnClickListener {
                    val i = Intent(Intent.ACTION_DIAL)
                    i.data = Uri.parse("tel:$NUMBER")
                    startActivity(i)
                }

    private val itemWhatsApp: Element
        get() = Element().setTitle(getString(R.string.label_calling_whatsApp))
                .setIconTint(R.color.colorWhatsAppLogo)
                .setGravity(Gravity.START)
                .setIconDrawable(R.drawable.ic_whatsapp_black_24dp)
                .setOnClickListener { whatsAppHelp() }

    private val itemEmail: Element
        get() = Element().setTitle(getString(R.string.label_feed_back))
                .setIconDrawable(R.drawable.ic_mail_in_box_black_24dp)
                .setIconTint(R.color.colorGrey)
                .setOnClickListener {
                    try {
                        val i = Intent(Intent.ACTION_SENDTO)
                        i.data = Uri.parse("mailto:")
                        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL))
                        startActivity(i)
                    } catch (exception: Exception) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com")))
                    }
                }

    private val itemLinkedIn: Element
        get() = Element().setTitle(getString(R.string.label_recommendation))
                .setIconTint(R.color.colorLinkedInLogo)
                .setGravity(Gravity.START)
                .setIconDrawable(R.drawable.ic_linkedin_box_black_24dp)
                .setOnClickListener {
                    var i = Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/$LINKED_IN_ID"))
                    i.setPackage("com.linkedin.android")
                    if (i.resolveActivity(requireContext().packageManager) == null) {
                        i = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=$LINKED_IN_ID"))
                    }
                    startActivity(i)
                }

    private fun whatsAppHelp() {
        val whatsAppUri = Uri.parse("smsto:$NUMBER_WHATS")
        val i = Intent(Intent.ACTION_SENDTO, whatsAppUri)
        i.setPackage("com.whatsapp")
        if (i.resolveActivity(requireContext().packageManager) != null) {
            startActivity(i)
        } else {
            Toast.makeText(activity, resources.getString(R.string.label_not_whatsApp), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val NUMBER = ""
        private const val NUMBER_WHATS = ""
        private const val EMAIL = "renderson.cs@gmail.com"
        private const val LINKED_IN_ID = "renderson-cerqueira-14a91a53"
    }
}