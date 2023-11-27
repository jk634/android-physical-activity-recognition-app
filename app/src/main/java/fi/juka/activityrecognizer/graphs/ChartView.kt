package fi.juka.activityrecognizer.graphs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View

class ChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val linePaintX = Paint().apply { // Viivapensseli X-suunnassa
        color = Color.RED
        strokeWidth = 20f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val linePaintY = Paint().apply { // Viivapensseli Y-suunnassa
        color = Color.GREEN
        strokeWidth = 7f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val linePaintZ = Paint().apply { // Viivapensseli Z-suunnassa
        color = Color.BLUE
        strokeWidth = 3f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val dataPointsX = mutableListOf<Float>()
    private val dataPointsY = mutableListOf<Float>()
    private val dataPointsZ = mutableListOf<Float>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d("TESTI", dataPointsX.toString())
        // Määritellään x- ja y-akselit kaavion piirtämiseksi
        val xStart = 0f
        val yStart = height.toFloat() / 2f
        val xEnd = width.toFloat()
        val yEnd = height.toFloat() / 2f

        // Piirretään x- ja y-akselit
        canvas.drawLine(xStart, yStart, xEnd, yEnd, linePaintX)

        // Piirretään datapistesarja X-suunnassa
        val dataPointsCountX = dataPointsX.size
        //Log.d("TAG", dataPointsCountX.toString())
        if (dataPointsCountX > 1) {
            val dataPointsStepX = (xEnd - xStart) / (dataPointsCountX - 1)
            var prevX = xStart
            var prevY = yStart
            for (i in 0 until dataPointsCountX) {
                val x = (xStart + i * dataPointsStepX)
                val y = (yStart - dataPointsX[i] * (yEnd - yStart))
                canvas.drawLine(prevX, prevY, x, y, linePaintX)
                prevX = x
                prevY = y
            }
        }

        // Piirretään datapistesarja Y-suunnassa
        val dataPointsCountY = dataPointsY.size
        if (dataPointsCountY > 1) {
            val dataPointsStepY = (xEnd - xStart) / (dataPointsCountY - 1)
            var prevX = xStart
            var prevY = yStart
            for (i in 0 until dataPointsCountY) {
                val x = xStart + i * dataPointsStepY
                val y = yStart - dataPointsY[i] * (yEnd - yStart)
                canvas.drawLine(prevX, prevY, x, y, linePaintY)
                prevX = x
                prevY = y
            }
        }

        // Piirretään datapistesarja Z-suunnassa
        val dataPointsCountZ = dataPointsZ.size
        if (dataPointsCountZ > 1) {
            val dataPointsStepZ = (xEnd - xStart) / (dataPointsCountZ - 1)
            var prevX = xStart
            var prevY = yStart
            for (i in 0 until dataPointsCountZ) {
                val x = xStart + i * dataPointsStepZ
                val y = yStart - dataPointsZ[i] * (yEnd - yStart)
                canvas.drawLine(prevX, prevY, x, y, linePaintZ)
                prevX = x
                prevY = y
            }
        }
    }

    fun addDataPoint(accelerometerValues: FloatArray) {
        dataPointsX.add(accelerometerValues[0])
        dataPointsY.add(accelerometerValues[1])
        dataPointsZ.add(accelerometerValues[2])
        invalidate() // Päivitetään näkymä uusien datapisteen lisäämisen jälkeen
    }

    fun clearDataPoints() {
        dataPointsX.clear()
        dataPointsY.clear()
        dataPointsZ.clear()
        invalidate()
    }
}


