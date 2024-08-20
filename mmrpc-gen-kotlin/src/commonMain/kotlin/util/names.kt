package org.cufy.mmrpc.gen.kotlin.util

/**
 * For any element that has a member field holding its mmrpc reflection info,
 * this is the name of said member field.
 */
const val F_OBJECT_INFO = "__info__"

/**
 * For any element that has a static field holding its mmrpc reflection info,
 * this is the name of said static field.
 */
const val F_STATIC_INFO = "__INFO__"

/**
 * For any element that has a static field holding its runtime value,
 * this is the name of said static field.
 */
const val F_STATIC_VALUE = "VALUE"

/**
 * For any element that has a static field holding its name,
 * this is the name of said static field.
 */
const val F_STATIC_NAME = "NAME"

/**
 * For any element that has a static field holding its canonical name,
 * this is the name of said static field.
 */
const val F_STATIC_CANONICAL_NAME = "CANONICAL_NAME"

/**
 * For any element that has a static field holding its `path` value,
 * this is the name of said static field.
 */
const val F_STATIC_PATH = "PATH"

/**
 * For any element that has a static field holding its `topic` value,
 * this is the name of said static field.
 */
const val F_STATIC_TOPIC = "TOPIC"
