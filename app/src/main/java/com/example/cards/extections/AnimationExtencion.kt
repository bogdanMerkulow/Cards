package com.example.cards.extections

import android.view.animation.Animation

fun Animation.setListener(
    onStart: () -> Unit = {},
    onEnd: () -> Unit = {},
    onRepeat: () -> Unit = {},
) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart()
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd()
        }

        override fun onAnimationRepeat(animation: Animation?) {
            onRepeat()
        }
    })
}