package com.anwesh.uiprojects.bdview

/**
 * Created by anweshmishra on 20/07/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF

fun Canvas.drawAtMid(cb : () -> Unit) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    save()
    translate(w, h/2)
    cb()
    restore()
}

fun Canvas.drawBrickNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val hSize = (h / 2 * nodes)
    val gap : Float = w / nodes
    save()
    translate(-gap - i * gap, -hSize/2)
    val path : Path = Path()
    path.addRect(RectF(-gap, 0f, -gap * scale, hSize), Path.Direction.CW)
    clipPath(path)
    drawRoundRect(RectF(-gap, 0f, 0F, hSize), gap/4, gap/4, paint)
    restore()
}

val nodes : Int = 5

class BDView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class BDState(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class BDAnimator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BDNode(var i : Int, val state : BDState = BDState()) {

        private var next : BDNode? = null

        private var prev : BDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawAtMid {
                canvas.drawBrickNode(i, state.scale, paint)
            }
            next?.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BDNode {
            var curr : BDNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}