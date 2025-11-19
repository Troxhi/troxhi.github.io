public class Game {

	private static final int MAX_HUMANS = 100;

	public StaticHumanArrayList activeHumans;
	private final StaticHumanArrayList allHumans;

	public Game() {
		activeHumans = new StaticHumanArrayList(MAX_HUMANS);
		allHumans = new StaticHumanArrayList(MAX_HUMANS);
	}

	void advanceTurn() {
		StaticHumanArrayList newActiveHumans = new StaticHumanArrayList(MAX_HUMANS);
		for (int i = 0; i < activeHumans.size(); i++) {
			Human source = activeHumans.get(i);
			ActionInstance actionInstance = source.getActionInstance();
			if (actionInstance.delay > 0) {
				actionInstance.delay--;
				newActiveHumans.add(source);
				continue;
			}
			source.actionInstance = null;

			if (!source.isAlive()) {
				continue;
			}
			if (source.type == Type.WARRIOR) {
				if (actionInstance.action == Action.ATTACK) {
					for (int j = 0; j < allHumans.size(); j++) {
						Human human = allHumans.get(j);
						int distance = Math.abs(human.position - source.position);
						if (distance == 1) {
							human.health -= 10;
						}
					}
				} else if (actionInstance.action == Action.SUMMON) {
					source.health -= 5;
				}
			} else if (source.type == Type.CLERIC) {
				if (actionInstance.action == Action.ATTACK) {
					for (int j = 0; j < allHumans.size(); j++) {
						Human human = allHumans.get(j);
						int distance = Math.abs(human.position - source.position);
						if (distance == 1) {
							human.health -= 3;
						}
					}
				} else if (actionInstance.action == Action.SUMMON) {
					for (int j = 0; j < allHumans.size(); j++) {
						Human human = allHumans.get(j);
						if (!human.isAlive()) {
							continue;
						}
						int distance = Math.abs(human.position - source.position);
						if (distance >= 3 && distance <= 5) {
							human.position = source.position;
						}
					}
				}
			}
		}
		activeHumans = newActiveHumans;
	}

	Human createJester(int health, int position) {
		return createHuman(health, position, Type.JESTER);
	}
	
	Human createWarrior(int health, int position) {
		return createHuman(health, position, Type.WARRIOR);
	}
	
	Human createCleric(int health, int position) {
		return createHuman(health, position, Type.CLERIC);
	}

	private Human createHuman(int health, int position, Type type) {
		Human human = new Human(health, position, type, this);
		allHumans.add(human);
		return human;
	}
}

public class Human {
	
	public int health;
	public int position;

	public final Type type;

	public ActionInstance actionInstance;

	private final Game game;

	public Human(int health, int position, Type type, Game game) {
		this.health = health;
		this.position = position;
		this.type = type;
		this.game = game;
	}

	public int getHealth() {
		return health;
	}
	
	public int getPosition() {
		return position;
	}

	public ActionInstance getActionInstance() {
		return actionInstance;
	}

	public boolean isAlive() {
		return health > 0;
	}
	
	public boolean scheduleAction(Action action) {
		if (!isAlive() || actionInstance != null) {
			return false;
		}
		actionInstance = new ActionInstance(action, type.getInitialActionDelay(action));
		game.activeHumans.add(this);
		return true;
	}
}

public class ActionInstance {

    public final Action action;
    public int delay;

    public ActionInstance(Action action, int delay) {
        this.action = action;
        this.delay = delay;
    }
}

public enum Action {
	ATTACK,
	SUMMON
}

public class StaticHumanArrayList {

    private final Human[] humans;
    private int size = 0;

    public StaticHumanArrayList(int staticSize) {
        humans = new Human[staticSize];
    }

    public void add(Human human) {
        humans[size++] = human;
    }

    public Human get(int index) {
        return humans[index];
    }

    public int size() {
        return size;
    }
}

public enum Type {

    JESTER(0, 0),
    WARRIOR(0, 1),
    CLERIC(0, 2);

    private final int attackDelay;
    private final int summonDelay;

    Type(int attackDelay, int summonDelay) {
        this.attackDelay = attackDelay;
        this.summonDelay = summonDelay;
    }

    public int getInitialActionDelay(Action action) {
        if (action == Action.ATTACK) {
            return attackDelay;
        } else if (action == Action.SUMMON) {
            return summonDelay;
        }
        // or simply return -1
        throw new IllegalArgumentException("Unexpected action");
    }
}