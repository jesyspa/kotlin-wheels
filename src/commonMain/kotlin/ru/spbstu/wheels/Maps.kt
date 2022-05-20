package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.jvm.JvmName

open class SimpleEntry<out K, out V>(override val key: K, override val value: V) : Map.Entry<K, V> {

    override fun equals(other: Any?): Boolean =
            other is Map.Entry<*, *> && key == other.key && value == other.value

    override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    override fun toString(): String = "$key=$value"
}

abstract class SimpleMutableEntry<K, V>(key: K, value: V): SimpleEntry<K, V>(key, value), MutableMap.MutableEntry<K, V> {
    abstract override fun setValue(newValue: V): V
}

inline fun <K, V> SimpleMutableEntry(key: K,
                                     value: V,
                                     crossinline setter: (V) -> V) = object : SimpleMutableEntry<K, V>(key, value) {
    override fun setValue(newValue: V): V = setter(newValue)
}

@Suppress(Warnings.UNCHECKED_CAST)
fun <K, V> Map<K, V>.getEntry(key: K): Map.Entry<K, V>? {
    val v = get(key)
    return when {
        v != null || key in this -> SimpleEntry(key, v as V)
        else -> null
    }
}

@Suppress(Warnings.UNCHECKED_CAST)
@JvmName("getOptionNonNullable")
fun <K, V: Any> Map<K, V>.getOption(key: K): Option<V> {
    return Option.ofNullable(this[key])
}

@Suppress(Warnings.UNCHECKED_CAST)
fun <K, V> Map<K, V>.getOption(key: K): Option<V> {
    val v = get(key)
    return when {
        v != null || key in this -> Option.just(v as V)
        else -> Option.empty()
    }
}

fun <K, V, M : MutableMap<K, V>> Iterable<Map.Entry<K, V>>.toMap(m: M): M =
        m.apply { this@toMap.forEach { put(it.key, it.value) } }

@Suppress(Warnings.USELESS_CAST)
fun <K, V> Iterable<Map.Entry<K, V>>.toMap(): Map<K, V> = when (this) {
    is Collection -> (this as Collection<Map.Entry<K, V>>).toMap()
    else -> toMap(mutableMapOf())
}

fun <K, V> Collection<Map.Entry<K, V>>.toMap(): Map<K, V> = when (size) {
    0 -> mapOf()
    1 -> first().let { mapOf(it.key to it.value) }
    else -> toMap(LinkedHashMap(size))
}

fun <K, V, M : MutableMap<K, V>> Sequence<Map.Entry<K, V>>.toMap(m: M): M =
        m.apply { this@toMap.forEach { put(it.key, it.value) } }

fun <K, V> Sequence<Map.Entry<K, V>>.toMap(): Map<K, V> = toMap(mutableMapOf())

@JvmName("pairsToMutableMap")
fun <K, V> Iterable<Pair<K, V>>.toMutableMap(): MutableMap<K, V> = toMap(mutableMapOf())

@JvmName("pairsToMutableMap")
fun <K, V> Sequence<Pair<K, V>>.toMutableMap(): MutableMap<K, V> = toMap(mutableMapOf())

fun <K, V> Iterable<Map.Entry<K, V>>.toMutableMap(): MutableMap<K, V> = toMap(mutableMapOf())
fun <K, V> Sequence<Map.Entry<K, V>>.toMutableMap(): MutableMap<K, V> = toMap(mutableMapOf())

fun <K, V, A : Appendable> Map<K, V>.joinTo(
        buffer: A,
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((K, V) -> CharSequence)? = null
): A {
    buffer.append(prefix)
    var count = 0
    for (entry in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            if (transform != null) buffer.append(transform(entry.key, entry.value))
            else buffer.append("$entry")
        } else break
    }
    if (limit in 0 until count) buffer.append(truncated)
    buffer.append(postfix)
    return buffer
}

fun <K, V> Map<K, V>.joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((K, V) -> CharSequence)? = null): String {
    return joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()
}

inline fun <K, V, reified B> Map<K, V>.mapToArray(body: (Map.Entry<K, V>) -> B): Array<B> {
    val arr = arrayOfNulls<B>(size)
    var i = 0
    for(e in this) arr[i++] = body(e)
    @Suppress(Warnings.UNCHECKED_CAST)
    return arr as Array<B>
}

fun <A, B, M: MutableMap<A, B>> Iterable<A>.zipTo(that: Iterable<B>, to: M): M {
    val thisIt = this.iterator()
    val thatIt = that.iterator()
    while(thisIt.hasNext() && thatIt.hasNext()) {
        to.put(thisIt.next(), thatIt.next())
    }
    return to
}

@Suppress(Warnings.NOTHING_TO_INLINE)
inline fun <K, V> Map<K, V>.asMap(): Map<K, V> = this


inline fun <K, V, C1: MutableMap<K, V>, C2: MutableMap<K, V>> Map<K, V>.partitionTo(
    c1: C1, c2: C2, body: (K, V) -> Boolean
): Pair<C1, C2> {
    for ((k, v) in this) {
        if (body(k, v)) {
            c1.put(k, v)
        } else {
            c2.put(k, v)
        }
    }
    return Pair(c1, c2)
}

inline fun <K, V> Map<K, V>.partition(body: (K, V) -> Boolean): Pair<Map<K, V>, Map<K, V>> =
    partitionTo(mutableMapOf(), mutableMapOf(), body)
