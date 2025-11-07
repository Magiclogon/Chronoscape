package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldContext;

import java.util.*;

@Setter
@Getter
public class PathFinder {
    private WorldContext context;
    private int tileSize;

    public PathFinder(WorldContext context, int tileSize) {
        this.context = context;
        this.tileSize = tileSize;
    }

    // Simple A* pathfinding
    public List<Vector2D> findPath(Vector2D start, Vector2D goal) {
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
                if (closedSet.contains(neighbor) || isObstacle(neighbor)) {
                    continue;
                }

                double tentativeG = gScore.getOrDefault(current, Double.MAX_VALUE) + 1.0;

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

        return new ArrayList<>(); // No path found
    }

    private Vector2D toGrid(Vector2D pos) {
        return new Vector2D(
                Math.floor(pos.getX() / tileSize),
                Math.floor(pos.getY() / tileSize)
        );
    }

    private Vector2D toWorld(Vector2D gridPos) {
        return new Vector2D(
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
        Vector2D[] directions = {
                new Vector2D(0, 1), new Vector2D(1, 0),
                new Vector2D(0, -1), new Vector2D(-1, 0)
        };

        for (Vector2D dir : directions) {
            Vector2D newPos = node.pos.add(dir);
            neighbors.add(new Node(newPos));
        }

        return neighbors;
    }

    private boolean isObstacle(Node node) {
        // Check if position is obstacle in world
        return context.isObstacle((int)node.pos.getX(), (int)node.pos.getY());
    }

    private List<Vector2D> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Vector2D> path = new ArrayList<>();

        while (cameFrom.containsKey(current)) {
            path.add(0, toWorld(current.pos));
            current = cameFrom.get(current);
        }

        return path;
    }

    private static class Node implements Comparable<Node> {
        Vector2D pos;
        double fScore;

        Node(Vector2D pos) {
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
