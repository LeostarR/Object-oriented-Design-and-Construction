import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class StateMap {
    private final HashMap<Integer, Integer> serviceMap = new HashMap<>();
    private final HashMap<Integer, Integer> onlyMap = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> accessMap = new HashMap<>();//电梯-->该电梯可达楼层
    private final HashMap<Integer, ArrayList<Integer>> pathMap = new HashMap<>();
    //楼层-->该楼层可直达的楼层(包含自己)
    //仅在新增电梯和维护电梯时改变

    public StateMap() {
        for (int i = 1;i <= 11;i++) {
            this.serviceMap.put(i, 0);
            this.onlyMap.put(i, 0);
        }
        for (int i = 1;i <= 6;i++) {
            ArrayList<Integer> accessList = new ArrayList<>();
            for (int j = 1; j <= 11; j++) {
                if ((2047 & (1 << (j - 1))) != 0) {
                    accessList.add(j);
                }
            }
            accessMap.put(i, accessList);
        }
        this.createMap();
    }

    public synchronized void addService(int floor) {
        while (this.serviceMap.get(floor) >= 4) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        serviceMap.put(floor, serviceMap.get(floor) + 1);
        notifyAll();
    }

    public synchronized void removeService(int floor) {
        serviceMap.put(floor, serviceMap.get(floor) - 1);
        notifyAll();
    }

    public synchronized void addOnly(int floor) {
        while (this.onlyMap.get(floor) >= 2) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        onlyMap.put(floor, onlyMap.get(floor) + 1);
        notifyAll();
    }

    public synchronized void removeOnly(int floor) {
        onlyMap.put(floor, onlyMap.get(floor) - 1);
        notifyAll();
    }

    public synchronized void addAccess(int access, int id) {
        ArrayList<Integer> accessList = new ArrayList<>();
        for (int i = 1;i <= 11;i++) {
            if ((access & (1 << (i - 1))) != 0) {
                accessList.add(i);
            }
        }
        this.accessMap.put(id, accessList);
        this.createMap();
        notifyAll();
    }

    public synchronized void removeAccess(int id) {
        this.accessMap.remove(id);
        this.createMap();
        notifyAll();
    }

    public ArrayList<Integer> getAccessList(int id) {
        return this.accessMap.get(id);
    }

    public boolean canAccess(int id, int floor) {
        if (this.accessMap.containsKey(id)) {
            return this.accessMap.get(id).contains(floor);
        } else {
            return false;
        }
    }

    public synchronized LinkedList<Integer> findPath(Passenger p) {
        LinkedList<Integer> path = new LinkedList<>();
        HashMap<Integer, ArrayList<Integer>> pathMap = this.pathMap;
        int from = p.getFromFloor();
        int target = p.getToFloor();
        if (pathMap.get(from).contains(target)) {
            path.add(target);
        } else {
            path = this.generateDijkstra(pathMap, from, target);
        }
        notifyAll();
        return path;
    }

    public void createMap() {
        HashMap<Integer, ArrayList<Integer>> pathMap = new HashMap<>();
        for (int i = 1;i <= 11;i++) {
            for (Integer id: accessMap.keySet()) {
                if (accessMap.get(id).contains(i)) {
                    ArrayList<Integer> l;
                    if (pathMap.containsKey(i)) {
                        l = new ArrayList<>(pathMap.get(i));
                    } else {
                        l = new ArrayList<>();
                    }
                    ArrayList<Integer> list = new ArrayList<>(accessMap.get(id));
                    for (Integer integer : list) {
                        if (!l.contains(integer)) {
                            l.add(integer);
                        }
                    }
                    l.sort(Comparator.naturalOrder());
                    pathMap.put(i, l);
                }
            }
        }
        this.pathMap.clear();
        this.pathMap.putAll(pathMap);
    }

    public LinkedList<Integer> generateDijkstra(HashMap<Integer, ArrayList<Integer>> pathMap
            , int s, int d) {
        int source = s - 1;
        int destination = d - 1;
        int[][] matrix = new int[11][11];
        for (int i = 0;i < 11;i++) {
            for (int j = 0;j < 11;j++) {
                if (pathMap.containsKey(i + 1) && pathMap.get(i + 1).contains(j + 1)) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
            matrix[i][i] = 0;
        }
        int n = matrix.length;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        int[] pre = new int[n];
        for (int i = 0; i < n; i++) {
            dist[i] = Integer.MAX_VALUE;
            visited[i] = false;
            pre[i] = -1;
        }
        dist[source] = 0;
        while (true) {
            int minIndex = -1;
            int minDist = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!visited[i] && dist[i] < minDist) {
                    minIndex = i;
                    minDist = dist[i];
                }
            }
            if (minIndex == -1 || minIndex == destination) {
                break;
            }
            visited[minIndex] = true;
            for (int i = 0; i < n; i++) {
                if (!visited[i] && matrix[minIndex][i] > 0) {
                    int newDist = dist[minIndex] + matrix[minIndex][i];
                    if (newDist < dist[i]) {
                        dist[i] = newDist;
                        pre[i] = minIndex;
                    }
                }
            }
        }
        LinkedList<Integer> demand = new LinkedList<>();
        int current = destination;
        demand.addFirst(current + 1);
        while (pre[current] != -1) {
            current = pre[current];
            demand.addFirst(current + 1);
        }
        demand.removeFirst();
        return demand;
    }
}
