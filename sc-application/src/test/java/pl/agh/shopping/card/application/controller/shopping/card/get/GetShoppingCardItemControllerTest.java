package pl.agh.shopping.card.application.controller.shopping.card.get;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.agh.shopping.card.application.config.WithCustomUser;
import pl.agh.shopping.card.application.rest.MicroService;
import pl.agh.shopping.card.application.rest.RestClient;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"classpath:schema-shopping.sql", "classpath:data-shopping.sql"})
public class GetShoppingCardItemControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private RestClient restClient;

    @Test
    @WithCustomUser(roles = "ADMIN")
    public void adminSuccessTest() throws Exception {

        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);


        mvc.perform(MockMvcRequestBuilders.get("/shoppingCards/1/items/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.book.id").value("1"))
                .andExpect(jsonPath("quantity").value("3"));
    }

    @Test
    @WithCustomUser("user1")
    public void loggedInSuccessTest() throws Exception {

        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);


        mvc.perform(MockMvcRequestBuilders.get("/shoppingCards/1/items/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.book.id").value("1"))
                .andExpect(jsonPath("quantity").value("3"));
    }

    @Test
    @WithCustomUser("AnotherUser")
    public void otherSuccessTest() throws Exception {

        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);


        mvc.perform(MockMvcRequestBuilders.get("/shoppingCards/1/items/1"))
                .andExpect(status().is(403));
    }

    @Test
    public void SuccessTest() throws Exception {
        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);

        mvc.perform(MockMvcRequestBuilders.get("/shoppingCards/1/items/1"))
                .andExpect(status().is(401));
    }


    @Test
    public void notFoundTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/shoppingCards/1/items/11"))
                .andExpect(status().is(404));
    }
}