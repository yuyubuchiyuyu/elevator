import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Person;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Test {
    private final MyNetwork myNetwork;

    public Test(MyNetwork myNetwork) {
        this.myNetwork = myNetwork;
    }

    @Parameterized.Parameters
    public static Collection prepareData() throws EqualPersonIdException, PersonIdNotFoundException, EqualRelationException, RelationNotFoundException {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int testNum = 20;//测试次数,可根据需求调整

        Object[][] object = new Object[testNum][];
        for (int i = 0; i < testNum; i++) {
            int personNum = random.nextInt(10) + 10;
            int indexNum = random.nextInt(150) + 100;
            MyNetwork myNetwork = new MyNetwork();
            for (int j = 0; j < personNum; j++) {
                MyPerson person = new MyPerson(j, "people" + j, j);
                if (!myNetwork.containsPerson(j)) {
                    myNetwork.addPerson(person);
                }
            }
            int j = 0;
            while (j < indexNum) {
                int sign = random.nextInt(2);
                if (sign == 0) { // 加关系
                    // System.out.println(personNum);
                    int id1 = random.nextInt(personNum);
                    int id2 = random.nextInt(personNum);
                    if (myNetwork.containsPerson(id1) && myNetwork.containsPerson(id2)
                            && !myNetwork.getPerson(id1).isLinked(myNetwork.getPerson(id2))) {
                        int value = random.nextInt(101);
                        myNetwork.addRelation(id1, id2, value);
                        j++;
                    }
                } else if (sign == 1) {
                    // System.out.println(personNum);
                    int id1 = random.nextInt(personNum);
                    int id2 = random.nextInt(personNum);
                    int value = random.nextInt(201) - 100; // 生成[min, max]之间的随机数
                    if (myNetwork.containsPerson(id1) && myNetwork.containsPerson(id2) && id1 != id2
                            && myNetwork.getPerson(id1).isLinked(myNetwork.getPerson(id2))) {
                        // System.out.println(((MyPerson)myNetwork.getPerson(id1)).findRoot().getId()+" "+((MyPerson)myNetwork.getPerson(id2)).findRoot().getId());
                        myNetwork.modifyRelation(id1, id2, value);
                        j++;
                    }
                }
            }
            object[i] = new Object[]{myNetwork};
        }
        return Arrays.asList(object);
    }


    @org.junit.Test
    public void queryTripleSum() {
        Person[] before = myNetwork.getPersons();
        int sum = 0;
        for (int i = 0; i < myNetwork.getPersons().length; i++) {
            for (int j = i + 1; j < myNetwork.getPersons().length; j++) {
                for (int k = j + 1; k < myNetwork.getPersons().length; k++) {
                    if (myNetwork.getPersons()[i].isLinked(myNetwork.getPersons()[j])
                            && myNetwork.getPersons()[j].isLinked(myNetwork.getPersons()[k])
                            && myNetwork.getPersons()[k].isLinked(myNetwork.getPersons()[i])) {
                        sum++;
                    }
                }
            }
        }
        int actual = myNetwork.queryTripleSum();
        Person[] after = myNetwork.getPersons();
        boolean flag = true;
        if (before.length != after.length) {
            flag = false;
        } else {
            for (int i = 0; i < before.length; i++) {
                boolean has = false;
                for (int j = 0; j < after.length; j++) {
                    if (((MyPerson) before[i]).strictEquals(after[j])) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    flag = false;
                    break;
                }
            }
        }
        assertTrue(flag);
        assertEquals(sum, actual);
        // System.out.println(sum);
    }
}