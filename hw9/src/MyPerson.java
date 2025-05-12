import com.oocourse.spec1.main.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private MyPerson root; // 新加
    private final HashMap<Integer, Person> acquaintance = new HashMap<>();
    private final HashMap<Integer, Integer> value = new HashMap<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.root = this;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public HashMap<Integer, Person> getAcquaintance() { // 新加
        return acquaintance;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Person) { // obj != null && obj instanceof Person
            return ((Person) obj).getId() == id;
        } else {
            return false;
        }
    }

    public boolean isLinked(Person person) {
        return (acquaintance.containsKey(person.getId()) || person.getId() == id);
    }

    public int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return value.get(person.getId());
        } else {
            return 0;
        }
    }

    public void addAcqAndValue(MyPerson person, int id, int value, boolean sign) { // 新加
        this.acquaintance.put(id, person);
        this.value.put(id, value);
        if (sign) {
            HashSet<Person> visited = new HashSet<>();
            this.modifyRoot(person.findRoot(), visited);
        }
    }

    public void replaceAcqAndValue(Person person, int id, int value) { // 新加
        // this.acquaintance.replace(id, person);
        this.value.replace(id, value);
    }

    public void removeAcqAndValue(int id) { // 新加
        this.acquaintance.remove(id);
        this.value.remove(id);
    }

    public int modifyBlock() { // 新加
        HashSet<Person> visited = new HashSet<>();
        this.modifyRoot(this, visited);
        return visited.size();
    }

    public MyPerson findRoot() { // 新加
        if (this.root == this) { // 自己是根节点
            return this.root;
        } else {
            this.root = this.root.findRoot(); // 更新root
            return this.root;
        }
    }

    public void modifyRoot(MyPerson person, HashSet<Person> visited) { // 新加
        if (visited.contains(this)) {
            return;
        }
        this.root = person;
        visited.add(this);
        for (Person neighbor : this.acquaintance.values()) {
            ((MyPerson) neighbor).modifyRoot(person, visited);
        }
    }

    public void resetRoot() {
        this.root = this;
    }

    boolean strictEquals(Person person) {
        if (this.id != person.getId()) {
            return false;
        } else if (!Objects.equals(this.name, person.getName())) {
            return false;
        } else if (this.age != person.getAge()) {
            return false;
        } else if (this.root != ((MyPerson) person).findRoot()) {
            return false;
        } else if (this.acquaintance.size() != ((MyPerson) person).acquaintance.size()) {
            return false;
        } else if (this.value.size() != ((MyPerson) person).value.size()) {
            return false;
        } else {
            for (int id : this.acquaintance.keySet()) {
                if (!((MyPerson) person).acquaintance.containsKey(id)) {
                    return false;
                }
                if (!((MyPerson) person).value.containsKey(id)) {
                    return false;
                }
            }
            return true;
        }
    }
}

