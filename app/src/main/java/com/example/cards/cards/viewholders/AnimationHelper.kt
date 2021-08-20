package com.example.cards.cards.viewholders

import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation

private const val PIVOT_SIZE = 0.5f

class AnimationHelper {
    companion object {
        fun getScaleAnimation(
            fromX: Float,
            toX: Float,
            fromY: Float,
            toY: Float,
            duration: Long
        ): ScaleAnimation {
            return ScaleAnimation(
                fromX, toX,
                fromY, toY,
                Animation.RELATIVE_TO_SELF,
                PIVOT_SIZE,
                Animation.RELATIVE_TO_SELF,
                PIVOT_SIZE
            ).apply {
                this.duration = duration
                fillAfter = true
            }
        }

        fun getTranslateAnimation(
            fromX: Float,
            toX: Float,
            fromY: Float,
            toY: Float,
            duration: Long
        ): TranslateAnimation {
            return TranslateAnimation(
                fromX, toX,
                fromY, toY
            ).apply {
                this.duration = duration
            }
        }
    }
}