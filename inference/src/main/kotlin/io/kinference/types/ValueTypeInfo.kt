package io.kinference.types

import io.kinference.graph.Context
import io.kinference.onnx.TensorProto.DataType
import io.kinference.onnx.TensorShapeProto
import io.kinference.onnx.TypeProto

class TensorShape(private val dims: List<Dimension>) {
    constructor(shape: IntArray) : this(shape.map { StaticDimension(it) })

    val size: Int
        get() = dims.size

    open class Dimension
    class StaticDimension(val value: Int) : Dimension()
    class DynamicDimension(val value: String) : Dimension()
    object UnknownDimension : Dimension()

    fun getDimensions(context: Context? = null): IntArray {
        if (context == null) require(dims.all { it is StaticDimension })
        return dims.map {
            when (it) {
                is StaticDimension -> it.value
                is DynamicDimension -> context!!.getShape(it.value)
                is UnknownDimension -> -1
                else -> error("Unsupported dimension type")
            }
        }.toIntArray()
    }

    companion object {
        fun empty() = TensorShape(emptyList())

        operator fun invoke(proto: TensorShapeProto): TensorShape {
            return TensorShape(proto.dim.map {
                when {
                    it.dim_value != null -> StaticDimension(it.dim_value.toInt())
                    it.dim_param != null -> DynamicDimension(it.dim_param)
                    else -> UnknownDimension
                }
            })
        }
    }
}

abstract class ValueTypeInfo(val type: DataType) {
    companion object {
        fun create(proto: TypeProto) = when {
            proto.tensor_type != null -> TensorTypeInfo(proto.tensor_type)
            proto.sequence_type != null -> SequenceTypeInfo(proto.sequence_type)
            proto.map_type != null -> TODO("support maps")
            else -> error("One should be present")
        }
    }
}

class TensorTypeInfo(val shape: TensorShape, type: DataType) : ValueTypeInfo(type) {
    constructor(proto: TypeProto.Tensor) : this(TensorShape(proto.shape!!), DataType.fromValue(proto.elem_type!!)!!)
}

class SequenceTypeInfo(type: DataType) : ValueTypeInfo(type) {
    constructor(proto: TypeProto.Sequence) : this(DataType.fromValue(proto.elem_type!!.tensor_type!!.elem_type!!)!!)
}
