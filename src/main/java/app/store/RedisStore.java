package app.store;

import app.model.Student;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

public class RedisStore {
    private Jedis jedis;
    private Gson gson = new Gson();

    public RedisStore() {
        
        this.jedis = new Jedis("localhost", 6379);
        System.out.println("Redis bağlantısı sağlandı.");
        initData();
    }

    private void initData() {
        
        if (jedis.dbSize() < 10000) {
            System.out.println("Redis'e veri yükleniyor...");
            for (int i = 0; i < 10000; i++) {
                String id = String.valueOf(2025000000 + i);
                Student s = new Student(id, "Ogrenci " + i, "Muhendislik");
                jedis.set(id, gson.toJson(s));
            }
            System.out.println("Redis veri yükleme tamamlandı.");
        }
    }

    public String getStudent(String studentNo) {
        return jedis.get(studentNo);
    }
}
