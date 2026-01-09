package app.store;

import app.model.Student;
import com.google.gson.Gson;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HazelcastStore {
    private HazelcastInstance hazelcastInstance;
    private IMap<String, String> map;
    private Gson gson = new Gson();

    public HazelcastStore() {
        this.hazelcastInstance = Hazelcast.newHazelcastInstance();
        this.map = hazelcastInstance.getMap("students");
        initData();
    }

    private void initData() {
        if (map.isEmpty()) {
            System.out.println("Hazelcast'e veri yükleniyor...");
            for (int i = 0; i < 10000; i++) {
                String id = String.valueOf(2025000000 + i);
                Student s = new Student(id, "Ogrenci " + i, "Muhendislik");
                map.put(id, gson.toJson(s));
            }
            System.out.println("Hazelcast veri yükleme tamamlandı.");
        }
    }

    public String getStudent(String studentNo) {
        return map.get(studentNo);
    }
}
