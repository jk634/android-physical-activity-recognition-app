package fi.juka.activityrecognizer.accelerometer

class AccelerationDataBuffer(private val bufferSize: Int) {
    private val dataBuffer = ArrayList<Pair<Triple<Double, Double, Double>, Long>>()

    fun addData(acceleration: Triple<Double, Double, Double>, time: Long) {
        if (dataBuffer.size >= bufferSize) {
            dataBuffer.removeAt(0)
        }
        dataBuffer.add(Pair(acceleration, time))
    }

    fun getData(): List<Pair<Triple<Double, Double, Double>, Long>> {
        return dataBuffer.toList()
    }

    fun emptyData() {
        dataBuffer.clear()
    }
}