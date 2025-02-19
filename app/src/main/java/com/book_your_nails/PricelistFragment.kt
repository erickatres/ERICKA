package com.book_your_nails

import android.os.Bundle
import android.text.SpannableString
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class PricelistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pricelist, container, false)

        // Find TextView for Price
        val priceTextView7 = view.findViewById<TextView>(R.id.price_text7)
        val priceTextView9 = view.findViewById<TextView>(R.id.price_text9)

        // Set styled text ₱20 (per piece) with different sizes
        val fullText = "₱20 (per piece)"
        val spannable = SpannableString(fullText)

        // Set ₱20 to 16sp
        spannable.setSpan(AbsoluteSizeSpan(16, true), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set (per piece) to 14sp
        spannable.setSpan(AbsoluteSizeSpan(12, true), 4, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        priceTextView7.text = spannable

        // Set styled text for ₱30 (per nail)
        val fullText9 = "₱30 (per nail)"
        val spannable9 = SpannableString(fullText9)

        // Set ₱30 to 16sp
        spannable9.setSpan(AbsoluteSizeSpan(16, true), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set (per nail) to 12sp
        spannable9.setSpan(AbsoluteSizeSpan(12, true), 4, fullText9.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        priceTextView9.text = spannable9

        return view
    }
}
