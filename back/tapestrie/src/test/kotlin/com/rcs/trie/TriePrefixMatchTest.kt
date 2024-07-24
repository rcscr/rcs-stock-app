package com.rcs.trie

import org.assertj.core.api.SoftAssertions
import kotlin.test.Test

class TriePrefixMatchTest {

    data class PrefixMatchScenario(
        val entries: Map<String, Int>,
        val search: String,
        val expectedResults: Map<String, Int>
    )

    @Test
    fun `test matchByPrefix with predefined scenarios`(): Unit = with(prefixMatchScenarios()) {
        val softAssertions = SoftAssertions()

        this.forEach { scenario ->
            // Arrange
            val trie = Trie<Int>()
            scenario.entries.forEach {
                trie.put(it.key, it.value)
            }

            // Act
            val result = trie.matchByPrefix(scenario.search)

            // Assert
            softAssertions.assertThat(result)
                .isEqualTo(scenario.expectedResults)
        }

        softAssertions.assertAll()
    }

    private fun prefixMatchScenarios(): List<PrefixMatchScenario> {
        val sharedData = mapOf(
            "Hello, Nomads!" to 1,
            "Hello, World!" to 2,
            "Hi there!" to 3,
            "Well, Hello" to 4
        )

        return listOf(
            PrefixMatchScenario(
                sharedData,
                "Hello",
                mapOf(
                    "Hello, Nomads!" to 1,
                    "Hello, World!" to 2
                )
            ),
            PrefixMatchScenario(
                sharedData,
                "Hi",
                mapOf(
                    "Hi there!" to 3
                )
            ),
            PrefixMatchScenario(
                sharedData,
                "H",
                mapOf(
                    "Hello, Nomads!" to 1,
                    "Hello, World!" to 2,
                    "Hi there!" to 3
                )
            ),
            PrefixMatchScenario(
                sharedData,
                "",
                mapOf(
                    "Hello, Nomads!" to 1,
                    "Hello, World!" to 2,
                    "Hi there!" to 3,
                    "Well, Hello" to 4
                )
            ),
            PrefixMatchScenario(
                sharedData,
                "O",
                mapOf()
            )
        )
    }
}