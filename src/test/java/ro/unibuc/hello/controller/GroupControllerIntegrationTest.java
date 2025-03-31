package ro.unibuc.hello.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.hello.config.NoSecurityConfig;
import ro.unibuc.hello.entity.Group;
import ro.unibuc.hello.repository.GroupRepository;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(NoSecurityConfig.class)
@Tag("IntegrationTest")
public class GroupControllerIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
            .withCommand("--replSet", "rs0");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    void setUp() {
        groupRepository.deleteAll();
    }

    @Test
    void testCreateGroup_success() throws Exception {
        String groupJson = "{ \"name\": \"Test Group\" }";

        mockMvc.perform(post("/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Group"));
    }

    @Test
    void testGetGroup_success() throws Exception {
        Group group = new Group();
        group.setName("Existing Group");
        group = groupRepository.save(group);

        mockMvc.perform(get("/group/" + group.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Existing Group"));
    }

    @Test
    void testDeleteGroup_success() throws Exception {
        Group group = new Group();
        group.setName("To Delete");
        group = groupRepository.save(group);

        mockMvc.perform(delete("/group/" + group.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Group deleted successfully."));
    }
}
