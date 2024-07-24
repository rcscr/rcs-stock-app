package com.rcs.trie

class TrieSearchResultComparator {

    companion object {

        private val byNumberOfMatchesMoreFirst: Comparator<TrieSearchResult<*>> =
            compareBy(TrieSearchResult<*>::numberOfMatches).reversed()

        private val byLengthOfStringShortestFirst: Comparator<TrieSearchResult<*>> =
            compareBy { it.string.length }

        private val byMatchedSequenceTrueFirst: Comparator<TrieSearchResult<*>> =
            compareBy(TrieSearchResult<*>::matchedWholeWord).reversed()

        private val byPrefixDistanceShortestFirst: Comparator<TrieSearchResult<*>> =
            compareBy(TrieSearchResult<*>::prefixDistance)

        private val byWordLengthShortestFirst: Comparator<TrieSearchResult<*>> =
            compareBy { it.matchedWord.length }

        private val byMatchedWholeWordTrueFirst: Comparator<TrieSearchResult<*>> =
            compareBy(TrieSearchResult<*>::matchedWholeWord).reversed()

        private val byNumberOfErrorsLessFirst: Comparator<TrieSearchResult<*>> =
            compareBy { it.numberOfErrors }

        val byBestMatchFirst: Comparator<TrieSearchResult<*>> =
            byPrefixDistanceShortestFirst
                .thenComparing(byNumberOfMatchesMoreFirst)
                .thenComparing(byWordLengthShortestFirst)
                .thenComparing(byMatchedSequenceTrueFirst)
                .thenComparing(byMatchedWholeWordTrueFirst)
                .thenComparing(byLengthOfStringShortestFirst)
                .thenComparing(byNumberOfErrorsLessFirst)
    }
}