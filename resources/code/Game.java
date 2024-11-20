import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Game {
	
	public final List<Human> humans;
	public final List<ActionInstance> actionInstances;
	
	public Game() {
		this.humans = new ArrayList<>(100);
		this.actionInstances = new ArrayList<>(100);
	}
	
	void advanceTurn() {
		ListIterator<ActionInstance> it = actionInstances.listIterator();
		while (it.hasNext()) {
			ActionInstance actionInstance = it.next();
			Human human = actionInstance.source;
			
			if (!human.isAlive()) {
				human.actionInstance = null;
				it.remove();
				continue;
			}
			if (actionInstance.delay != 0) {
				actionInstance.delay--;
				continue;
			}
			// execute action
			human.actionInstance = null;
			it.remove();
			if (human.type == Type.WARRIOR) {
				if (actionInstance.action == Action.SUMMON) {
					human.health -= 5;
				} else if (actionInstance.action == Action.ATTACK) {
					for (Human neighbour : humans) {
						int diff = Math.abs(human.position - neighbour.position);
						if (diff == 1) {
							neighbour.health -= 10;
						}
					}
				}
			} else if (human.type == Type.CLERIC) {
				if (actionInstance.action == Action.SUMMON) {
					int i = human.position;
					for (Human neighbour : humans) {
						if (!neighbour.isAlive()) {
							continue;
						}
						int diff = Math.abs(i - neighbour.position);
						if (diff >= 3 && diff <= 5) {
							neighbour.position = i;
						}
					}
				} else if (actionInstance.action == Action.ATTACK) {
					for (Human neighbour : humans) {
						int diff = Math.abs(human.position - neighbour.position);
						if (diff == 1) {
							neighbour.health -= 3;
						}
					}
				}
			}
		}
	}

	Human createJester(int health, int position) {
		Human human = new Human(health, position, Type.JESTER, this);
		humans.add(human);
		return human;
	}
	
	Human createWarrior(int health, int position) {
		Human human = new Human(health, position, Type.WARRIOR, this);
		humans.add(human);
		return human;
	}
	
	Human createCleric(int health, int position) {
		Human human = new Human(health, position, Type.CLERIC, this);
		humans.add(human);
		return human;
	}
}

class Human {
	
	public final Game gameInstance;
	public ActionInstance actionInstance;
	
	public final Type type;
	
	public int health, position;
	
	public Human(int health, int position, Type type, Game gameInstance) {
		this.health = health;
		this.position = position;
		this.type = type;
		this.gameInstance = gameInstance;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getPosition() {
		return position;
	}
	
	public boolean isAlive() {
		return health > 0;
	}
	
	public boolean scheduleAction(Action action) {
		if (!isAlive()) {
			return false;
		}
		if (actionInstance != null) {
			return false;
		}
		int delay = type.getDelay(action);
		ActionInstance instance = new ActionInstance(action, this, delay);
		actionInstance = instance;
		gameInstance.actionInstances.add(instance);
		return true;
	}
}

enum Type {

	JESTER(0, 0),
	WARRIOR(0, 1),
	CLERIC(0, 2);
	
	private int attackDelay;
	private int summonDelay;
	
	private Type(int attackDelay, int summonDelay) {
		this.attackDelay = attackDelay;
		this.summonDelay = summonDelay;
	}
	
	public int getDelay(Action action) {
		if (action == Action.ATTACK) {
			return attackDelay;
		} else {
			return summonDelay;
		}
	}
}

class ActionInstance {
	
	public final Action action;
	public final Human source;
	public int delay;
	
	public ActionInstance(Action action, Human source, int delay) {
		this.action = action;
		this.source = source;
		this.delay = delay;
	}
}

enum Action {
	ATTACK,
	SUMMON
}
