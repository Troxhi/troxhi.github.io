import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TransportSystem {
	private List<Station> stations;

	private Map<Character, Station> stationMapping;

	public TransportSystem(List<Station> stations) {
		this.stations = stations;
		this.stationMapping = new TreeMap<>();
		for (Station station : stations) {
			stationMapping.put(station.getName(), station);
		}
	}

	public Route findFastestRoute() {
		return shortestPath(stations.getFirst(), stations.getLast(), null, 0);
	}

	public Route shortestPath(Station next, Station end, Connection lastConnection, int time) {
		if (next == end) {
            return new Route(time, new ArrayList<>());
		}

		Route minRoute = null;
		for (Connection connection : next.getConnections()) {
			Station station = stationMapping.get(connection.getNextStation());
			int weight = connection.getTime();
			if (lastConnection != null && lastConnection.getType() == connection.getType()) {
				weight -= lastConnection.getDeduction();
			}

			Route result = shortestPath(station, end, connection, time + weight);
			boolean betterRoute = minRoute == null || (result != null && result.time < minRoute.time);
			if (betterRoute) {
				if (result != null) {
					result.getConnections().addFirst(connection);
				}
				minRoute = result;
			}
		}
		return minRoute;
	}
}

public class Tram extends Connection {

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

public class Train extends Connection {

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

import java.util.List;

public class Station {
	private char name;
	private List<Connection> connections;

	public Station(char name, List<Connection> connections) {
		this.name = name;
		this.connections = connections;
	}

	public char getName() {
		return name;
	}

	public void setName(char name) {
		this.name = name;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	@Override
	public String toString() {
		return "Station [name=" + name + ", connections=" + connections + "]";
	}
}

import java.util.List;

public class Route {
	int time;
	List<Connection> connections;

	public Route(int time, List<Connection> connections) {
		this.time = time;
		this.connections = connections;
	}

	public String toString() {
		return "Route(time=" + time + ", connections=" + connections + ")";
	}

	public int getTime() {
		return time;
	}

	public List<Connection> getConnections() {
		return connections;
	}
}

public abstract class Connection {
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
		return this.getClass().getSimpleName() + "(" + name + ", " + distance + ")";
	}
}

public class Bus extends Connection {

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