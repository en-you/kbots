package net.shilu.kbots.utilities

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

internal class JsonMapper(private val node: JsonNode?) {
    internal operator fun get(key: String): JsonMapper {
        return if (node?.isObject == true) JsonMapper(node.get(key))
        else JsonMapper(null)
    }

    internal fun put(key: String, item: Any) {
        if (node is ObjectNode) {
            if (item is JsonMapper) node.set(key, item.node)
            else node.set(key, mapper.valueToTree(item))
        } else {
            throw IllegalStateException("Mapper is not on Object")
        }
    }

    @Suppress("unused")
    internal fun format(): String {
        return node?.toPrettyString() ?: ""
    }

    companion object {
        private val mapper = newMapper()
        internal val EMPTY_OBJECT = this.parse("{}")

        @Suppress("MemberVisibilityCanBePrivate")
        internal fun parse(text: String): JsonMapper {
            return JsonMapper(mapper.readTree(text))
        }

        private fun newMapper(): ObjectMapper {
            val jsonFactory = JsonFactory()
            jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS)
            jsonFactory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            return ObjectMapper(jsonFactory)
        }
    }
}
