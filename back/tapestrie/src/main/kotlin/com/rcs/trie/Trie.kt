package com.rcs.trie

import kotlinx.coroutines.runBlocking
import kotlin.math.max
import kotlin.properties.Delegates

class Trie<T>: Iterable<TrieEntry<T>> {

    private val depthUpdateLock = Any()

    private lateinit var root: TrieNode<T>

    private var _size: Int by Delegates.vetoable(0) { _, _, newValue ->
        if (newValue < 0) {
            throw IllegalStateException("Size should never be < 0")
        } else {
            true
        }
    }

    val size: Int get() = _size

    init {
        clear()
    }

    fun clear() {
        _size = 0
        root = TrieNode("", null, 0, mutableSetOf(), null)
    }

    fun depth(): Int {
        return root.depth
    }

    fun isEmpty(): Boolean {
        return root.next.isEmpty()
    }

    override fun iterator(): Iterator<TrieEntry<T>> {
        return TrieIterator(root)
    }

    /**
     * Returns the previous value, if any, associated with this key (inputString)
     */
    fun put(inputString: String, value: T): T? {
        if (inputString.isEmpty()) {
            throw IllegalArgumentException("Cannot add an empty string")
        }

        var previousValue: T? = null

        synchronized(depthUpdateLock) {
            var current = root

            for (i in inputString.indices) {
                val reachedEndOfInput = i == inputString.length - 1
                val currentCharacter = inputString[i].toString()

                when (val nextMatchingNode = current.getNextNode(currentCharacter)) {
                    null -> {
                        // we do not have a string going this far, so we create a new node,
                        // and then keep appending the remaining characters of the input to it
                        val valueToInsert = if (reachedEndOfInput) value else null
                        val depth = inputString.length - i - 1
                        val nextNode = TrieNode(
                            currentCharacter, valueToInsert, depth, mutableSetOf(), previous = current)
                        current.addNextNode(nextNode)
                        current = nextNode
                    }
                    else -> {
                        // we are at the last character of the input
                        // we have a string going this far, so we modify it, setting it to complete
                        // (if its already complete, that means we have already inserted the same input before)
                        // see TrieBasicTest.testAddShorterAfter
                        if (reachedEndOfInput) {
                            previousValue = nextMatchingNode.value
                            nextMatchingNode.value = value

                        // there is a matching node, but we're not at the end of the input yet,
                        // so go on to the next character
                        } else {
                            current = nextMatchingNode
                        }
                    }
                }
            }

            updateDepths(current, current.next.maxByOrNull { it.depth })

            if (previousValue == null) { // = is update
                _size++
            }
        }

        return previousValue
    }

    /**
     * Returns the previous value, if any, associated with this key (inputString)
     */
    fun remove(inputString: String): T? {
        synchronized(depthUpdateLock) {
            var nodeToRemove = root

            for (element in inputString) {
                val currentCharacter = element.toString()
                when (val nextMatchingNode = nodeToRemove.getNextNode(currentCharacter)) {
                    null -> return null
                    else -> nodeToRemove = nextMatchingNode
                }
            }

            if (nodeToRemove.completes()) {
                // look back until we find the first node that is used for other strings
                // (nodes that are not used for other strings can be fully removed)
                var j = inputString.length
                var last = nodeToRemove
                do {
                    last = last.previous!!
                    j--
                } while (!last.isUsedForOtherStrings())

                // remove the character from node.next, thus completing the removal
                val charToUnlink = inputString[j].toString()
                last.removeNextNode(charToUnlink)

                // update depths to reflect the change
                updateDepths(last, last.next.maxByOrNull { it.depth })

                _size--

                return nodeToRemove.value
            }
        }

        return null
    }

    fun getExactly(string: String): T? {
        return prefixMatchUpTo(string)?.value
    }

    fun containsExactly(string: String): Boolean {
        return getExactly(string) != null
    }

    fun matchByPrefix(prefix: String): Map<String, T> {
        return prefixMatchUpTo(prefix)
            ?.let { gatherAll(it, prefix) }
            ?: mutableMapOf()
    }

    fun matchBySubstring(search: String): List<TrieSearchResult<T>> {
        return matchBySubstringFuzzy(search, 0, FuzzyMatchingStrategy.LIBERAL)
    }

    fun matchBySubstringFuzzy(
        search: String,
        errorTolerance: Int,
        matchingStrategy: FuzzyMatchingStrategy
    ): List<TrieSearchResult<T>> = runBlocking {
        FuzzySearcher.search(root, search, errorTolerance, matchingStrategy)
    }

    private fun prefixMatchUpTo(string: String): TrieNode<T>? {
        var current = root
        for (letter in string) {
            val currentCharacter = letter.toString()
            when (val nextNode = current.getNextNode(currentCharacter)) {
                null -> return null
                else -> current = nextNode
            }
        }
        return current
    }

    private fun gatherAll(start: TrieNode<T>, startSequence: String): MutableMap<String, T> {
        return start.next
            .flatMap {
                TrieIterator(it).asSequence()
            }
            .fold(mutableMapOf()) { map, entry ->
                map[startSequence + entry.string] = entry.value
                map
            }
    }

    private fun updateDepths(current: TrieNode<T>?, next: TrieNode<T>?) {
        current?.let {
            val maxDepth = current.next.filter { it != next }.maxOfOrNull { it.depth } ?: 0
            current.depth = max(next?.depth ?: 0, maxDepth) + current.string.length
            updateDepths(current.previous, current)
        }
    }

    private fun TrieNode<T>.isUsedForOtherStrings(): Boolean {
        return this === root || this.completes() || this.next.size > 1
    }
}