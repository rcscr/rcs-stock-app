# Tapes*trie*

This project implements a `Trie`, a.k.a. `PrefixTree`, a data structure used for efficient string searching.

The analogy of a `Trie` data structure to a tapestry is quite fitting. Just as a tapestry is composed of many threads woven together to create a complete piece of art, a `Trie` organizes various threads of strings to form a cohesive structure for efficient retrieval. Each path through the `Trie` can be thought of as a thread contributing to the overall functionality.

The `Trie` implemented here is thread-safe, unit-tested, and able to efficiently retrieve data using any of the following strategies:
  - exact match (like a `Map`)
  - prefix match
  - substring match
  - "fuzzy" substring match with configurable error tolerance: Brasil will match Brazil; Raphael will match Rafael; etc

### Demo

A demo of an `HtmlCrawler` & `HtmlSearcher` have also been provided to illustrate the usage of the `Trie`.

Searching the Linux manual (1,860 HTML pages and 21,181 unique tokens) for `computer` with `errorTolerance=2` takes ~1 second and will return HTML pages containing any of these hits:

<pre>
[computer, computers, computerr1, compute, computed, computes, compuserve, comput, compiler, compugen, competes, compilers, computing, computation, compatgroup, computations, recomputes, minicomputer, deepcomputing]
</pre>

Some results, like `competes`, might seem irrelevant, but they are still acceptable matches given the `errorTolerance=2`. Notice that, if you swap the first `e` and `s` for `u` and `r` respectively, you'll have your keyword - with only two errors, as required.

As you might have noticed, these results are sorted by best match, considering the following information:
    
- matchedSubstring (String): *the minimum portion of the string that matched the keyword*
- matchedWord (String): *the whole word where the match was found*
- numberOfMatches (Int): *number of characters that matched*
- numberOfErrors (Int): *number of errors due to misspelling or letters missing*
- prefixDistance (Int): *the distance from the start of the match to the beginning of the word*
- matchedWholeString (Boolean): *whether the keyword perfectly matched the entire string stored in the Trie*
- matchedWholeWord (Boolean): *whether the keyword perfectly matched a whole word within the string*

As an example, let's examine the best and worst search hits above:

#### Best
<pre>
TrieSearchResult(
    string=computer, 
    value=[HtmlIndexEntry(url=htmlman8/agetty.8.html, occurrences=2), HtmlIndexEntry(url=htmlman3/rtime.3.html, occurrences=2), HtmlIndexEntry(url=gfdl-3.html, occurrences=1), ...], 
    matchedSubstring=computer, 
    matchedWord=computer, 
    numberOfMatches=8, 
    numberOfErrors=0, 
    prefixDistance=0, 
    matchedWholeString=true, 
    matchedWholeWord=true
)
</pre>

#### Worst
<pre>
TrieSearchResult(
    string=deepcomputing, 
    value=[HtmlIndexEntry(url=htmlman2/spu_run.2.html, occurrences=1), HtmlIndexEntry(url=htmlman2/spu_create.2.html, occurrences=1)], 
    matchedSubstring=comput, 
    matchedWord=deepcomputing, 
    numberOfMatches=6, 
    numberOfErrors=2, 
    prefixDistance=4, 
    matchedWholeString=false, 
    matchedWholeWord=false
)
</pre>

### Other notes

As an optimization, each node in the `Trie` stores its depth: the max size of a word stemming from it. This allowed me to implement a culling strategy to swiftly discard nodes whose strings are not long enough to provide a match. In general, searches for longer strings are faster, because fewer strings will be examined.

The example above is quite extreme with more than 20,000 strings. But even so, the fuzzy search took ~1 second, which is quite impressive. However, this in-memory `Trie` certainly has its limitations; for one, it is quite memory-intensive. In many scenarios, a solution like `ElasticSearch` should be used instead.

In general, the greater the error tolerance, the slower the performance, because there are more paths to explore. Furthermore, a shallow `Trie`, where each entry is short (i.e. words) offers the best performance, but with the limitation that you can only search for short strings. A `Trie` that stores longer text (i.e. sentences) allows searching for phrases (multiple words chained together), but is slower.

### Build & run demo

<pre>docker build . -t trie-demo</pre>

<pre>
docker run -p 4567:4567 --rm trie-demo
</pre>

Or just run `com.rcs.htmlcrawlerdemo.HtmlCrawlerDemo.kt`

<pre>
POST http://localhost:4567/search
Body: {"keyword": "computer", "strategy": "FUZZY", "errorTolerance": 2 }
</pre>