package com.rcs.trie

enum class FuzzyMatchingStrategy {
    // matches everywhere in the string, and allows errors in the beginning, middle, and end
    LIBERAL,

    // similar to LIBERAL and matches a * to any character, without considering it as an error
    WILDCARD,

    // matches only words that start with the first letter of the keyword
    EXACT_PREFIX,

    // similar to EXACT_PREFIX, but allows errors in the beginning, middle, and end
    FUZZY_PREFIX,

    // similar to EXACT_PREFIX, but allows errors only at the end
    FUZZY_POSTFIX,

    // accepts only errors due to adjacent letter swaps (i.e. typos)
    ADJACENT_SWAP,

    // accepts only errors due to letter swaps anywhere in the string
    // (i.e. symmetrical spoonerisms)
    // TODO: Implement asymmetrical spoonerism match
    SYMMETRICAL_SWAP,

    // matches strings containing words that form the acronym provided
    ACRONYM
}