import java.util.ArrayList;
import java.util.TreeMap;

public class TransportSystem {
	private ArrayList<Station> stations = new ArrayList<Station>();
	private TreeMap<Character, Station> stationMapping = new TreeMap<Character, Station>();

	public TransportSystem(ArrayList<Station> stations) {
		this.stations = stations;
	}

	public Route findFastestRoute() {
		for (int i = 0; i < stations.size(); i++) {
			Station station = stations.get(i);
			station.setId(i);
			stationMapping.put(station.getName(), station);
		}
	    return shortestPath(stations.getFirst(), stations.getLast(), null, 0);		
	}
	
	public Route shortestPath(Station next, Station end, Connection lastConnection, int time) {
		if (next == end) {
			Route route = new Route(time, new ArrayList<>());
			return route;
		}
		
		Route minRoute = null;
		for (Connection connection : next.getConnections()) {
			Station station = stationMapping.get(connection.getNextStation());
			int weight = connection.getTime();
			if (lastConnection != null && lastConnection.getType() == connection.getType()) {
				weight -= lastConnection.getDeduction();
			}

			Route result = shortestPath(station, end, connection, time + weight);
			if (minRoute == null || (result != null && result.time < minRoute.time)) {
				if (result != null) {
					result.getConnections().addFirst(connection);
				}
				minRoute = result;
			}
		}
		return minRoute;
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

