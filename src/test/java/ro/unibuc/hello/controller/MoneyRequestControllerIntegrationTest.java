package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.hello.entity.BankAccount;
import ro.unibuc.hello.entity.MoneyRequest;
import ro.unibuc.hello.service.MoneyRequestService;
import ro.unibuc.hello.repository.BankAccountRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class MoneyRequestControllerIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
            .withExposedPorts(27017)
            .withSharding();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.connection.url", () ->
                "mongodb://localhost:" + mongoDBContainer.getMappedPort(27017));
    }

    @BeforeAll
    public static void startContainer() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void stopContainer() {
        mongoDBContainer.stop();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MoneyRequestService moneyRequestService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String requestId;

    @BeforeEach
    public void cleanDbAndInsertData() {
        moneyRequestService.deleteAllRequests();
        bankAccountRepository.deleteAll();

        BankAccount sender = new BankAccount("senderId", "IBAN123", "client1", 1000.0, "RON");
        BankAccount receiver = new BankAccount("receiverId", "IBAN456", "client2", 1000.0, "RON");
        bankAccountRepository.save(sender);
        bankAccountRepository.save(receiver);

        MoneyRequest saved = moneyRequestService.createRequest(
                new MoneyRequest(null, "receiverId", "senderId", 100.0, "PENDING")
        );

        requestId = saved.getId();
    }

    @Test
    public void testGetAllRequests() throws Exception {
        mockMvc.perform(get("/api/money-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetRequestById() throws Exception {
        mockMvc.perform(get("/api/money-requests/" + requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    public void testGetRequestsForUser() throws Exception {
        mockMvc.perform(get("/api/money-requests/user/receiverId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)); // This will fail if data doesn't match
    }

    @Test
    public void testCreateRequest() throws Exception {
        MoneyRequest newRequest = new MoneyRequest(null, "receiverId", "senderId", 300.0, "PENDING");

        mockMvc.perform(post("/api/money-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(300.0));
    }

    @Test
    public void testUpdateRequestStatus() throws Exception {
        mockMvc.perform(put("/api/money-requests/" + requestId + "/status")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void testUpdateRequestStatusWithInvalidStatus() throws Exception {
        mockMvc.perform(put("/api/money-requests/" + requestId + "/status")
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest());
    }
}
