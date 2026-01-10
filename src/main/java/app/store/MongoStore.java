package app.store;

import app.model.Student;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

public class MongoStore {
    private MongoCollection<Document> collection;
    private Gson gson = new Gson();

    public MongoStore() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("schoolDB");
        this.collection = database.getCollection("students");
        initData();
    }

    private void initData() {
        if (collection.countDocuments() == 0) {
            System.out.println("MongoDB'ye veri yükleniyor...");
            for (int i = 0; i < 10000; i++) {
                String id = String.valueOf(2025000000 + i);
                Document doc = new Document("student_no", id)
                        .append("name", "Ogrenci " + i)
                        .append("department", "Muhendislik");
                collection.insertOne(doc);
            }
            System.out.println("MongoDB veri yükleme tamamlandı.");
        }
    }

    public String getStudent(String studentNo) {
        Document doc = collection.find(eq("student_no", studentNo)).first();
        if (doc != null) {
            
            doc.remove("_id"); 
            return doc.toJson();
        }
        return null;
    }
}
