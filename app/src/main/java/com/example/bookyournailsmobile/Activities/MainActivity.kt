package com.example.bookyournailsmobile.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bookyournailsmobile.Fragments.BookingFragment
import com.example.bookyournailsmobile.Fragments.HomeFragment
import com.example.bookyournailsmobile.Fragments.PricelistFragment
import com.example.bookyournailsmobile.Fragments.ProfileFragment
import com.example.bookyournailsmobile.R
import com.example.bookyournailsmobile.Managers.SessionManagement

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManagement: SessionManagement

    private lateinit var homeLayout: LinearLayout
    private lateinit var pricelistLayout: LinearLayout
    private lateinit var bookingLayout: LinearLayout
    private lateinit var profileLayout: LinearLayout

    private lateinit var homeIcon: ImageButton
    private lateinit var pricelistIcon: ImageButton
    private lateinit var bookingIcon: ImageButton
    private lateinit var profileIcon: ImageButton

    private lateinit var homeText: TextView
    private lateinit var pricelistText: TextView
    private lateinit var bookingText: TextView
    private lateinit var profileText: TextView

    private var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide the system navigation bar

        sessionManagement = SessionManagement(this)
        if (sessionManagement.getSession() == null) { // Check for null instead of -1
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        initViews()
        setClickListeners()

        if (savedInstanceState == null) {
            switchTab(HomeFragment(), homeIcon, homeText, homeLayout)
        } else {
            currentFragmentTag = savedInstanceState.getString("CURRENT_FRAGMENT_TAG")
            supportFragmentManager.findFragmentByTag(currentFragmentTag)?.let {
                showFragment(it)
                setActiveTab(
                    findViewById(
                        resources.getIdentifier(
                            "btn_${currentFragmentTag?.lowercase()}",
                            "id",
                            packageName
                        )
                    ),
                    findViewById(
                        resources.getIdentifier(
                            "${currentFragmentTag?.lowercase()}_txt",
                            "id",
                            packageName
                        )
                    ),
                    findViewById(
                        resources.getIdentifier(
                            "${currentFragmentTag?.lowercase()}_layout",
                            "id",
                            packageName
                        )
                    )
                )
            }
        }
    }

    private fun hideSystemNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For older versions
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }


    private fun initViews() {
        homeLayout = findViewById(R.id.home_layout)
        pricelistLayout = findViewById(R.id.pricelist_layout)
        bookingLayout = findViewById(R.id.booking_layout)
        profileLayout = findViewById(R.id.profile_layout)

        homeIcon = findViewById(R.id.btn_home)
        pricelistIcon = findViewById(R.id.btn_pricelist)
        bookingIcon = findViewById(R.id.btn_booking)
        profileIcon = findViewById(R.id.btn_profile)

        homeText = findViewById(R.id.home_txt)
        pricelistText = findViewById(R.id.pricelist_txt)
        bookingText = findViewById(R.id.booking_txt)
        profileText = findViewById(R.id.profile_txt)
    }

    private fun setClickListeners() {
        val tabs = listOf(
            Triple(homeLayout, homeIcon, homeText),
            Triple(pricelistLayout, pricelistIcon, pricelistText),
            Triple(bookingLayout, bookingIcon, bookingText),
            Triple(profileLayout, profileIcon, profileText)
        )

        tabs.forEach { (layout, icon, text) ->
            layout.setOnClickListener { switchTab(getFragmentForTab(icon.id), icon, text, layout) }
            icon.setOnClickListener { switchTab(getFragmentForTab(icon.id), icon, text, layout) }
        }
    }

    private fun getFragmentForTab(id: Int): Fragment = when (id) {
        R.id.btn_pricelist -> PricelistFragment()
        R.id.btn_booking -> BookingFragment()
        R.id.btn_profile -> ProfileFragment()
        else -> HomeFragment()
    }

    private fun switchTab(
        fragment: Fragment,
        icon: ImageButton,
        text: TextView,
        layout: LinearLayout
    ) {
        val tag = fragment::class.java.simpleName

        if (tag == HomeFragment::class.java.simpleName) {
            val transaction = supportFragmentManager.beginTransaction()
            supportFragmentManager.fragments.forEach { transaction.remove(it) }
            transaction.replace(R.id.fragment_container, HomeFragment(), tag)
            transaction.commit()
            resetTabs()
            setActiveTab(icon, text, layout)
            currentFragmentTag = tag
        } else {
            if (currentFragmentTag != tag) {
                showFragment(fragment)
                resetTabs()
                setActiveTab(icon, text, layout)
                currentFragmentTag = tag
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        val tag = fragment::class.java.simpleName

        supportFragmentManager.fragments.forEach { transaction.hide(it) }

        supportFragmentManager.findFragmentByTag(tag)?.let {
            transaction.show(it)
        } ?: transaction.add(R.id.fragment_container, fragment, tag)

        transaction.commit()
    }

    private fun resetTabs() {
        listOf(homeIcon, pricelistIcon, bookingIcon, profileIcon).forEach {
            it.setImageResource(getUnfilledIcon(it.id))
        }

        listOf(homeText, pricelistText, bookingText, profileText).forEach {
            it.setTextColor(Color.GRAY)
        }
    }

    private fun setActiveTab(icon: ImageButton, text: TextView, layout: LinearLayout) {
        icon.setImageResource(getFilledIcon(icon.id))
        text.setTextColor(Color.parseColor("#FF9E9E"))
    }

    private fun getUnfilledIcon(id: Int) = when (id) {
        R.id.btn_home -> R.drawable.home_without_fill
        R.id.btn_pricelist -> R.drawable.pricelist_without_fill
        R.id.btn_booking -> R.drawable.booking_without_fill
        R.id.btn_profile -> R.drawable.profile_without_fill
        else -> 0
    }

    private fun getFilledIcon(id: Int) = when (id) {
        R.id.btn_home -> R.drawable.home_with_fill
        R.id.btn_pricelist -> R.drawable.pricelist_with_fill
        R.id.btn_booking -> R.drawable.booking_with_fill
        R.id.btn_profile -> R.drawable.profile_with_fill
        else -> 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("CURRENT_FRAGMENT_TAG", currentFragmentTag)
    }
}