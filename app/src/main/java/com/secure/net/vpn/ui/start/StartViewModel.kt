package com.secure.net.vpn.ui.start

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.MutableLiveData
import com.secure.net.vpn.base.BaseViewModel

class StartViewModel:BaseViewModel() {
    val jumpToMain = MutableLiveData(false)
     fun startCountdown(animationFun:(progress:Int)->Unit,nextEnd:()->Unit) {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 5000
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            animationFun(progress)
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                nextEnd()
            }
        })
        animator.start()
    }
}