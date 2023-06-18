import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final ArrayList<Person> acquaintance = new ArrayList<>();
    private final HashMap<Integer, Integer> value = new HashMap<>();
    private int socialValue = 0;
    private int money = 0;
    private final LinkedList<Message> messages = new LinkedList<>();
    private int maxValue = Integer.MIN_VALUE;
    private int globalBestAc = Integer.MIN_VALUE;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return (((Person) obj).getId() == id);
        }
        return false;
    }

    @Override
    public boolean isLinked(Person person) {
        if (person.getId() == id) {
            return true;
        }
        for (Person item : acquaintance) {
            if (item.getId() == person.getId() || person.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        for (Person item : acquaintance) {
            if (item.getId() == person.getId()) {
                return this.value.get(person.getId());
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Person p2) {
        return this.name.compareTo(p2.getName());
    }

    @Override
    public void addSocialValue(int num) {
        this.socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return this.messages;
    }

    @Override
    public List<Message> getReceivedMessages() {
        ArrayList<Message> newMessages = new ArrayList<>();
        for (int i = 0;i <= 4 && i < this.messages.size();i++) {
            newMessages.add(this.messages.get(i));
        }
        return newMessages;
    }

    @Override
    public void addMoney(int num) {
        this.money += num;
    }

    @Override
    public int getMoney() {
        return this.money;
    }

    public void addPerson(Person person) {
        acquaintance.add(person);
    }

    public void addValue(int id, int v) {
        value.put(id, v);
        if (v == maxValue) {
            if (globalBestAc == Integer.MIN_VALUE || id < globalBestAc) {
                globalBestAc = id;
            }
        } else if (v > maxValue) {
            maxValue = v;
            globalBestAc = id;
        }
    }

    public void setValue(int id, int newValue) {
        int oldValue = value.get(id);
        value.replace(id, oldValue + newValue);
        if (oldValue + newValue > maxValue) {
            maxValue = oldValue + newValue;
            globalBestAc = id;
        } else if ((oldValue + newValue < maxValue && globalBestAc == id)) {
            maxValue = Collections.max(value.values());
            ArrayList<Integer> keys = new ArrayList<>();
            for (HashMap.Entry<Integer, Integer> entry : value.entrySet()) {
                if (entry.getValue() == maxValue) {
                    keys.add(entry.getKey());
                }
            }
            globalBestAc = Collections.min(keys);
        } else if (oldValue + newValue == maxValue && globalBestAc > id) {
            globalBestAc = id;
        }
    }

    public void removeRel(int id) {
        ArrayList<Person> list = new ArrayList<>(acquaintance);
        for (Person item : list) {
            if (item.getId() == id) {
                acquaintance.remove(item);
            }
        }
        int v = value.get(id);
        value.remove(id);
        if (v == maxValue && value.size() > 0) {
            maxValue = Collections.max(value.values());
            ArrayList<Integer> keys = new ArrayList<>();
            for (HashMap.Entry<Integer, Integer> entry : value.entrySet()) {
                if (entry.getValue() == maxValue) {
                    keys.add(entry.getKey());
                }
            }
            globalBestAc = Collections.min(keys);
        }
        if (value.size() == 0) {
            maxValue = Integer.MIN_VALUE;
            globalBestAc = Integer.MIN_VALUE;
        }
    }

    public void addMessage(Message message) {
        this.messages.addFirst(message);
    }

    public ArrayList<Person> getAcquaintance() {
        return this.acquaintance;
    }

    public int getGlobalBestAc() {
        return this.globalBestAc;
    }

    public HashMap<Integer, Integer> getValue() {
        return value;
    }

    public void deleteNotice() {
        messages.removeIf(message -> message instanceof NoticeMessage);
    }

}