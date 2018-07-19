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
}