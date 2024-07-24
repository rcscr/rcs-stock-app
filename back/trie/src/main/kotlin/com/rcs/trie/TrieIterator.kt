package com.rcs.trie

class TrieIterator<T>(root: TrieNode<T>): Iterator<TrieEntry<T>> {

    private val queue = ArrayDeque<Pair<TrieNode<T>, String>>()
    private var next: TrieEntry<T>? = null

    init {
        queue.add(Pair(root, root.string))
        setNext()
    }

    override fun hasNext(): Boolean {
        return next != null
    }

    override fun next(): TrieEntry<T> {
        val toReturn = next
        setNext()
        return toReturn!!
    }

    private fun setNext() {
        // breadth-first search: returns strings from shortest to longest
        while(queue.isNotEmpty()) {
            val (node, sequence) = queue.removeFirst()
            for (next in node.next) {
                queue.add(Pair(next, sequence + next.string))
            }
            if (node.completes()) {
                next = TrieEntry(sequence, node.value!!)
                return
            }
        }
        next = null
    }
}