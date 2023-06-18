import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Graph {
    private final HashMap<Integer, HashMap<Integer, Integer>> matrix = new HashMap<>();
    private final HashMap<Integer, Integer> prev = new HashMap<>();
    private final HashMap<Integer, Integer> minWeight = new HashMap<>();
    private final HashMap<Integer, Boolean> findSymbol = new HashMap<>();
    private boolean updateSymbol = false;

    public Graph() {
    }

    public void addPoint(Person person) {
        MyPerson p = (MyPerson) person;
        this.matrix.put(p.getId(), p.getValue());
        this.minWeight.put(p.getId(), Integer.MAX_VALUE);
        this.findSymbol.put(p.getId(), false);
    }

    public void addEdge(int u, int v, int w) {
        this.matrix.get(u).put(v, w);
        this.matrix.get(v).put(u, w);
    }

    public void removeEdge(int u, int v) {
        this.matrix.get(u).remove(v);
        this.matrix.get(v).remove(u);
    }

    public int shortestPath(int u, int v) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        HashMap<Integer, Integer> dist = new HashMap<>();
        HashSet<Integer> visited = new HashSet<>();
        prev.clear();
        pq.offer(new int[]{u, 0});
        dist.put(u, 0);
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int node = cur[0];
            int distance = cur[1];
            if (visited.contains(node)) {
                continue;
            }
            if (node == v) {
                return distance;
            }
            visited.add(node);
            for (int nei : this.matrix.get(node).keySet()) {
                if (!visited.contains(nei) && (!dist.containsKey(nei)
                        || dist.get(nei) > distance + matrix.get(node).get(nei))) {
                    dist.put(nei, distance + matrix.get(node).get(nei));
                    prev.put(nei, node);
                    pq.offer(new int[]{nei, dist.get(nei)});
                }
            }
        }
        return -1;
    }

    public int findMinCycle(int u) {
        int minCycle = Integer.MAX_VALUE;
        // 定义一个列表，存储要删除的边
        List<int[]> edgesToRemove = new ArrayList<>();
        for (int v : matrix.get(u).keySet()) {
            int w = matrix.get(u).get(v);
            edgesToRemove.add(new int[]{u, v, w});
        }
        for (int[] edge : edgesToRemove) {
            int v = edge[1];
            int w = edge[2];
            removeEdge(u, v);
            int result = shortestPath(v, u);
            if (result != -1) {
                int len = 0;
                int cur = u;
                while (cur != v) {
                    len++;
                    cur = prev.get(cur);
                }
                if (len >= 2) {
                    minCycle = Math.min(minCycle, result + w);
                }
            }
            addEdge(u, v, w);
        }
        return minCycle == Integer.MAX_VALUE ? -1 : minCycle;
    }

    public void findMinLoop(int id) {
        if (!this.findSymbol.containsKey(id)) {
            return;
        }
        if (!this.findSymbol.get(id) || updateSymbol) {
            if (updateSymbol) {
                this.findSymbol.replaceAll((k, v) -> false);
                this.minWeight.replaceAll((k, v) -> Integer.MAX_VALUE);
                updateSymbol = false;
            }
            int result = findMinCycle(id);
            if (result != -1) {
                this.findSymbol.put(id, true);
                this.minWeight.put(id, result);
            }
        }
    }

    public int getWeight(int id) {
        return this.minWeight.get(id);
    }

    public boolean getFindSymbol(int id) {
        return this.findSymbol.get(id);
    }

    public void update() {
        this.updateSymbol = true;
    }
}
