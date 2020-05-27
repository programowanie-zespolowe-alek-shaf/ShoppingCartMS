package pl.agh.shopping.card.application.controller.shopping.card.delete;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.application.rest.MicroService;
import pl.agh.shopping.card.application.rest.RestClient;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;
import pl.agh.shopping.card.mysql.repository.ShoppingCardItemRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.agh.shopping.card.application.config.TestUtils.mapObjectToStringJson;


@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@Sql({"classpath:schema-shopping.sql", "classpath:data-shopping.sql"})
public class DeleteShoppingCardItemControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ShoppingCardItemRepository shoppingCardItemRepository;
    @MockBean
    private RestClient restClient;

    @Test
    public void createAndDeleteSuccessTest() throws Exception {



        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);


        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 1);


        String requestJson = mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.book.id").value("1"))
                .andExpect(jsonPath("quantity").value("1"));

        List<ShoppingCardItem> all = shoppingCardItemRepository.findAll();
        ShoppingCardItem shoppingCardItem = all.get(all.size() - 1);
        Long id = shoppingCardItem.getId();

        assertNotNull(shoppingCardItem);
        assertEquals(1L, shoppingCardItem.getBookId(), 0.01);
        assertEquals(shoppingCardItem.getQuantity(), Integer.valueOf(1));

        mvc.perform(MockMvcRequestBuilders.delete("/shoppingCards/1/items/" + id))
                .andExpect(status().is(204));

        ShoppingCardItem shoppingCardItemAfterDelete = shoppingCardItemRepository.findById(id).orElse(null);

        assertNull(shoppingCardItemAfterDelete);
    }

    @Test
    public void notFoundTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/shoppingCards/1/items/111"))
                .andExpect(status().is(404));
    }
}
