package fi.juka.activityrecognizer.database

class TrainingData(   val id: Long,
                      val x_axis: Float,
                      val y_axis: Float,
                      val z_axis: Float,
                      val timestamp: Long,
                      val activityId: Long) {

}