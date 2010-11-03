package sc2;

import sc2.action.SC2Action;
import sc2.action.SC2Morph;
import sc2.action.SC2ActionException;

import java.util.LinkedList;

/**
** Represents a completed asset. (Not researches.)
*/
public class SC2Asset {

	/** max queue size. TODO for BO-optimisation we should probably set this to 1 */
	final public static int qcap = 5;

	final public SC2AssetType type;

	final protected LinkedList<SC2Action> queue = new LinkedList<SC2Action>();

	protected double energy;

	public SC2Asset(SC2AssetType type, int energy) {
		if (type == null) { throw new NullPointerException(); }
		this.type = type;
		this.energy = energy;
	}

	public SC2Asset(SC2AssetType type) {
		this(type, type.stat_ep.init);
	}

	protected void advance(double dec) {
		// advance queue
		SC2Action current = peekQueue();
		if (current == null) {
			return;
		} else if (current.advance(dec)) {
			queue.removeFirst();
		}
		// regen energy
		energy = type.stat_ep.regen(energy);
	}

	/**
	** Advance by 1 game second.
	*/
	public void advance() {
		advance(1.0);
	}

	public boolean canQueue(SC2Action action) {
		if (queue.size() >= qcap) { return false; }
		SC2Action current = peekQueue();

		// can't queue on a morphing asset
		if (current instanceof SC2Morph) { return false; }
		// can't morph when items on queue
		if (action instanceof SC2Morph && current != null) { return false; }

		return true;
	}

	/**
	** @return The item at the head of the queue, or {@code null}.
	*/
	public SC2Action peekQueue() {
		return queue.isEmpty()? null: queue.getFirst();
	}

	/**
	** Whether the asset is currently doing something.
	*/
	public boolean isActive() {
		return peekQueue() != null;
	}

	/**
	** @return The action that was pushed or {@code null} if unchanged
	** @throws NullPointerException if the action was {@code null}
	*/
	public SC2Action pushQueue(SC2Action action) throws SC2ActionException {
		if (action == null) { throw new NullPointerException(); }
		if (!canQueue(action)) { return null; }
		queue.addLast(action);
		action.init();
		return action;
	}

	/**
	** @return The action that was popped or {@code null} if unchanged
	*/
	public SC2Action popQueue(int i) {
		SC2Action action = queue.remove(i);
		if (action != null) { action.cancel(); }
		return action;
	}

	/**
	** @return The action that was popped or {@code null} if unchanged
	** @throws NullPointerException if the action was {@code null}
	*/
	public SC2Action popQueue(SC2Action action) {
		if (action == null) { throw new NullPointerException(); }
		int i = queue.indexOf(action);
		return (i < 0)? null: popQueue(i);
	}

}