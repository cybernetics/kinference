package org.jetbrains.research.kotlin.inference.model

import org.jetbrains.research.kotlin.inference.data.ONNXData
import org.jetbrains.research.kotlin.inference.graph.Graph
import org.jetbrains.research.kotlin.inference.onnx.ModelProto
import java.io.File

class Model(proto: ModelProto) {
    val graph = Graph(proto.graph!!)

    inline fun <reified T : Number> predict(input: List<T>): List<ONNXData> {
        val inputs = listOf(graph.prepareInput(input))
        return graph.execute(inputs)
    }

    fun predict(input: Collection<ONNXData>): List<ONNXData> {
        return graph.execute(input.toList())
    }

    companion object {
        fun load(file: String): Model {
            val modelScheme = ModelProto.ADAPTER.decode(File(file).readBytes())
            return Model(modelScheme)
        }
    }
}
