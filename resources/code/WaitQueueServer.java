import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Idea of this solution:
 * Use doubly linked list (nodes store prev and next field) to store the order of the requests for a file.
 * To store the requests we use an array because we know that the size is fixed. In this case it would be wrong
 * to use an ArrayList because the remove() method in the ArrayList class does more than we want / need.
 * Since we use an array and a linked list we have two different orders. The order of the array is needed because
 * we require every element to have a global index (which is independent of the fileID). Additionally we want to store
 * the order in which the requests were added. We do this by using a linked list for every fileID. This approach gives
 * use a huge performance benefit since we only have to iterate over elements with the same fileID to find the head or tail.
 * 
 * Ideas for alternative solutions:
 * - Use singly linked lists (nodes do not have a prev field) to store the order of the requests for a file.
 * If we choose this approach we have to look for the head and tail in the queue array. Thus we have to
 * iterate over elements that are not relevant for finding the head or tail. 
 * 
 * - Use a TreeMap to map every fileID to a linked list. This approach is probably the most time efficient approach
 * since we need at most O(log(P)) time if P denotes the number of files. However it is hard to argue about performance
 * since we do not have any information on the order of the incoming requests (we do not know in which order add() and 
 * pop() is called).
 * 
 * - Use an ArrayList to store the insertion order.
 * 
 */
public class WaitQueueServer {

	private final QueueEntry[] queue;
	private final int capacity;

	private int currentSize;

	/**
	 * Initialize the queue array with capacity N
	 * We also store the capacity of the queue array in the field capacity
	 * 
	 * @param N is the length of the queue array
	 */
	public WaitQueueServer(int N) {
		queue = new QueueEntry[N];
		capacity = N;
	}

	/**
	 * We first try to find the first element in the queue array that requests access to fileID.
	 * We then use the found element to compute the head and tail. We say k = "maximum number of
	 * elements with fileID". We need at most O(k) time to find the head and tail.
	 * 
	 * @param fileID is an integer to identify the file
	 * @param userID is a char to identify the user
	 * @param readOnly is true if write access is not needed
	 * @return null if the queue array is already full, otherwise we return an instance of the reference
	 * type Response which contains the index of the head and the index of the tail. 
	 */
	public Response add(int fileID, char userID, boolean readOnly) { 
		// currentSize stores the current number of elements in the array queue
		if (currentSize == capacity) { // if currentSize == capacity, then queue is full
			return null;
		}
		// Precondition: currentSize != capacity 
		// Based on the precondition we know that there will be a free spot in the queue array, hence
		// we already increase the size of the queue array.
		currentSize++;
		QueueEntry firstOccurrence = findFirstOccurringEntry(fileID);
		QueueEntry head = findHead(firstOccurrence);
		QueueEntry tail = findTail(firstOccurrence);

		// Find index / position in the queue array that is not used
		int i = 0;
		while (queue[i] != null) {
			i++;
		}

		// Create new entry
		QueueEntry newEntry = new QueueEntry(i, fileID, userID, readOnly, null, tail);
		queue[i] = newEntry;
		// If firstOccurrence == null, then we know that this entry will be the first entry with this fileID
		if (firstOccurrence == null) {
			return new Response(i, i);
		}

		// Update tail
		if (tail != null) {
			tail.next = newEntry;
		}
		tail = newEntry;
		return new Response(head.index, tail.index);
	}

	/**
	 * Find first occurring entry in queue with fileID
	 * @param fileID that we use to find the relevant element
	 * @return first occurrence or null if no entry with fileID exists
	 */
	private QueueEntry findFirstOccurringEntry(int fileID) {
		for (int i = 0; i < capacity; i++) {
			QueueEntry entry = queue[i];
			if (entry != null && entry.fileID == fileID) {
				return entry;
			}
		}
		return null;
	}

	private QueueEntry findHead(QueueEntry entry) {
		if (entry == null) {
			return null;
		}
		if (entry.isHead()) {
			return entry;
		}
		QueueEntry next = entry.prev;
		// Precondition: entry.prev != null because entry.isHead() is false
		while (next.prev != null) {
			next = next.prev;
		}
		return next;
	}

	private QueueEntry findTail(QueueEntry entry) {
		if (entry == null) {
			return null;
		}
		if (entry.isTail()) {
			return entry;
		}
		QueueEntry next = entry.next;
		// entry.next != null because entry.isTail() is false
		while (next.next != null) {
			next = next.next;
		}
		return next;
	}

	// Pop queued request(s) on this fileID
	// Return format: {userID, ...}
	public char[] pop(int fileID) {
		QueueEntry head = findHead(findFirstOccurringEntry(fileID));
		if (head == null) {
			return null;
		}
		if (head.readOnly) {
			LinkedList<Character> interResult = new LinkedList<>();
			QueueEntry next = head;
			while (next != null) {
				if (!next.readOnly) {
					next = next.next;
					continue;
				}
				interResult.add(next.userID);
				remove(next);
				next = next.next;
			}
			return linkedListToCharArray(interResult);
		} else {
			remove(head);
			return new char[] {head.userID};
		}
	}

	public char[] linkedListToCharArray(LinkedList<Character> list) {
		char[] charArray = new char[list.size()];
		for (int i = 0; i < list.size(); i++) {
			charArray[i] = list.get(i);
		}
		return charArray;
	}

	public void remove(QueueEntry entry) {
		// If prev element exists
		if (entry.prev != null) {
			entry.prev.next = entry.next;
		}
		// If next element exists
		if (entry.next != null) {
			entry.next.prev = entry.prev;
		}
		currentSize--;
		queue[entry.index] = null;
	}

	// Get a quick reference list on all queued files, and the list is sorted by fileID
	// Return format: {{fileID, head, tail}, {...}, ...}
	public int[][] getQuickList() {
		if (currentSize == 0) {
			return null;
		}

		// find and store distinct fileIDs. ∀u,v ∈ fileIDs: u ≠ v
		// Would be more efficient with TreeSet, because ArrayList.contains() needs O(n) time where n is the size of the ArrayList
		ArrayList<Integer> fileIDs = new ArrayList<>();
		for (int i = 0; i < capacity; i++) {
			QueueEntry entry = queue[i];
			if (entry != null && !fileIDs.contains(entry.fileID)) {
				fileIDs.add(entry.fileID);
			}
		}
		Collections.sort(fileIDs);

		int size = fileIDs.size();
		int[][] result = new int[size][3];
		for (int i = 0; i < size; i++) {
			int fileID = fileIDs.get(i);
			QueueEntry firstOccurrence = findFirstOccurringEntry(fileID);
			QueueEntry head = findHead(firstOccurrence);
			QueueEntry tail = findTail(firstOccurrence);
			result[i][0] = fileID;
			result[i][1] = head.index;
			result[i][2] = tail.index;
		}

		return result;
	}
}

class Response {
	private int head;
	private int tail;

	public Response(int head, int tail) {
		this.head = head;
		this.tail = tail;
	}

	@Override
		public String toString() {
			return "Response(" + head + ", " + tail+ ")";
		}

	public int getHead() {
		return head;
	}

	public int getTail() {
		return tail;
	}	
}

class QueueEntry {

	int index;

	int fileID;
	char userID;
	boolean readOnly;

	QueueEntry next;
	QueueEntry prev;

	public QueueEntry(int index, int fileID, char userID, boolean readOnly, QueueEntry next, QueueEntry prev) {
		this.index = index;
		this.fileID = fileID;
		this.userID = userID;
		this.readOnly = readOnly;
		this.next = next;
		this.prev = prev;
	}

	public boolean isHead() {
		return this.prev == null;
	}

	public boolean isTail() {
		return this.next == null;
	}
}

