import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class FastTransportSystem {
	private ArrayList<Station> stations = new ArrayList<Station>();
	private TreeMap<Character, Integer> stationNodeMapping = new TreeMap<Character, Integer>();

	public FastTransportSystem(ArrayList<Station> stations) {
		this.stations = stations;
	}

	public Route findFastestRoute() {
		for (int i = 0; i < stations.size(); i++) {
			Station station = stations.get(i);
			stationNodeMapping.put(station.getName(), i);
		}
		int n = stations.size();
		return dijkstra(0, n-1, n);
	}
	
	public Route dijkstra(int start, int end, int n) {
		// use combination of node and connection
		// CAUTION: Do not use a combination of node and a list of previous connections. If you do this you no longer
		// have the time complexity guarantee that is given by Dijkstra's algorithm because |V'| ∉ O(|V|)
		PriorityQueue<Node> q = new PriorityQueue<Node>();
		int[][] distance = new int[n][3];
		Node[][] prev = new Node[n][3];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 3; j++) {
				distance[i][j] = Integer.MAX_VALUE;
 			}
		}
		
		distance[start][0] = 0;
		q.add(new Node(start, 0, null));
		while (!q.isEmpty()) {
			Node element = q.poll(); // takes O(log(n))
			int node = element.node;
			if (node == end) {
				Route route = createRoute(distance, prev, element);
				return route;
			}
			
			Station station = stations.get(node);
			Connection lastConnection = element.prevConnection;
			for (Connection connection : station.getConnections()) {
				int connectionType = connection.getType();
				int neighbour = stationNodeMapping.get(connection.getNextStation()); // takes O(log(n))
				int weight = connection.getTime();
				if (lastConnection != null && lastConnection.getType() == connectionType) {
					weight -= lastConnection.getDeduction();
				}
				// weight is never negative => weight >= 0
				int nodeType = neighbour == end ? 0 : connectionType;
				int currentNodeType = node == start ? 0 : lastConnection.getType();
				int newDistance = distance[node][currentNodeType] + weight;
				
				if (newDistance < distance[neighbour][nodeType]) {
					distance[neighbour][nodeType] = newDistance;
					prev[neighbour][nodeType] = element;
					q.add(new Node(neighbour, newDistance, connection)); // takes O(log(n))
				}
			}
		}
		return null;
	}
	
	private Route createRoute(int[][] distance, Node[][] prev, Node endNode) {
		if (endNode == null) {
			return null;
		}
		int time = distance[endNode.node][0];
		if (time == 0) {
			return new Route(time, new ArrayList<Connection>());
		}
		ArrayList<Connection> connections = new ArrayList<>();
		connections.add(endNode.prevConnection);
		for (Node current = prev[endNode.node][0]; current.prevConnection != null; current = prev[current.node][current.prevConnection.getType()]) {
			connections.add(current.prevConnection);
		}
		Collections.reverse(connections);
		return new Route(time, connections);
	}

}

class Node implements Comparable<Node> {

	public int node;
	public int distance;
	public Connection prevConnection;
	
	public Node(int node, int distance, Connection prevConnection) {
		this.node = node;
		this.distance = distance;
		this.prevConnection = prevConnection;
	}
	
	@Override
	public int compareTo(Node o) {
		return distance - o.distance;
	}
}

abstract class Connection {
	final char name;
	final int distance;
	
	public Connection(char name, int distance) {
		this.name = name;
		this.distance = distance;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public char getNextStation() {
		return name;
	}
	
	public int getTime() {
		return 0;
	}
	
	public abstract int getType();
	
	public abstract int getDeduction();

	@Override
	public String toString() {
		return this.getClass().getSimpleName()+"(" + name + ", " + distance + ")";
	}
}

class Bus extends Connection {

	public Bus(char name, int distance) {
		super(name, distance);
	}
	
	@Override
	public int getTime() {
		return 10 + Math.max(0, (distance-2)*7);
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public int getDeduction() {
		return 2;
	}
}

class Route {
	int time;
	ArrayList<Connection> connections;
	
	public Route(int time, ArrayList<Connection> connections) {
		this.time = time;
		this.connections = connections;
	}
	
	public String toString() {
		return "Route(time=" + time + ", connections=" +connections+ ")";
	}
	
	public int getTime() {
		return time;
	}
	public ArrayList<Connection> getConnections() {
		return connections;
	}
}

class Station {
	// add new id
	private int id;
	private char name;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	
	public Station(char name, ArrayList<Connection> connections) {
		this.name = name;
		this.connections = connections;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public char getName() {
		return name;
	}
	
	public void setName(char name) {
		this.name = name;
	}
	
	public ArrayList<Connection> getConnections() {
		return connections;
	}

	@Override
	public String toString() {
		return "Station [name=" + name + ", connections=" + connections  + "]";
	}
	
}

class Train extends Connection {

	public Train(char name, int distance) {
		super(name, distance);
	}
	
	@Override
	public int getTime() {
		return 32 + Math.max(0, (distance - 8)*2);
	}

	@Override
	public int getType() {
		return 2;
	}

	@Override
	public int getDeduction() {
		return 10;
	}
}

class Tram extends Connection {

	public Tram(char name, int distance) {
		super(name, distance);
	}
	
	@Override
	public int getTime() {
		return 20 + Math.max(0, (distance - 5)*5);
	}

	@Override
	public int getType() {
		return 1;
	}

	@Override
	public int getDeduction() {
		return 5;
	}
}
