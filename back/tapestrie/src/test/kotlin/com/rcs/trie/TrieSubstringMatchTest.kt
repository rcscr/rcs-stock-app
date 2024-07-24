package com.rcs.trie

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class TrieSubstringMatchTest {

    data class SubstringMatchScenario(
        val entries: Set<String>,
        val search: String,
        val expectedResults: List<TrieSearchResult<Unit>>
    )

    private fun runTestScenario(scenario: SubstringMatchScenario) {
        // Arrange
        val trie = Trie<Unit>()
        scenario.entries.forEach {
            trie.put(it, Unit)
        }

        // Act
        val result = trie.matchBySubstring(scenario.search)

        // Assert
        assertThat(result)
            .isEqualTo(scenario.expectedResults)
    }

    @Test
    fun `matches a prefix of length 1`() {
        val scenario = SubstringMatchScenario(
            setOf("abcdef", "hijklm"),
            "a",
            listOf(TrieSearchResult("abcdef", Unit, "a", "abcdef", 1, 0, 0, false, false))
        )
        runTestScenario(scenario)
    }

    @Test
    fun `matches a prefix of length greater than 1`() {
        val scenario = SubstringMatchScenario(
            setOf("abcdef", "defghi"),
            "def",
            listOf(
                TrieSearchResult("defghi", Unit, "def", "defghi", 3, 0, 0, false, false),
                TrieSearchResult("abcdef", Unit, "def", "abcdef", 3, 0, 3, false, false)
            )
        )
        runTestScenario(scenario)
    }

    @Test
    fun `matches a postfix`() {
        val scenario = SubstringMatchScenario(
            setOf("defghi", "jklmno"),
            "ghi",
            listOf(TrieSearchResult("defghi", Unit, "ghi", "defghi", 3, 0, 3, false, false))
        )
        runTestScenario(scenario)
    }

    @Test
    fun `matches a string within`() {
        val scenario = SubstringMatchScenario(
            setOf("deghij", "jklmno"),
            "ghi",
            listOf(TrieSearchResult("deghij", Unit, "ghi", "deghij", 3, 0, 2, false, false))
        )
        runTestScenario(scenario)
    }

    @Test
    fun `matches the whole sequence`() {
        val scenario = SubstringMatchScenario(
            setOf("jklmno", "jklmnp"),
            "jklmno",
            listOf(TrieSearchResult("jklmno", Unit, "jklmno", "jklmno", 6, 0, 0, true, true))
        )
        runTestScenario(scenario)
    }

    @Test
    fun `matches after an initial failed attempt`() {
        val scenario = SubstringMatchScenario(
            setOf("pqrpqs"),
            "pqs",
            listOf(TrieSearchResult("pqrpqs", Unit, "pqs", "pqrpqs", 3, 0, 3, false, false))
        )
        runTestScenario(scenario)
    }

    @Test
    fun `matches whole word`() {
        val scenario = SubstringMatchScenario(
            setOf("tu vw, xyz"),
            "vw",
            listOf(TrieSearchResult("tu vw, xyz", Unit, "vw", "vw", 2, 0, 0, false, true))
        )
        runTestScenario(scenario)
    }

    @Test
    fun `does not match partial match`() {
        val scenario = SubstringMatchScenario(
            setOf("123"),
            "234",
            listOf()
        )
        runTestScenario(scenario)
    }
}