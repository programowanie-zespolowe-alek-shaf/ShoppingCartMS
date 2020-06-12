package pl.agh.shopping.card.application.controller.shopping.card.get;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.agh.shopping.card.application.rest.MicroService;
import pl.agh.shopping.card.application.rest.RestClient;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@Sql({"classpath:schema-shopping.sql", "classpath:data-shopping.sql"})
public class GetShoppingCardControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private RestClient restClient;

    @Test
    public void successTest() throws Exception {
        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .build();
        Map<String, Object> book2 = ImmutableMap.<String, Object>builder()
                .put("id", 2)
                .put("title", "Dziady")
                .put("available", true)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);
        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/2", Map.class)).thenReturn(book2);

        mvc.perform(MockMvcRequestBuilders.get("/shoppingCards/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("username").value("user1"))
                .andExpect(jsonPath("createDate").value("2020-05-04"))
                .andExpect(jsonPath("items.list[0].id").value("1"))
                .andExpect(jsonPath("items.list[0].book.id").value("1"))
                .andExpect(jsonPath("items.list[0].book.title").value("Lalka"))
                .andExpect(jsonPath("items.list[0].book.available").value("true"))
                .andExpect(jsonPath("items.list[1].id").value("2"))
                .andExpect(jsonPath("items.list[1].book.id").value("2"))
                .andExpect(jsonPath("items.list[1].book.title").value("Dziady"))
                .andExpect(jsonPath("items.list[1].book.available").value("true"))
                .andExpect(jsonPath("items.count").value("4"));
    }

    @Test
    public void notFoundTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/shoppingCards/10"))
                .andExpect(status().is(404));
    }
}
