package io.github.droidkaigi.confsched2019.floormap.ui

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import java.util.Locale

class ScaleSplingAnimation(animateView: ImageView) {
    private val INITIAL_SCALE = 1f
    private var scaleFactor = 1f

    private val scaleXAnimation: SpringAnimation
    private val scaleYAnimation: SpringAnimation
    private var scaleGestureDetector: ScaleGestureDetector? = null

    private val animateView: View?

    init {
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
        setupPinchToZoom()
        this.animateView.setOnTouchListener(this.touchListener)
      //  scaleXAnimation.addUpdateListener(updateListener)
    }

    private val touchListener = View.OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            scaleXAnimation.start()
            scaleYAnimation.start()
        } else {
            scaleXAnimation.cancel()
            scaleYAnimation.cancel()
            scaleGestureDetector!!.onTouchEvent(event)
        }
        true
    }

    private fun setupPinchToZoom() {
        scaleGestureDetector = ScaleGestureDetector(animateView!!.getContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    animateView!!.scaleX = animateView.scaleX * scaleFactor
                    animateView!!.scaleY = animateView.scaleY * scaleFactor
                    return true
                }
            }
        )
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
