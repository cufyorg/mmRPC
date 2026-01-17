package org.cufy.mmrpc.gen.kotlin.common.model

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlinx.coroutines.flow.Flow
import net.pearx.kasechange.toCamelCase
import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.assumedPackageName
import org.cufy.mmrpc.gen.kotlin.common.assumedSimpleNames
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.debug
import org.cufy.mmrpc.gen.kotlin.util.createCall
import org.cufy.mmrpc.gen.kotlin.util.funSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.runtime.FaultException
import org.cufy.mmrpc.runtime.ServerEngine

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
fun RoutineDefinition.abstractFunSpec(): FunSpec {
    val request: TypeName
    val response: TypeName

    when (comm) {
        Comm.VoidUnary -> {
            request = output.className()
            response = UNIT
        }

        Comm.UnaryVoid -> {
            request = input.className()
            response = UNIT
        }

        Comm.UnaryUnary -> {
            request = input.className()
            response = output.className()
        }

        Comm.UnaryStream -> {
            request = input.className()
            response = Flow::class.asClassName()
                .parameterizedBy(output.className())
        }

        Comm.StreamUnary -> {
            request = Flow::class.asClassName()
                .parameterizedBy(input.className())
            response = output.className()
        }

        Comm.StreamStream,
        -> {
            request = Flow::class.asClassName()
                .parameterizedBy(input.className())
            response = Flow::class.asClassName()
                .parameterizedBy(output.className())
        }
    }

    return funSpec(nameOfFunction()) {
        addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
        addKdoc(createKdocCode(this@abstractFunSpec))
        addParameter("request", request)
        returns(response)
    }
}

@ContextScope
context(_: Context)
fun RoutineDefinition.clientExecImplFunSpec(): FunSpec {
    val request: TypeName
    val response: TypeName
    val n: Int // function overload number

    when (comm) {
        Comm.VoidUnary -> {
            request = output.className()
            response = UNIT
            n = 0
        }

        Comm.UnaryVoid -> {
            request = input.className()
            response = UNIT
            n = 0
        }

        Comm.UnaryUnary -> {
            request = input.className()
            response = output.className()
            n = 1
        }

        Comm.UnaryStream -> {
            request = input.className()
            response = Flow::class.asClassName()
                .parameterizedBy(output.className())
            n = 2
        }

        Comm.StreamUnary -> {
            request = Flow::class.asClassName()
                .parameterizedBy(input.className())
            response = output.className()
            n = 3
        }

        Comm.StreamStream,
        -> {
            request = Flow::class.asClassName()
                .parameterizedBy(input.className())
            response = Flow::class.asClassName()
                .parameterizedBy(output.className())
            n = 4
        }
    }

    return funSpec(nameOfFunction()) {
        addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)

        addParameter("request", request)
        returns(response)

        // Implementation
        if (faults.isEmpty()) {
            addStatement(
                "♢return %M(this.engine, %T.CANONICAL_NAME, request)",
                MemberName(
                    packageName = "org.cufy.mmrpc.runtime",
                    simpleName = "exec$n",
                ),
                generatedClassName(),
            )
        } else {
            beginControlFlow("try")
            addStatement(
                "return %M(this.engine, %T.CANONICAL_NAME, request)",
                MemberName(
                    packageName = "org.cufy.mmrpc.runtime",
                    simpleName = "exec$n",
                ),
                generatedClassName(),
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
fun RoutineDefinition.serverRegisterImplCode(handler: CodeBlock): CodeBlock {
    val n: Int // function overload number

    when (comm) {
        Comm.VoidUnary -> {
            n = 0
        }

        Comm.UnaryVoid -> {
            n = 0
        }

        Comm.UnaryUnary -> {
            n = 1
        }

        Comm.UnaryStream -> {
            n = 2
        }

        Comm.StreamUnary -> {
            n = 3
        }

        Comm.StreamStream,
        -> {
            n = 4
        }
    }

    return CodeBlock.of(
        "%M(%T.CANONICAL_NAME, %L)",
        MemberName(
            packageName = "org.cufy.mmrpc.runtime",
            simpleName = "register$n",
        ),
        generatedClassName(),
        handler,
    )
}

@ContextScope
context(_: Context)
fun RoutineDefinition.clientFlatInputExecFunSpec(receiver: ClassName): FunSpec {
    val request: StructDefinition
    val response: TypeName

    when (comm) {
        Comm.VoidUnary -> {
            request = output
            response = UNIT
        }

        Comm.UnaryVoid -> {
            request = input
            response = UNIT
        }

        Comm.UnaryUnary -> {
            request = input
            response = output.className()
        }

        Comm.UnaryStream -> {
            request = input
            response = Flow::class.asClassName()
                .parameterizedBy(output.className())
        }

        Comm.StreamUnary,
        Comm.StreamStream,
        -> error("Cannot create shortcut function for routines with stream input")
    }

    return funSpec(nameOfFunction()) {
        addModifiers(KModifier.SUSPEND)
        receiver(receiver)

        val fields = request.collectAllFields()
        fields.forEach { field ->
            val name = field.nameOfProperty()
            addParameter(name, field.type.typeName())
        }
        returns(response)

        // Implementation
        addStatement("♢return %L(\n⇥%L⇤\n)", nameOfFunction(), createCall(
            function = CodeBlock.of("%T", request.className()),
            parameters = fields.associate {
                val name = it.nameOfProperty()
                name to CodeBlock.of(name)
            }
        ))
    }
}

@OptIn(ExperimentalKotlinPoetApi::class)
@ContextScope
context(_: Context)
fun RoutineDefinition.serverDirectRegisterFunSpec(receiver: ClassName): FunSpec {
    val request: TypeName
    val response: TypeName
    val n: Int // function overload number

    when (comm) {
        Comm.VoidUnary -> {
            request = output.className()
            response = UNIT
            n = 0
        }

        Comm.UnaryVoid -> {
            request = input.className()
            response = UNIT
            n = 0
        }

        Comm.UnaryUnary -> {
            request = input.className()
            response = output.className()
            n = 1
        }

        Comm.UnaryStream -> {
            request = input.className()
            response = Flow::class.asClassName()
                .parameterizedBy(output.className())
            n = 2
        }

        Comm.StreamUnary,
        -> {
            request = Flow::class.asClassName()
                .parameterizedBy(input.className())
            response = output.className()
            n = 3
        }

        Comm.StreamStream,
        -> {
            request = Flow::class.asClassName()
                .parameterizedBy(input.className())
            response = Flow::class.asClassName()
                .parameterizedBy(output.className())
            n = 4
        }
    }

    return funSpec(nameOfFunction()) {
        contextParameter("engine", ServerEngine::class)
        receiver(receiver)
        addParameter(
            "handler",
            LambdaTypeName.get(
                parameters = listOf(
                    parameterSpec("request", request)
                ),
                returnType = response,
            ).copy(suspending = true)
        )

        // Implementation
        beginControlFlow("if (engine.is%LSupported())", n)
        addStatement("%L", serverRegisterImplCode(
            handler = CodeBlock.of("handler"),
        ))
        endControlFlow()
    }
}

////////////////////////////////////////
