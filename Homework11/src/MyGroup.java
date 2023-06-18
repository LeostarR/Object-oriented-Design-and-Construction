import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

import java.util.ArrayList;

public class MyGroup implements Group {
    private final int id;
    private final ArrayList<Person> people;
    private int globalValueSum = 0;
    private int globalAgeMean = 0;
    private int globalAgeVar = 0;
    private boolean changeMean = true;
    private boolean changeVar = true;

    public MyGroup(int id) {
        this.id = id;
        this.people = new ArrayList<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group) {
            return (((Group) obj).getId() == this.id);
        }
        return false;
    }

    @Override
    public void addPerson(Person person) {
        if (!hasPerson(person)) {
            changeMean = true;
            changeVar = true;
            for (Person p : people) {
                if (p.isLinked(person)) {
                    globalValueSum += person.queryValue(p);
                    globalValueSum += p.queryValue(person);
                }
            }
            this.people.add(person);
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        for (Person p : people) {
            if (p.equals(person)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getValueSum() {
        return globalValueSum;
    }

    @Override
    public int getAgeMean() {
        if (people.size() == 0) {
            return 0;
        }
        if (changeMean) {
            changeMean = false;
            int sum = 0;
            for (Person person : people) {
                sum += person.getAge();
            }
            sum /= people.size();
            globalAgeMean = sum;
        }
        return globalAgeMean;
    }

    @Override
    public int getAgeVar() {
        if (people.size() == 0) {
            return 0;
        }
        if (changeVar) {
            changeVar = false;
            int sum = 0;
            for (Person person : people) {
                int cnt = person.getAge() - getAgeMean();
                sum += cnt * cnt;
            }
            sum /= people.size();
            globalAgeVar = sum;
        }
        return globalAgeVar;
    }

    @Override
    public void delPerson(Person person) {
        if (hasPerson(person)) {
            this.people.remove(person);
            for (Person p : people) {
                if (p.isLinked(person)) {
                    globalValueSum -= person.queryValue(p);
                    globalValueSum -= p.queryValue(person);
                }
            }
            changeMean = true;
            changeVar = true;
        }
    }

    @Override
    public int getSize() {
        return this.people.size();
    }

    public void addValueSum(int value) {
        this.globalValueSum += 2 * value;
    }
}
