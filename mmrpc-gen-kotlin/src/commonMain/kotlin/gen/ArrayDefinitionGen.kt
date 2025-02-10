package org.cufy.mmrpc.gen.kotlin.gen

import org.cufy.mmrpc.ArrayDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.asClassName
import org.cufy.mmrpc.gen.kotlin.common.classOf
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.typealiasSpec

class ArrayDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is ArrayDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateTypealias(element)
            }
        }
    }

    private fun applyCreateTypealias(element: ArrayDefinition) {
        /*
        <namespace> {
            <kdoc>
            [ @<metadata> ]
            @Serializable()
            @SerialName("<canonical-name>")
            typealias <name> = List< <class-of-type> >
        }
        */

        injectFile(element.canonicalName) {
            addTypeAlias(typealiasSpec(asClassName(element), classOf(element.type)))
        }
    }
}
