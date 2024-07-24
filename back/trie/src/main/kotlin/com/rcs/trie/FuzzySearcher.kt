package com.rcs.trie

import kotlinx.coroutines.*

class FuzzySearcher {

    companion object {

        private val updateLock = Any()

        suspend fun <T> search(
            root: TrieNode<T>,
            search: String,
            errorTolerance: Int,
            matchingStrategy: FuzzyMatchingStrategy
        ): List<TrieSearchResult<T>> = coroutineScope {

            if (search.isEmpty() || errorTolerance < 0 || errorTolerance > search.length) {
                throw IllegalArgumentException()
            }

            val initialStates = FuzzySearchState.getInitialStates(
                root, search, errorTolerance, matchingStrategy, true)
            val results = mutableMapOf<String, TrieSearchResult<T>>()

            // Parallelizes only top-level of the Trie:
            // one coroutine for each state derived from each node directly beneath the root
            val topLevelStates = initialStates
                .map { it.nextStates() }
                .flatten()

            val jobs = topLevelStates.map {
                launch(Dispatchers.Default) {
                    searchJob(it, results)
                }
            }

            jobs.forEach { it.join() }

            // clean up resources to prevent memory hoarding
            System.gc()

            results.values.sortedWith(TrieSearchResultComparator.byBestMatchFirst)
        }

        private fun <T> searchJob(
            initialState: FuzzySearchState<T>,
            results: MutableMap<String, TrieSearchResult<T>>
        ) {
            val queue = ArrayDeque<FuzzySearchState<T>>()
            queue.add(initialState)

            while (queue.isNotEmpty()) {
                val state = queue.removeFirst()

                if (state.hasSearchResult()) {
                    val searchResult = state.buildSearchResult()
                    results.putOnlyNewOrBetter(searchResult)
                }

                queue.addAll(state.nextStates())
            }
        }

        private fun <T> MutableMap<String, TrieSearchResult<T>>.putOnlyNewOrBetter(newMatch: TrieSearchResult<T>) {
            synchronized(updateLock) {
                this[newMatch.string] = when (val existing = this[newMatch.string]) {
                    null -> newMatch
                    else -> {
                        val compareResult = TrieSearchResultComparator.byBestMatchFirst.compare(newMatch, existing)
                        val newMatchIsBetter = compareResult == -1
                        when {
                            newMatchIsBetter -> newMatch
                            else -> existing
                        }
                    }
                }
            }
        }
    }
}