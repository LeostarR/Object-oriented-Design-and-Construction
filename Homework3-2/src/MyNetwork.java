import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.GroupIdNotFoundException;
import com.oocourse.spec2.exceptions.EqualGroupIdException;
import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.EqualMessageIdException;
import com.oocourse.spec2.exceptions.MessageIdNotFoundException;
import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MyNetwork implements Network {
    private final ArrayList<Person> people;
    private final ArrayList<Group> groups;
    private final ArrayList<Message> messages;
    private final HashMap<Integer, Integer> symbol = new HashMap<>();
    private int globalQts = 0;
    private int globalQbs = 0;
    private int globalCouSum = 0;
    private final HashSet<Pair<Integer, Integer>> pairs = new HashSet<>();
    private final HashSet<Integer> peopleIdSet = new HashSet<>();
    private final HashSet<Integer> groupIdSet = new HashSet<>();

    public MyNetwork() {
        people = new ArrayList<>();
        groups = new ArrayList<>();
        messages = new ArrayList<>();
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
        for (Person person : people) {
            if (person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        for (Person p : people) {
            if (p.equals(person)) {
                throw new MyEqualPersonIdException(person.getId());
            }
        }
        people.add(person);
        peopleIdSet.add(person.getId());
        symbol.put(person.getId(), person.getId());
        globalQbs++;
    }

    public int calCu(int id1, int id2) {
        int sum = 0;
        try {
            int acid1;
            int acid11;
            if (((MyPerson) getPerson(id1)).getAcquaintance().size() == 0) {
                acid1 = Integer.MIN_VALUE;
                acid11 = Integer.MAX_VALUE;
            } else {
                acid1 = queryBestAcquaintance(id1);
                if (((MyPerson) getPerson(acid1)).getAcquaintance().size() != 0) {
                    acid11 = queryBestAcquaintance(acid1);
                } else {
                    acid11 = Integer.MAX_VALUE;
                }
            }
            int acid2;
            int acid22;
            if (((MyPerson) getPerson(id2)).getAcquaintance().size() == 0) {
                acid2 = Integer.MIN_VALUE;
                acid22 = Integer.MAX_VALUE;
            } else {
                acid2 = queryBestAcquaintance(id2);
                if (((MyPerson) getPerson(acid2)).getAcquaintance().size() != 0) {
                    acid22 = queryBestAcquaintance(acid2);
                } else {
                    acid22 = Integer.MAX_VALUE;
                }
            }
            if (acid11 == id1 && ((MyPerson) getPerson(id1)).getAcquaintance().size() != 0) {
                sum++;
            }
            if (acid22 == id2 && ((MyPerson) getPerson(id2)).getAcquaintance().size() != 0) {
                sum++;
            }
            if (acid1 == id2 && acid2 == id1
                    && ((MyPerson) getPerson(id1)).getAcquaintance().size() != 0
                    && ((MyPerson) getPerson(id2)).getAcquaintance().size() != 0) {
                sum--;
            }
        } catch (AcquaintanceNotFoundException | PersonIdNotFoundException e) {
            throw new RuntimeException(e);
        }
        return sum;
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        if (contains(id1) && contains(id2) && !getPerson(id1).isLinked(getPerson(id2))) {
            final int oldCs = calCu(id1, id2);
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            person1.addPerson(person2);
            person2.addPerson(person1);
            person1.addValue(id2, value);
            person2.addValue(id1, value);
            if (find(id1) != find(id2)) {
                globalQbs--;
            }
            merge(id1, id2);
            modifyQts(id1, id2, '+');
            modifyValueSum(id1, id2, value);
            int newCs = calCu(id1, id2);
            globalCouSum += (newCs - oldCs);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else {
            throw new MyEqualRelationException(id1, id2);
        }
    }

    public int find(int key) {
        if (symbol.get(key) != key) {
            symbol.put(key, find(symbol.get(key)));
        }
        return symbol.get(key);
    }

    public void merge(int id1, int id2) {
        int i1 = Integer.min(id1, id2);
        int i2 = Integer.max(id1, id2);
        int root1 = find(i1);
        int root2 = find(i2);
        if (root1 != root2) {
            symbol.put(root1, root2);
        }
        pairs.add(new Pair<>(i1, i2));
    }

    public void disconnect(int id1, int id2) {
        int i1 = Integer.min(id1, id2);
        int i2 = Integer.max(id1, id2);
        pairs.remove(new Pair<>(i1, i2));
        for (HashMap.Entry<Integer, Integer> entry : symbol.entrySet()) {
            entry.setValue(entry.getKey());
        }
        for (Pair pair : pairs) {
            merge((Integer) pair.getKey(), (Integer) pair.getValue());
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualPersonIdException, RelationNotFoundException {
        if (contains(id1) && contains(id2) && id1 != id2 && getPerson(id1).isLinked(getPerson(id2))
                && getPerson(id1).queryValue(getPerson(id2)) + value > 0) {
            final int oldCs = calCu(id1, id2);
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            person1.setValue(id2, value);
            person2.setValue(id1, value);
            modifyValueSum(id1, id2, value);
            int newCs = calCu(id1, id2);
            globalCouSum += (newCs - oldCs);
        } else if (contains(id1) && contains(id2) && id1 != id2
                && getPerson(id1).isLinked(getPerson(id2))
                && getPerson(id1).queryValue(getPerson(id2)) + value <= 0) {
            final int oldCs = calCu(id1, id2);
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            final int v = person1.queryValue(person2);
            person1.removeRel(id2);
            person2.removeRel(id1);
            disconnect(id1, id2);
            if (find(id1) != find(id2)) {
                globalQbs++;
            }
            modifyQts(id1, id2, '-');
            modifyValueSum(id1, id2, -v);
            int newCs = calCu(id1, id2);
            globalCouSum += (newCs - oldCs);
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (contains(id1) && contains(id2) && id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        } else {
            throw new MyRelationNotFoundException(id1, id2);
        }
    }

    public void modifyQts(int id1, int id2, char operation) {
        int v = min(id1, id2);
        MyPerson person = (MyPerson) getPerson(v);
        for (Person item : person.getAcquaintance()) {
            if (item.isLinked(getPerson(max(id1, id2)))
                    && item.getId() != id1 && item.getId() != id2) {
                if (operation == '+') {
                    globalQts++;
                } else if (operation == '-') {
                    globalQts--;
                }
            }
        }
    }

    public void modifyValueSum(int id1, int id2, int value) {
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);
        for (Group group : groups) {
            if (group.hasPerson(person1) && group.hasPerson(person2)) {
                ((MyGroup) group).addValueSum(value);
            }
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
        } else {
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
    public void addGroup(Group group) throws EqualGroupIdException {
        if (!groupIdSet.contains(group.getId())) {
            groups.add(group);
            groupIdSet.add(group.getId());
        } else {
            throw new MyEqualGroupIdException(group.getId());
        }
    }

    @Override
    public Group getGroup(int id) {
        for (Group group : groups) {
            if (group.getId() == id) {
                return group;
            }
        }
        return null;
    }

    @Override
    public void addToGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (groupIdSet.contains(id2) && peopleIdSet.contains(id1)
                && !getGroup(id2).hasPerson(getPerson(id1)) && getGroup(id2).getSize() <= 1111) {
            getGroup(id2).addPerson(getPerson(id1));
        } else if (groupIdSet.contains(id2) && peopleIdSet.contains(id1)
                && !getGroup(id2).hasPerson(getPerson(id1)) && getGroup(id2).getSize() > 1111) {
            return;
        } else if (!groupIdSet.contains(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (groupIdSet.contains(id2) && !peopleIdSet.contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else {
            throw new MyEqualPersonIdException(id1);
        }
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (groupIdSet.contains(id)) {
            return getGroup(id).getValueSum();
        } else {
            throw new MyGroupIdNotFoundException(id);
        }
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (groupIdSet.contains(id)) {
            return getGroup(id).getAgeVar();
        } else {
            throw new MyGroupIdNotFoundException(id);
        }
    }

    @Override
    public void delFromGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (groupIdSet.contains(id2)
                && peopleIdSet.contains(id1) && getGroup(id2).hasPerson(getPerson(id1))) {
            getGroup(id2).delPerson(getPerson(id1));
        } else if (!groupIdSet.contains(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (groupIdSet.contains(id2) && !peopleIdSet.contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else {
            throw new MyEqualPersonIdException(id1);
        }
    }

    @Override
    public boolean containsMessage(int id) {
        for (Message message : messages) {
            if (message.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addMessage(Message message) throws EqualMessageIdException, EqualPersonIdException {
        if (!containsMessage(message.getId())
                && (message.getType() != 0 || !message.getPerson1().equals(message.getPerson2()))) {
            messages.add(message);
        } else if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
    }

    @Override
    public Message getMessage(int id) {
        for (Message message : messages) {
            if (message.getId() == id) {
                return message;
            }
        }
        return null;
    }

    @Override
    public void sendMessage(int id) throws RelationNotFoundException,
            MessageIdNotFoundException, PersonIdNotFoundException {
        if (containsMessage(id) && getMessage(id).getType() == 0
                && getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2())
                && getMessage(id).getPerson1() != getMessage(id).getPerson2()) {
            getMessage(id).getPerson1().addSocialValue(getMessage(id).getSocialValue());
            getMessage(id).getPerson2().addSocialValue(getMessage(id).getSocialValue());
            Message message = getMessage(id);
            this.messages.remove(message);
            ((MyPerson)message.getPerson2()).addMessage(message);
        } else if (containsMessage(id) && getMessage(id).getType() == 1
                && getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1())) {
            Message message = getMessage(id);
            for (Person person : people) {
                if (message.getGroup().hasPerson(person)) {
                    person.addSocialValue(message.getSocialValue());
                }
            }
            this.messages.remove(message);
        } else if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        } else if (containsMessage(id) && getMessage(id).getType() == 0 &&
                !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()))) {
            throw new MyRelationNotFoundException(getMessage(id).getPerson1().getId()
                    , getMessage(id).getPerson2().getId());
        } else if (containsMessage(id) && getMessage(id).getType() == 1
                && !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
            throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
        }
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (contains(id)) {
            return getPerson(id).getSocialValue();
        } else {
            throw new MyPersonIdNotFoundException(id);
        }
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (contains(id)) {
            return getPerson(id).getReceivedMessages();
        } else {
            throw new MyPersonIdNotFoundException(id);
        }
    }

    @Override
    public int queryBestAcquaintance(int id)
            throws PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (contains(id) && ((MyPerson) getPerson(id)).getAcquaintance().size() != 0) {
            return ((MyPerson) getPerson(id)).getGlobalBestAc();
        } else if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            throw new MyAcquaintanceNotFoundException(id);
        }
    }

    @Override
    public int queryCoupleSum() {
        return globalCouSum;
    }

    @Override
    public int modifyRelationOKTest(int id1, int id2, int value,
                                    HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                    HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        OkTest ok = new OkTest(id1, id2, value, beforeData, afterData);
        return ok.test();
    }
}