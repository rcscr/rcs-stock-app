package rcs.stock.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * See the latest version of this project here:
 * https://github.com/raphael-correa-ng/Tapestrie
 */
public class Trie<T> {

    private final Comparator<SearchResult<T>> sortByLengthOfMatchLongestFirst =
            Comparator.<SearchResult<T>>comparingInt(SearchResult::lengthOfMatch).reversed();

    private final Comparator<SearchResult<T>> sortByMatchedWholeWordTrueFirst =
            Comparator.comparing(
                    Trie.SearchResult::matchedWholeWord,
                    (bool1, bool2) -> bool1 == bool2 ? 0 : bool1 ? -1 : 1);

    private final Comparator<SearchResult<T>> sortByBestMatchFirst =
            sortByLengthOfMatchLongestFirst.thenComparing(sortByMatchedWholeWordTrueFirst);

    private static final String wholeWordSeparator = "[^a-zA-Z\\d:]";

    public record SearchResult<T>(
            String string,
            T value,
            int lengthOfMatch,
            boolean matchedWholeWord) { }

    private record Node<T>(String string, T value, Set<Node<T>> next) {
        public boolean completes() {
            return value != null;
        }
    }

    private final Node<T> root;

    public Trie() {
        root = new Node<>("", null, new HashSet<>());
    }

    public void put(String input, T value) {
        Node<T> current = root;

        for (int i = 0; i < input.length(); i++) {
            boolean reachedEndOfInput = i == input.length() - 1;

            String currentCharacter = Character.toString(input.charAt(i));

            synchronized(current.next()) {
                Node<T> nextMatchingNode = current.next()
                        .stream()
                        .filter(node -> node.string().equals(currentCharacter))
                        .findAny()
                        .orElse(null);

                // we do not have a string going this far, so we create a new node,
                // and then keep appending the remaining characters of the input to it
                if (null == nextMatchingNode) {
                    Node<T> next = new Node<>(currentCharacter, reachedEndOfInput ? value : null, new HashSet<>());
                    current.next().add(next);
                    current = next;

                // we are at the last character of the input
                // we have a string going this far, so we modify it, setting it to complete
                // (if its already complete, that means we have already inserted the same input before)
                // see TrieTest.testAddShorterAfter
                } else if (reachedEndOfInput && nextMatchingNode.value() == null) {
                    Node<T> completed = new Node<T>(nextMatchingNode.string(), value, nextMatchingNode.next());
                    current.next().removeIf(node -> node.string().equals(nextMatchingNode.string()));
                    current.next().add(completed);

                // there is a matching node, but we're not at the end of the input yet,
                // so go on to the next character
                } else {
                    current = nextMatchingNode;
                }
            }
        }
    }

    public void remove(String input) {
        Node<T> current = root;

        Deque<Node<T>> deque = new ArrayDeque<>(input.length() + 1);
        deque.add(root);

        for (int i = 0; i < input.length(); i++) {
            String currentCharacter = Character.toString(input.charAt(i));

            Optional<Node<T>> nextMatchingNode;

            synchronized (current.next()) {
                nextMatchingNode = current.next()
                        .stream()
                        .filter(node -> node.string().equals(currentCharacter))
                        .findAny();
            }

            // input does not exist
            if (nextMatchingNode.isEmpty()) {
                return;
            }

            current = nextMatchingNode.get();
            deque.add(current);
        }

        Node<T> last = deque.removeLast();

        // if it does not complete, input does not exist
        if (last.completes()) {
            int j = input.length() - 1;
            while (!isUsedForOtherStrings(last = deque.removeLast())) {
                j--;
            }
            String charToUnlink = Character.toString(input.charAt(j));
            synchronized(last.next()) {
                last.next().removeIf(node -> node.string().equals(charToUnlink));
            }
        }
    }

    public Map<String, T> matchByPrefix(String prefix) {
        return prefixMatchUpTo(prefix)
                .map(node -> {
                    Map<String, T> matches = new HashMap<>();
                    findCompleteStringsStartingAt(node, prefix, matches);
                    return matches;
                })
                .orElse(new HashMap<>());
    }

    public List<SearchResult<T>> matchByExactSubstring(String search) {
        return matchBySubstring(search, search.length());
    }
    public List<SearchResult<T>> matchBySubstring(String search, int minLength) {
        if (search.length() == 0 || minLength < 1 || minLength > search.length()) {
            throw new IllegalArgumentException();
        }

        List<SearchResult<T>> matches = new LinkedList<>();
        Set<String> alreadySaved = new HashSet<>();

        findCompleteStringsBySubstring(
                root,
                null,
                search,
                0,
                minLength,
                new StringBuilder(),
                matches,
                alreadySaved);

        return matches
                .stream()
                .sorted(sortByBestMatchFirst)
                .collect(Collectors.toList());
    }


    public Optional<T> getExactly(String string) {
        return prefixMatchUpTo(string)
                .filter(Node::completes)
                .map(Node::value);
    }

    public boolean containsExactly(String string) {
        return getExactly(string).isPresent();
    }

    private Optional<Node<T>> prefixMatchUpTo(String string) {
        Node<T> current = root;

        for (int i = 0; i < string.length(); i++) {
            String currentCharacter = Character.toString(string.charAt(i));

            Optional<Node<T>> nextSubstring;

            synchronized(current.next()) {
                nextSubstring = current.next()
                        .stream()
                        .filter(node -> node.string().equals(currentCharacter))
                        .findAny();
            }

            if (nextSubstring.isEmpty()) {
                return Optional.empty();
            }

            current = nextSubstring.get();
        }
        
        return Optional.of(current);
    }

    public void findCompleteStringsBySubstring(
            Node<T> current,
            Node<T> leftOfFirstMatchingCharacter,
            String search,
            int consecutiveMatches,
            int minLength,
            StringBuilder matchUpToHere,
            Collection<SearchResult<T>> accumulation,
            Set<String> alreadySaved) {

        if (consecutiveMatches == minLength) {
            findCompleteStringsStartingAt(
                    current,
                    leftOfFirstMatchingCharacter,
                    null,
                    search,
                    consecutiveMatches,
                    matchUpToHere,
                    accumulation,
                    alreadySaved);
            return;
        }

        boolean thisNodeMatches = consecutiveMatches > 0;

        Set<Node<T>> nextNodes;

        synchronized(current.next()) {
            nextNodes = Set.copyOf(current.next());
        }

        for (Node<T> nextNode : nextNodes) {
            boolean nextNodeMatches = nextNode.string()
                    .equals(Character.toString(search.charAt(consecutiveMatches)));

            findCompleteStringsBySubstring(
                    nextNode,
                    !thisNodeMatches && nextNodeMatches ? current : leftOfFirstMatchingCharacter,
                    search,
                    nextNodeMatches ? consecutiveMatches + 1 : 0,
                    minLength,
                    new StringBuilder(matchUpToHere).append(nextNode.string()),
                    accumulation,
                    alreadySaved);
        }
    }

    public void findCompleteStringsStartingAt(
            Node<T> current,
            Node<T> leftOfFirstMatchingCharacter,
            Node<T> rightOfLastMatchingCharacter,
            String search,
            int consecutiveMatches,
            StringBuilder matchUpToHere,
            Collection<SearchResult<T>> accumulation,
            Set<String> alreadySaved) {

        if (current.completes()) {
            String matchUpToHereString = matchUpToHere.toString();

            if (!alreadySaved.contains(matchUpToHereString)) {
                boolean matchedWholeWord =
                        (leftOfFirstMatchingCharacter == root || leftOfFirstMatchingCharacter.string().matches(wholeWordSeparator))
                        &&
                        (rightOfLastMatchingCharacter == null || rightOfLastMatchingCharacter.string().matches(wholeWordSeparator));

                SearchResult<T> newSearchResult = new SearchResult<T>(
                        matchUpToHereString,
                        current.value(),
                        consecutiveMatches,
                        matchedWholeWord);

                accumulation.add(newSearchResult);
                alreadySaved.add(matchUpToHereString);
            }
        }

        Set<Node<T>> nextNodes;

        synchronized(current.next()) {
            nextNodes = Set.copyOf(current.next());
        }

        boolean endMatch = null != rightOfLastMatchingCharacter;

        for (Node<T> nextNode : nextNodes) {
            boolean nextNodeMatches = !endMatch
                    && consecutiveMatches < search.length()
                    && nextNode.string().equals(Character.toString(search.charAt(consecutiveMatches)));

            Node<T> nextRightOfLastMatchingCharacter = !endMatch && !nextNodeMatches
                    ? nextNode
                    : rightOfLastMatchingCharacter;

            int newConsecutiveMatches = !endMatch && nextNodeMatches
                    ? consecutiveMatches + 1
                    : consecutiveMatches;

            findCompleteStringsStartingAt(
                    nextNode,
                    leftOfFirstMatchingCharacter,
                    nextRightOfLastMatchingCharacter,
                    search,
                    newConsecutiveMatches,
                    new StringBuilder(matchUpToHere).append(nextNode.string()),
                    accumulation,
                    alreadySaved);
        }
    }

    private void findCompleteStringsStartingAt(Node<T> current, String matchUpToHere, Map<String, T> accumulation) {
        if (current.completes()) {
            accumulation.put(matchUpToHere, current.value());
        }
        for (Node<T> next : current.next()) {
            findCompleteStringsStartingAt(next, matchUpToHere + next.string(), accumulation);
        }
    }

    private boolean isUsedForOtherStrings(Node<T> node) {
        return node == root || node.completes() || node.next().size() > 1;
    }
}
