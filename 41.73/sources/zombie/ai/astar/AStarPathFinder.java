package zombie.ai.astar;


public class AStarPathFinder {

	public static enum PathFindProgress {

		notrunning,
		failed,
		found,
		notyetfound;

		private static AStarPathFinder.PathFindProgress[] $values() {
			return new AStarPathFinder.PathFindProgress[]{notrunning, failed, found, notyetfound};
		}
	}
}
