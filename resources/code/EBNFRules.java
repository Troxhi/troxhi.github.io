import java.util.Collections;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class EBNFRules implements EBNFString {

	private static final String SIDE_SEPARATOR = "<-";
	
	private static final String LEFT_BRACKET = "<";
	private static final String RIGHT_BRACKET = ">";
	
	private final Map<String, EBNFNode> rules;

	public EBNFRules(Map<String, EBNFNode> rules) {
		this.rules = rules;
	}

	@Override
	public String toEBNFString() {
		// Java Stream API is very useful when working with collections
		return rules.entrySet()
			 .stream()
			 .sorted(Map.Entry.<String, EBNFNode>comparingByKey())
			 .map((Map.Entry<String, EBNFNode> entry) -> toEBNFString(entry.getKey(), entry.getValue()))
			 .collect(Collectors.joining(System.lineSeparator()));
	}
	
	private String toEBNFString(String name, EBNFNode node) {
		StringBuilder builder = new StringBuilder();
		builder.append(LEFT_BRACKET)
			   .append(name)
			   .append(RIGHT_BRACKET)
			   .append(SIDE_SEPARATOR)
			   .append(node.toEBNFString());
		return builder.toString();
	}

	public Set<String> getShortestWords(String name, int limit) {
		EBNFNode node = rules.get(name);
		if (node == null) {
			throw new IllegalEBNFDescriptionException();
		}
		return node.getShortestWords(this, limit);
	}
}

abstract class EBNFNode implements EBNFString {
	
	public Set<String> getShortestWords(EBNFRules rules, int limit){
		// we recommend overriding this method for part b
		
		return null;
	}
}

class Brackets extends EBNFNode {

	private static final String LEFT_BRACKET = "(";
	private static final String RIGHT_BRACKET = ")";
	
	private final EBNFNode child;
	
	public Brackets(EBNFNode child) {
		super();
		this.child = child;
	}

	public Set<String> getShortestWords(EBNFRules rules, int limit){		
		return child.getShortestWords(rules, limit);
	}
	
	@Override
	public String toEBNFString() {		
		return LEFT_BRACKET + child.toEBNFString() + RIGHT_BRACKET;
	}
	
	@Override
	public String toString() {
		return "Brackets(" + child + ")";
	}
}

class Alternative extends EBNFNode {

	private static final String SEPARATOR = "|";
	
	private final EBNFNode first, second;
	
	public Alternative(EBNFNode first, EBNFNode second) {
		super();
		this.first = first;
		this.second = second;
	}

	public Set<String> getShortestWords(EBNFRules rules, int limit) {
		Set<String> firstShortestWords = first.getShortestWords(rules, limit);
		Set<String> secondShortestWords = second.getShortestWords(rules, limit);
		Set<String> result = new HashSet<>();
		result.addAll(firstShortestWords);
		result.addAll(secondShortestWords);
		return result;
	}
	
	@Override
	public String toEBNFString() {		
		return first.toEBNFString() + SEPARATOR + second.toEBNFString();
	}
	
	@Override
	public String toString() {
		return "Alternative(" + first + ", " + second + ")";
	}
}

interface EBNFString {
	String EMPTY_STRING = "";
	
	public String toEBNFString();
}

class Epsilon extends EBNFNode {
	
	public Set<String> getShortestWords(EBNFRules rules, int limit) {	
		String result = toEBNFString();
		if (result.length() > limit) {
			return Collections.emptySet();
		}
		return Set.of(result);
	}
	
	@Override
	public String toEBNFString() {		
		return EMPTY_STRING;
	}
	
	@Override
	public String toString() {
		return "Epsilon()";
	}
}

class IllegalEBNFDescriptionException extends NoSuchElementException {

	private static final long serialVersionUID = 1L;

}

class Literal extends EBNFNode {
	
	private final char literal;
	
	public Literal(char literal) {
		super();
		this.literal = literal;
	}
	
	public Set<String> getShortestWords(EBNFRules rules, int limit) {	
		String result = toEBNFString();
		if (result.length() > limit) {
			return Collections.emptySet();
		}
		return Set.of(result);
	}
	
	@Override
	public String toEBNFString() {		
		return Character.toString(literal);
	}

	@Override
	public String toString() {
		return "Literal(" + literal + ")";
	}
	
}

class Option extends EBNFNode {
	
	private static final String LEFT_BRACKET = "[";
	private static final String RIGHT_BRACKET = "]";
	
	private final EBNFNode child;
	
	public Option(EBNFNode child) {
		super();
		this.child = child;
	}

	public Set<String> getShortestWords(EBNFRules rules, int limit) {
		if (limit < 0) {
			return Collections.emptySet();
		}
		Set<String> shortestWordsOfChild = child.getShortestWords(rules, limit);
		Set<String> result = new HashSet<>();
		result.add(EMPTY_STRING);
		result.addAll(shortestWordsOfChild);
		return result;
	}
	
	@Override
	public String toEBNFString() {	
		return LEFT_BRACKET + child.toEBNFString() + RIGHT_BRACKET; 
	}
	
	@Override
	public String toString() {
		return "Option(" + child + ")";
	}
}

class Repetition extends EBNFNode {
	
	private static final String LEFT_BRACKET = "{";
	private static final String RIGHT_BRACKET = "}";
	
	private final EBNFNode child;
	
	public Repetition(EBNFNode child) {
		super();
		this.child = child;
	}

	public Set<String> getShortestWords(EBNFRules rules, int limit) {	
		Set<String> shortestWordsOfChild = child.getShortestWords(rules, limit);
		Set<String> result = new HashSet<>();
		generateWords(result, shortestWordsOfChild, EMPTY_STRING, limit);
		// Would also be possible. Patrick Bateman would say: Impressive, very nice.
		//generateWords(result, shortestWordsOfChild, limit);
		return result;
	}
	
	// Wait... is this BFS? YES, kind of!
	// Analysis of the algorithm:
	// Assume n = |getShortestWords(rules, limit)| and m = |child.getShortestWords(rules, limit)|
	// Time complexity: O(n*m)
	// Space complexity: O(n)
	// 'Set<String> result' corresponds to visited array in BFS
	// 'Queue<String> q' corresponds to the queue in BFS
	public void generateWords(Set<String> result, Set<String> shortestWordsOfChild, int limit) {
		if (limit < 0) {
			return;
		}
		Queue<String> q = new LinkedList<>();
		q.add(EMPTY_STRING);
		result.add(EMPTY_STRING);
		while (!q.isEmpty()) {
			String currentWord = q.poll(); // O(1)
			for (String word : shortestWordsOfChild) { // edges
				String newWord = currentWord + word;
				if (newWord.length() <= limit && result.add(newWord)) { // O(1) on average
					q.add(newWord); // O(1)
				}
			}
		}
	}
	
	public void generateWords(Set<String> result, Set<String> shortestWordsOfChild, String currentWord, int limit) {
		if (currentWord.length() > limit || result.contains(currentWord)) {
			return;
		}
		result.add(currentWord);
		
		for (String word : shortestWordsOfChild) {
			generateWords(result, shortestWordsOfChild, currentWord + word, limit);
		}
	}
	
	@Override
	public String toEBNFString() {
		return LEFT_BRACKET + child.toEBNFString() + RIGHT_BRACKET;
	}

	@Override
	public String toString() {
		return "Repetition(" + child + ")";
	}
}

class RuleName extends EBNFNode {
	
	private static final String LEFT_BRACKET = "<";
	private static final String RIGHT_BRACKET = ">";
	
	private final String name;
	
	public RuleName(String name) {
		super();
		this.name = name;
	}
	
	public Set<String> getShortestWords(EBNFRules rules, int limit){
		Set<String> result = rules.getShortestWords(name, limit);
		return result;
	}

	@Override
	public String toEBNFString() {
		return LEFT_BRACKET + name + RIGHT_BRACKET;
	}
	
	@Override
	public String toString() {
		return "RuleName(" + name + ")";
	}
}

class Sequence extends EBNFNode {
	
	private static final String LEFT_BRACKET = "(";
	private static final String RIGHT_BRACKET = ")";
	
	private final EBNFNode left, right;
	
	public Sequence(EBNFNode left, EBNFNode right) {
		super();
		this.left = left;
		this.right = right;
	}
	
	@Override
	public Set<String> getShortestWords(EBNFRules rules, int limit) {
		Set<String> leftShortestWords = left.getShortestWords(rules, limit);
		Set<String> rightShortestWords = right.getShortestWords(rules, limit);
		Set<String> result = new HashSet<>();
		for (String leftWord : leftShortestWords) {
			for (String rightWord : rightShortestWords) {
				String resultingWord = leftWord + rightWord;
				if (resultingWord.length() <= limit) {
					result.add(resultingWord);
				}
			}
		}
		return result;
	}

	@Override
	public String toEBNFString() {
		String result = ensurePrecedence(left) + ensurePrecedence(right);
		return result;
	}
	
	private String ensurePrecedence(EBNFNode node) {
		String result = node.toEBNFString();
		if (node instanceof Alternative) {
			result = LEFT_BRACKET + result + RIGHT_BRACKET;
		}
		return result;
	}
 	
	@Override
	public String toString() {
		return "Sequence(" + left + ", " + right + ")";
	}
}

