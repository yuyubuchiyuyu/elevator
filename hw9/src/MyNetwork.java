import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.HashMap;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> persons = new HashMap<>();
    private int tripleSum = 0; // 新加
    private final HashMap<Person, Integer> block = new HashMap<>(); // 新加

    public MyNetwork() {

    }

    public boolean containsPerson(int id) {
        return persons.containsKey(id);
    }

    public Person getPerson(int id) {
        return persons.get(id);
    }

    public void addPerson(Person person) throws EqualPersonIdException {
        if (!containsPerson(person.getId())) {
            persons.put(person.getId(), person);
            block.put(person, 1); // 孤立点单独成块
        } else {
            throw new MyEqualPersonIdException(person.getId());
        }
    }

    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1, id2);
        } else {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            // 先找交集，再加边
            for (Person neighbor : person1.getAcquaintance().values()) {
                if (neighbor.isLinked(person2)) {
                    tripleSum++;
                }
            }
            MyPerson root1 = person1.findRoot();
            MyPerson root2 = person2.findRoot();
            if (root1 == root2) { // 同根，不动
                person1.addAcqAndValue(person2, id2, value, false);
                person2.addAcqAndValue(person1, id1, value, false);
            } else { // 不同根，要合并（原则：小加入大）
                if (block.get(root1) <= block.get(root2)) { // 把块1合并到块2上
                    person1.addAcqAndValue(person2, id2, value, true);
                    person2.addAcqAndValue(person1, id1, value, false);
                    block.replace(root2, block.get(root1) + block.get(root2));
                    block.remove(root1);
                } else { // 把块2合并到块1上
                    person1.addAcqAndValue(person2, id2, value, false);
                    person2.addAcqAndValue(person1, id1, value, true);
                    block.replace(root1, block.get(root1) + block.get(root2));
                    block.remove(root2);
                }
            }
        }
    }

    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        } else {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            if (getPerson(id1).queryValue(getPerson(id2)) + value > 0) { // 仅改变值
                int oldValue = person1.queryValue(person2);
                person1.replaceAcqAndValue(person2, id2, oldValue + value);
                person2.replaceAcqAndValue(person1, id1, oldValue + value);
            } else { // 删去一条边
                MyPerson oldRoot = person1.findRoot();
                person1.removeAcqAndValue(id2);
                person2.removeAcqAndValue(id1);
                for (Person person : persons.values()) {
                    if (((MyPerson) person).findRoot() == oldRoot) {
                        ((MyPerson) person).resetRoot();
                    }
                }
                int num = block.get(oldRoot); // 旧块的大小
                int num1 = person1.modifyBlock(); // 修改1的根节点，用num1记录修改后1块的大小
                int num2 = num - num1; // 用num2记录修改后2块的大小
                if (person2.findRoot() != person1.findRoot()) { // 删边之后12不同块
                    person2.modifyBlock();
                    block.remove(oldRoot);
                    block.put(person1.findRoot(), num1);
                    block.put(person2.findRoot(), num2);
                } else { // 否则，删边之后12同块
                    block.remove(oldRoot);
                    block.put(person1.findRoot(), num);
                }
                // 删完之后，找交集
                for (Person neighbor : person1.getAcquaintance().values()) {
                    if (neighbor.isLinked(person2)) {
                        tripleSum--;
                    }
                }
            }
        }
    }

    public int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        } else {
            return getPerson(id1).queryValue(getPerson(id2));
        }
    }

    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else {
            MyPerson person1 = (MyPerson) getPerson(id1);
            MyPerson person2 = (MyPerson) getPerson(id2);
            return person1.findRoot() == person2.findRoot();
        }
    }

    public int queryBlockSum() {
        return block.size();
    }

    public int queryTripleSum() {
        return tripleSum;
    }

    public Person[] getPersons() {
        Person[] personTemp = new Person[persons.size()];
        int i = 0;
        for (Person person : persons.values()) {
            personTemp[i] = person;
            i++;
        }
        return personTemp;
    }
}