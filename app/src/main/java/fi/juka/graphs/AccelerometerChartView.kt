package fi.juka.graphs

import android.app.Activity
import android.content.Context
import android.graphics.Color
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import fi.juka.activityrecognizer.R

class AccelerometerChartView(private val context: Context) {

    private val graphView = (context as Activity).findViewById<GraphView>(R.id.graph)
    private val viewport = graphView.viewport
    private val seriesX = LineGraphSeries(arrayOf<DataPoint>())
    private val seriesY = LineGraphSeries(arrayOf<DataPoint>())
    private val seriesZ = LineGraphSeries(arrayOf<DataPoint>())
    private var lastX = 0

    init {

        // Set the viewport bounds manually
        viewport.isXAxisBoundsManual = true
        viewport.isYAxisBoundsManual = true

        // Set the minimum and maximum values for the x and y axes
        viewport.setMinX(0.0)
        viewport.setMaxX(100.0)
        viewport.setMinY(-20.0)
        viewport.setMaxY(20.0)

        // Set the colors for the X, Y, and Z datapoint series
        seriesX.color = Color.RED
        seriesY.color = Color.BLUE
        seriesZ.color = Color.GREEN

        // Configure the legend renderer to display the series titles at the top of the graph
        graphView.legendRenderer.isVisible = true
        graphView.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        graphView.legendRenderer.backgroundColor = Color.TRANSPARENT

        // Set the titles for the X, Y, and Z datapoint series
        seriesX.title = "X-Axis: 0.000000"
        seriesY.title = "Y-Axis: 0.000000"
        seriesZ.title = "Z-Axis: 0.000000"

        // Add the datapoint series to the chart
        graphView.addSeries(seriesX)
        graphView.addSeries(seriesY)
        graphView.addSeries(seriesZ)
    }

    fun update(x: Float, y: Float, z: Float) {

        // Add the datapoints to the series
        seriesX.appendData(DataPoint(lastX.toDouble(), x.toDouble()), true, 100)
        seriesY.appendData(DataPoint(lastX.toDouble(), y.toDouble()), true, 100)
        seriesZ.appendData(DataPoint(lastX.toDouble(), z.toDouble()), true, 100)
        lastX++

        seriesX.title = "X-Axis: $x"
        seriesY.title = "Y-Axis: $y"
        seriesZ.title = "Z-Axis: $z"

        // Sets the minimum X-value to be 100 before the maximum X-value to ensure that the
        // chart shows the most recent data.
        val maxX = seriesX.highestValueX.coerceAtMost(seriesY.highestValueX)
            .coerceAtMost(seriesZ.highestValueX)
        val minX = maxX - 100.0
        viewport.setMinX(minX)
        viewport.setMaxX(maxX)

        // Removes all datapoint series
        if (lastX % 100 == 0) {
            graphView.removeAllSeries()
            graphView.addSeries(seriesX)
            graphView.addSeries(seriesY)
            graphView.addSeries(seriesZ)
        }
    }
}