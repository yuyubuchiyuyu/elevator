import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;

public class RequestPool {
    private final ArrayList<PersonRequest> requests = new ArrayList<>();

    public void addRequest(PersonRequest request) {
        requests.add(request);
    }

    public Integer getSize() {
        return requests.size();
    }

    public PersonRequest getRequest(Integer i) {
        return requests.get(i);
    }

    public void removeRequest(PersonRequest request) {
        requests.remove(request);
    }

    public void changeResult(int i) { // 将requests中的第i个提到最前面
        if (i != 0) {
            PersonRequest requestI = requests.get(i);
            for (int j = i - 1; j >= 0; j--) {
                requests.set(j + 1, requests.get(j));
            }
            requests.set(0, requestI);
        }
    }
}
