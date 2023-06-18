import com.oocourse.spec1.main.Person;

import java.util.ArrayList;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final ArrayList<Person> acquaintance = new ArrayList<>();
    private final ArrayList<Integer> value = new ArrayList<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public ArrayList<Person> getAcquaintance() {
        return acquaintance;
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
        } else {
            return false;
        }
    }

    @Override
    public boolean isLinked(Person person) {
        for (Person item : acquaintance) {
            if (item.getId() == person.getId() || person.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        for (int i = 0;i < acquaintance.size();i++) {
            if (acquaintance.get(i).getId() == person.getId()) {
                return this.value.get(i);
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Person p2) {
        return this.name.compareTo(p2.getName());
    }

    public void addPerson(Person person) {
        acquaintance.add(person);
    }

    public void addValue(int v) {
        value.add(v);
    }
}
