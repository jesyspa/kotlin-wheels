package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HeapTest {

    @Test
    fun emptyHeapTest() {
        val h = heap<Int>()
        assertEquals(null, h.current)
    }

    @Test
    fun heapSortTest() {

        val random = Random(42)

        run {
            val data = (0..500).toMutableList()
            data.shuffle(random)

            val heap: Heap<Int> = heap()
            heap += data

            val heapSorted = sequence { while(heap.isNotEmpty()) yield(heap.take()) }.toList()

            assertEquals(data.sorted(), heapSorted)
        }

        run {
            val cmp = compareBy { it: Int -> "$it$it"  }
            val data = (0..500).toMutableList()
            data.shuffle(random)

            val heap = heap(cmp)
            heap += data

            val heapSorted = sequence { while(heap.isNotEmpty()) yield(heap.take()) }.toList()

            assertEquals(data.sortedWith(cmp), heapSorted)
        }

    }

}
