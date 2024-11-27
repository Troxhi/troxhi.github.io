import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class WaitQueueServerAlternative {

	private final ArrayList<QueueEntryAlternative> order;
	private final QueueEntryAlternative[] queue;
	private final int capacity;

	private int currentSize;

	public WaitQueueServerAlternative(int N) {
		queue = new QueueEntryAlternative[N];
		order = new ArrayList<>(N);
		capacity = N;
	}

	public Response add(int fileID, char userID, boolean readOnly) { 
		if (currentSize == capacity) {
			return null;
		}
		currentSize++;
		QueueEntryAlternative head = findHead(fileID);
		int i = 0;
		while (queue[i] != null) {
			i++;
		}
		QueueEntryAlternative newEntry = new QueueEntryAlternative(i, fileID, userID, readOnly);
		queue[i] = newEntry;
		order.add(newEntry);
		if (head == null) {
			return new Response(i, i);
		}
		return new Response(head.index, i);
	}

	private QueueEntryAlternative findHead(int fileID) {
		for (QueueEntryAlternative entry : order) {
			if (entry != null && entry.fileID == fileID) {
				return entry;
			}
		}
		return null;
	}

	private QueueEntryAlternative findTail(int fileID) {
		QueueEntryAlternative result = null;
		for (QueueEntryAlternative entry : order) {
			if (entry != null && entry.fileID == fileID) {
				result = entry;
			}
		}
		return result;
	}

	// Pop queued request(s) on this fileID
	// Return format: {userID, ...}
	public char[] pop(int fileID) {
		QueueEntryAlternative head = findHead(fileID);
		if (head == null) {
			return null;
		}
		if (head.readOnly) {
			LinkedList<Character> interResult = new LinkedList<>();
			Iterator<QueueEntryAlternative> it = order.iterator();
			while (it.hasNext()) {
				QueueEntryAlternative entry = it.next();
				if (!entry.readOnly || entry.fileID != fileID) {
					continue;
				}
				interResult.add(entry.userID);
				it.remove();
				removeFromQueue(entry);
			}
			return linkedListToCharArray(interResult);
		} else {
			order.remove(head);
			removeFromQueue(head);
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

	public void removeFromQueue(QueueEntryAlternative entry) {
		currentSize--;
		queue[entry.index] = null;
	}

	// Get a quick reference list on all queued files, and the list is sorted by fileID
	// Return format: {{fileID, head, tail}, {...}, ...}
	public int[][] getQuickList() {
		if (currentSize == 0) {
			return null;
		}

		// more efficient with TreeSet
		ArrayList<Integer> fileIDs = new ArrayList<>();
		for (int i = 0; i < capacity; i++) {
			QueueEntryAlternative entry = queue[i];
			if (entry != null && !fileIDs.contains(entry.fileID)) {
				fileIDs.add(entry.fileID);
			}
		}
		Collections.sort(fileIDs);

		int size = fileIDs.size();
		int[][] result = new int[size][];
		for (int i = 0; i < size; i++) {
			int fileID = fileIDs.get(i);
			QueueEntryAlternative head = findHead(fileID);
			QueueEntryAlternative tail = findTail(fileID);
			result[i] = new int[] { 
				fileID, 
					head.index, 
					tail.index 
			};
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

class QueueEntryAlternative {

	int index;

	int fileID;
	char userID;
	boolean readOnly;

	public QueueEntryAlternative(int index, int fileID, char userID, boolean readOnly) {
		this.index = index;
		this.fileID = fileID;
		this.userID = userID;
		this.readOnly = readOnly;
	}
}
