import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MyNetwork implements Network {
    private final ArrayList<Person> people;
    private final HashMap<Integer, Integer> symbol = new HashMap<>();
    private int globalQts = 0;
    private int globalQbs = 0;

    public MyNetwork() {
        people = new ArrayList<>();
    }

    @Override
    public boolean contains(int id) {
        for (Person person : people) {
            if (person.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Person getPerson(int id) {
        if (this.contains(id)) {
            for (Person person : people) {
                if (person.getId() == id) {
                    return person;
                }
            }
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        for (Person value : people) {
            if (value.equals(person)) {
                throw new MyEqualPersonIdException(person.getId());
            }
        }
        people.add(person);
        symbol.put(person.getId(), person.getId());
        globalQbs++;
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        if (contains(id1) && contains(id2) && !getPerson(id1).isLinked(getPerson(id2))) {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            person1.addPerson(person2);
            person2.addPerson(person1);
            person1.addValue(value);
            person2.addValue(value);
            if (find(id1) != find(id2)) {
                globalQbs--;
            }
            merge(id1, id2);
            int v = min(id1, id2);
            MyPerson person = (MyPerson) getPerson(v);
            for (Person item : person.getAcquaintance()) {
                if (item.isLinked(getPerson(max(id1, id2)))
                        && item.getId() != id1 && item.getId() != id2) {
                    globalQts++;
                }
            }
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else {
            throw new MyEqualRelationException(id1, id2);
        }
    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (contains(id1) && contains(id2) && getPerson(id1).isLinked(getPerson(id2))) {
            return getPerson(id1).queryValue(getPerson(id2));
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else /*if(contains(id1) && contains(id2) && !getPerson(id1).isLinked(getPerson(id2)))*/ {
            throw new MyRelationNotFoundException(id1, id2);
        }
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (this.contains(id1) && this.contains(id2)) {
            return find(id1) == find(id2);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else {
            throw new MyPersonIdNotFoundException(id2);
        }
    }

    @Override
    public int queryBlockSum() {
        return globalQbs;
    }

    @Override
    public int queryTripleSum() {
        return globalQts;
    }

    @Override
    public boolean queryTripleSumOKTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                        HashMap<Integer, HashMap<Integer, Integer>> afterData,
                                        int result) {
        boolean flag1 = beforeData.equals(afterData);
        ArrayList<Integer> list = new ArrayList<>(beforeData.keySet());
        //
        int sum = 0;
        for (int i = 0; i < list.size();i++) {
            for (int j = i + 1; j < list.size();j++) {
                for (int k = j + 1; k < list.size();k++) {
                    if (beforeData.get(list.get(i)).containsKey((list.get(j)))
                            && beforeData.get(list.get(j)).containsKey((list.get(i)))
                            && beforeData.get(list.get(j)).containsKey((list.get(k)))
                            && beforeData.get(list.get(k)).containsKey((list.get(j)))
                            && beforeData.get(list.get(k)).containsKey((list.get(i)))
                            && beforeData.get(list.get(i)).containsKey((list.get(k)))) {
                        sum++;
                    }
                }
            }
        }
        //
        boolean flag2 = (sum == result);
        return flag1 & flag2;
    }

    public HashMap<Integer, ArrayList<Integer>> generateMap() {
        HashMap<Integer, ArrayList<Integer>> hash = new HashMap<>();
        for (Person person: people) {
            MyPerson p = (MyPerson) person;
            ArrayList<Person> list = new ArrayList<>(p.getAcquaintance());
            ArrayList<Integer> l = new ArrayList<>();
            for (Person value : list) {
                if (value.getId() != person.getId()) {
                    l.add(value.getId());
                }
            }
            hash.put(person.getId(), l);
        }
        return hash;
    }

    public boolean hasPath(int id1, int id2, boolean[] visited,
                           HashMap<Integer, ArrayList<Integer>> graph) {
        if (id1 == id2) {
            return true;
        }
        visited[id1] = true;
        for (int neighbor : graph.get(id1)) {
            if (!visited[neighbor]) {
                if (hasPath(neighbor, id2, visited, graph)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int find(int key) {
        if (symbol.get(key) != key) {
            symbol.put(key, find(symbol.get(key)));
        }
        return symbol.get(key);
    }

    public void merge(int id1, int id2) {
        int root1 = find(id1);
        int root2 = find(id2);
        if (root1 != root2) {
            symbol.put(root1, root2);
        }
    }
}
