package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.WorldContext;

import java.util.*;

@Setter
@Getter
public class PathFinder {

    private WorldContext context;
    private int tileSize;

    private static final double WALL_HUG_PENALTY = 10.0;

    public PathFinder(WorldContext context, int tileSize) {
        this.context = context;
        this.tileSize = tileSize;
    }

    public List<Vector3D> findPath(Vector3D start, Vector3D goal) {
        Node startNode = new Node(toGrid(start));
        Node goalNode = new Node(toGrid(goal));

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Double> gScore = new HashMap<>();

        gScore.put(startNode, 0.0);
        startNode.fScore = heuristic(startNode, goalNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(goalNode)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
                // Skip if in closed set or is an actual obstacle
                if (closedSet.contains(neighbor) || isObstacle(neighbor)) {
                    continue;
                }

                double moveCost = 1.0 + getSafetyPenalty(neighbor);

                double tentativeG = gScore.getOrDefault(current, Double.MAX_VALUE) + moveCost;

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    neighbor.fScore = tentativeG + heuristic(neighbor, goalNode);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }


    private double getSafetyPenalty(Node node) {
        int x = (int) node.pos.getX();
        int y = (int) node.pos.getY();

        int[][] offsets = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] offset : offsets) {
            if (context.isObstacle(x + offset[0], y + offset[1])) {
                return WALL_HUG_PENALTY;
            }
        }
        return 0.0;
    }

    private Vector3D toGrid(Vector3D pos) {
        return new Vector3D(
                Math.floor(pos.getX() / tileSize),
                Math.floor(pos.getY() / tileSize)
        );
    }

    private Vector3D toWorld(Vector3D gridPos) {
        return new Vector3D(
                gridPos.getX() * tileSize + tileSize / 2,
                gridPos.getY() * tileSize + tileSize / 2
        );
    }

    private double heuristic(Node a, Node b) {
        return Math.abs(a.pos.getX() - b.pos.getX()) +
                Math.abs(a.pos.getY() - b.pos.getY());
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        Vector3D[] directions = {
                new Vector3D(0, 1), new Vector3D(1, 0),
                new Vector3D(0, -1), new Vector3D(-1, 0)
        };

        for (Vector3D dir : directions) {
            Vector3D newPos = node.pos.add(dir);
            neighbors.add(new Node(newPos));
        }

        return neighbors;
    }

    private boolean isObstacle(Node node) {
        return context.isObstacle((int)node.pos.getX(), (int)node.pos.getY());
    }

    private List<Vector3D> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Vector3D> path = new ArrayList<>();

        while (cameFrom.containsKey(current)) {
            path.add(0, toWorld(current.pos));
            current = cameFrom.get(current);
        }

        return path;
    }

    private static class Node implements Comparable<Node> {
        Vector3D pos;
        double fScore;

        Node(Vector3D pos) {
            this.pos = pos;
            this.fScore = Double.MAX_VALUE;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fScore, other.fScore);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            Node other = (Node) obj;
            return Math.abs(pos.getX() - other.pos.getX()) < 0.01 &&
                    Math.abs(pos.getY() - other.pos.getY()) < 0.01;
        }

        @Override
        public int hashCode() {
            return Objects.hash((int)pos.getX(), (int)pos.getY());
        }
    }
}