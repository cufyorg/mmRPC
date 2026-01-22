package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.*
import net.pearx.kasechange.toCamelCase
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug
import org.cufy.mmrpc.gen.kotlin.util.createCall
import org.cufy.mmrpc.runtime.FaultException

////////////////////////////////////////

@ContextScope
context(ctx: Context)
fun RoutineDefinition.nameOfFunction(): String {
    if (GenFeature.KEEP_ROUTINE_FUNCTION_NAMES in ctx.features)
        return name

    return name.toCamelCase()
}

@ContextScope
context(ctx: Context)
fun RoutineDefinition.generatedClassName(): ClassName {
    debug { check(hasGeneratedClass()) }
    val pkg = canonicalName.assumedPackageName()
    val simpleNames = canonicalName.assumedSimpleNames()
    return ClassName(pkg, simpleNames)
}

////////////////////////////////////////

@ContextScope
context(_: Context)
fun RoutineDefinition.serverRegisterCode(
    register: MemberName,
    handler: CodeBlock,
): CodeBlock {
    return CodeBlock.of(
        "%M(%T.CANONICAL_NAME, %L)",
        register,
        generatedClassName(),
        handler,
    )
}

@ContextScope
context(_: Context)
fun RoutineDefinition.serverDirectRegisterCode(
    condition: CodeBlock,
    register: MemberName,
    wrap: MemberName,
    handler: CodeBlock,
): CodeBlock {
    return buildCodeBlock {
        beginControlFlow("if (%L)", condition)
        addStatement(
            "%M(%T.CANONICAL_NAME, %M(%L))",
            register,
            generatedClassName(),
            wrap,
            handler,
        )
        endControlFlow()
    }
}

////////////////////////////////////////

@ContextScope
context(_: Context)
fun RoutineDefinition.clientStubImplCode(
    engine: CodeBlock,
    exec: MemberName,
): CodeBlock {
    return buildCodeBlock {
        if (faults.isEmpty()) {
            addStatement(
                "♢return %M(%L, %T.CANONICAL_NAME, request)",
                exec, engine, generatedClassName(),
            )
        } else {
            beginControlFlow("try")
            addStatement(
                "return %M(%L, %T.CANONICAL_NAME, request)",
                exec, engine, generatedClassName(),
            )
            endControlFlow()
            beginControlFlow("catch (e: %T)", FaultException::class)
            beginControlFlow("when (e.canonicalName)")
            faults.forEach { fault ->
                addStatement(
                    "%T.CANONICAL_NAME -> throw %T(e)",
                    fault.generatedClassName(),
                    fault.generatedClassName(),
                )
            }
            addStatement("else -> throw e")
            endControlFlow()
            endControlFlow()
        }
    }
}

@ContextScope
context(_: Context)
fun RoutineDefinition.clientFlatImplCode(
    request: TypeName,
    fields: List<String>,
): CodeBlock {
    return buildCodeBlock {
        addStatement("♢return %L(\n⇥%L⇤\n)", nameOfFunction(), createCall(
            function = CodeBlock.of("%T", request),
            parameters = fields.associateWith {
                CodeBlock.of(it)
            },
        ))
    }
}

////////////////////////////////////////
