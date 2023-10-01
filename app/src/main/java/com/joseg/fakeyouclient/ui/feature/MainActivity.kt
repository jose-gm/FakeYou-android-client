package com.joseg.fakeyouclient.ui.feature

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.inflateMenu(R.menu.bottom_navigation_view_menu)
        binding.bottomNavigationView.setOnItemSelectedListener {
            it.onNavDestinationSelected(navController)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.textToSpeechFragment,
                R.id.audiosFragment -> {
                    if (binding.bottomNavigationView.isGone)
                        binding.bottomNavigationView.slideOut()
                }
                else -> {
                    if (binding.bottomNavigationView.isVisible)
                        binding.bottomNavigationView.slideIn()
                }
            }
        }
    }

    private fun BottomNavigationView.slideIn() {
        val slideInAnim = ObjectAnimator.ofFloat(this, "translationY", 0f, height.toFloat())
        val fadeOutAnim = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        AnimatorSet().apply {
            duration = 150L
            playTogether(slideInAnim, fadeOutAnim)
            addListener(object : AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationEnd(p0: Animator) {
                    this@slideIn.isGone = true
                }
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
            start()
        }
    }

    private fun BottomNavigationView.slideOut() {
        val slideOutAnim = ObjectAnimator.ofFloat(this, "translationY", height.toFloat(), -0f)
        val fadeInAnim = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        AnimatorSet().apply {
            duration = 150L
            playTogether(slideOutAnim, fadeInAnim)
            addListener(object : AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                    this@slideOut.isVisible = true
                }
                override fun onAnimationEnd(p0: Animator) {}
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
            start()
        }
    }

    fun slideOutBottomNavigationView() {
        binding.bottomNavigationView.slideOut()
    }

    fun slideInBottomNavigationView() {
        binding.bottomNavigationView.slideIn()
    }

    fun isBottomNavigationHidden() = binding.bottomNavigationView.isGone
}