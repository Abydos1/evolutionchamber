package sc2;

import java.util.HashMap;
import static sc2.SC2AssetType.Group;

/**
** Holds data for in-game asset types.
*/
public class SC2StatDB extends HashMap<String, SC2AssetType> {

	public SC2StatDB() { }

	public Group getType(String key) {
		return get(key).type();
	}

	public static SC2StatDB getDefault() {
		// TODO
		return null;
	}

}
