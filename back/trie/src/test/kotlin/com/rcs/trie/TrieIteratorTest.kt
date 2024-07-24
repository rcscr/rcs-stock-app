package com.rcs.trie

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class TrieIteratorTest {

    @Test
    fun testIterator() {
        // Arrange
        val trie = Trie<Int>()
        trie.put("Hey", 0)
        trie.put("Oi", 1)
        trie.put("Coucou", 2)
        trie.put("Hallo", 3)
        trie.put("Konnichiwa", 4)
        trie.put("Hujambo", 5)

        // Act
        val iterated = mutableListOf<TrieEntry<Int>>()

        for (entry in trie) {
            iterated.add(entry)
        }

        // Assert
        assertThat(iterated).containsExactly(
            TrieEntry("Oi", 1),
            TrieEntry("Hey", 0),
            TrieEntry("Hallo", 3),
            TrieEntry("Coucou", 2),
            TrieEntry("Hujambo", 5),
            TrieEntry("Konnichiwa", 4),
        )
    }
}