package io.github.droidkaigi.confsched2019.floormap.ui

import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.view.View

import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ScaleSplingAnimation( animateView: ImageView, infoView: TextView ) {
    private val INITIAL_SCALE = 1f
    private var scaleFactor = 1f

    private val scaleXAnimation: SpringAnimation
    private val scaleYAnimation: SpringAnimation
    private var scaleGestureDetector: ScaleGestureDetector? = null

    private val animateView: View?
    private val infoView: TextView?

    init {
        this.infoView = infoView
        this.animateView = animateView
        // create scaleX and scaleY animations
        scaleXAnimation = createSpringAnimation(
            animateView, SpringAnimation.SCALE_X,
            INITIAL_SCALE, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        )
        scaleYAnimation = createSpringAnimation(
            animateView, SpringAnimation.SCALE_Y,
            INITIAL_SCALE, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        )
        updateInfoView()
        setupPinchToZoom()
        this.animateView.setOnTouchListener(touchListener)
        scaleXAnimation.addUpdateListener(updateListener)
    }

    fun createSpringAnimation(
        view: View,
        property: DynamicAnimation.ViewProperty,
        finalPosition: Float,
        stiffness: Float,
        dampingRatio: Float
    ): SpringAnimation {
        val animation = SpringAnimation(view, property)
        val springForce = SpringForce(finalPosition)
        springForce.setStiffness(stiffness)
        springForce.setDampingRatio(dampingRatio)
        animation.setSpring(springForce)
        return animation
    }
}
