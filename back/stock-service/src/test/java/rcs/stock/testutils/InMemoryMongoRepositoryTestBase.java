package rcs.stock.testutils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.After;
import org.junit.Before;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.InetSocketAddress;

public class InMemoryMongoRepositoryTestBase {

    private MongoServer server;
    private MongoTemplate mongoTemplate;

    @Before
    public final void setupMongo() {
        server = new MongoServer(new MemoryBackend());
        InetSocketAddress address = server.bind();
        String connectionString = "mongodb:/" + address;
        MongoClient mongoClient = MongoClients.create(connectionString);
        mongoTemplate = new MongoTemplate(mongoClient, "test-db");
    }

    @After
    public void cleanup() {
        server.shutdown();
    }

    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
