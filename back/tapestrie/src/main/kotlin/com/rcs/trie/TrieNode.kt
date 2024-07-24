package com.rcs.trie

class TrieNode<T>(
    val string: String,
    var value: T?,
    var depth: Int,
    val next: MutableSet<TrieNode<T>>,
    val previous: TrieNode<T>?
) {

    fun isRoot(): Boolean {
        return string == "" && previous == null
    }

    fun completes(): Boolean {
        return value != null
    }

    fun getNextNode(string: String): TrieNode<T>? {
        synchronized(next) {
            return next.firstOrNull { it.string == string }
        }
    }

    fun addNextNode(node: TrieNode<T>) {
        synchronized(next) {
            next.add(node)
        }
    }

    fun removeNextNode(string: String) {
        synchronized(next) {
            next.removeIf { it.string == string }
        }
    }
}