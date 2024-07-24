package com.rcs.trie

import org.assertj.core.api.Assertions.*
import kotlin.test.Test

class TrieBasicTest {

    @Test
    fun testClear() {
        // Arrange
        val trie = Trie<Int>()
        trie.put("Hello", 123)
        assertThat(trie.getExactly("Hello")).isEqualTo(123)

        // Act
        trie.clear()

        // Assert
        assertThat(trie.isEmpty()).isTrue()
        assertThat(trie.size).isEqualTo(0)
    }

    @Test
    fun testAdd() {
        // Arrange
        val trie = Trie<Int>()

        // Act
        trie.put("Hello, Nomads!", 123)

        // Assert
        assertThat(trie.size).isEqualTo(1)
        assertThat(trie.containsExactly("Hello")).isFalse()
        assertThat(trie.containsExactly("Hello, World!")).isFalse()
        assertThat(trie.containsExactly("Hello, Nomads!")).isTrue()
    }

    @Test
    fun testUpdate() {
        // Arrange
        val trie = Trie<Int>()
        trie.put("Hello", 123)

        // Act
        val previousValue = trie.put("Hello", 456)

        // Assert
        assertThat(trie.size).isEqualTo(1)
        assertThat(previousValue).isEqualTo(123)
        assertThat(trie.getExactly("Hello")).isEqualTo(456)
    }

    @Test
    fun testAddShorterAfter() {
        // Arrange
        val trie = Trie<Int>()

        // Act
        trie.put("123456", 1)
        trie.put("12345", 2)
        trie.put("1234", 3)

        // Assert
        assertThat(trie.size).isEqualTo(3)
        assertThat(trie.containsExactly("123456")).isTrue()
        assertThat(trie.containsExactly("12345")).isTrue()
        assertThat(trie.containsExactly("1234")).isTrue()
    }

    @Test
    fun testAddLongerAfter() {
        // Arrange
        val trie = Trie<Int>()

        // Act
        trie.put("1234", 1)
        trie.put("12345", 2)
        trie.put("123456", 3)

        // Assert
        assertThat(trie.size).isEqualTo(3)
        assertThat(trie.containsExactly("1234")).isTrue()
        assertThat(trie.containsExactly("12345")).isTrue()
        assertThat(trie.containsExactly("123456")).isTrue()
    }

    @Test
    fun testGetExactly() {
        // Arrange
        val trie = Trie<Int>()
        trie.put("123", 1)
        trie.put("456", 4)
        trie.put("789", 7)

        // Act
        val result = trie.getExactly("456")

        // Assert
        assertThat(result).isEqualTo(4)
    }

    @Test
    fun testGetExactlyNonExistent() {
        // Arrange
        val trie = Trie<Int>()
        trie.put("123", 1)
        trie.put("456", 4)
        trie.put("789", 7)

        // Act
        val result = trie.getExactly("000")

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun testRemove() {
        // Arrange
        val trie = Trie<Int>()
        trie.put("Hello, Nomads!", 1)
        trie.put("Hello, World!", 1)

        // Act
        val result = trie.remove("Hello, Nomads!")

        // Assert
        assertThat(trie.size).isEqualTo(1)
        assertThat(result).isEqualTo(1)
        assertThat(trie.containsExactly("Hello, Nomads!")).isFalse()
        assertThat(trie.containsExactly("Hello, World!")).isTrue()
    }

    @Test
    fun testRemoveNonExistent() {
        // Arrange
        val trie = Trie<Int>()
        trie.put("Hello, Nomads!", 1)
        trie.put("Hello, World!", 1)

        // Act
        val result = trie.remove("Hello, People!")

        // Assert
        assertThat(trie.size).isEqualTo(2)
        assertThat(result).isNull()
        assertThat(trie.containsExactly("Hello, Nomads!")).isTrue()
        assertThat(trie.containsExactly("Hello, World!")).isTrue()
    }

    @Test
    fun testDepthEmpty() {
        // Arrange
        val trie = Trie<Unit>()

        // Act
        val result = trie.depth()

        // Assert
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun testDepthOnAdd() {
        // Arrange
        val trie = Trie<Unit>()
        trie.put("1", Unit)
        trie.put("12", Unit)
        trie.put("123", Unit)
        trie.put("1234", Unit)
        trie.put("a", Unit)
        trie.put("ab", Unit)

        // Act
        val result = trie.depth()

        // Assert
        assertThat(result).isEqualTo(4)
    }

    @Test
    fun testDepthOnRemove() {
        // Arrange
        val trie = Trie<Unit>()
        trie.put("1", Unit)
        trie.put("12", Unit)
        trie.put("123", Unit)
        trie.put("1234", Unit)
        trie.put("a", Unit)
        trie.put("ab", Unit)

        // Act
        val removed = trie.remove("1234")
        val result = trie.depth()

        // Assert
        assertThat(removed).isNotNull()
        assertThat(result).isEqualTo(3)
    }
}