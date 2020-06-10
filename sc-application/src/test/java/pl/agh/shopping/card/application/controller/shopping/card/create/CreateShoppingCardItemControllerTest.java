package pl.agh.shopping.card.application.controller.shopping.card.create;

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
import pl.agh.shopping.card.application.config.TestUtils;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.application.rest.MicroService;
import pl.agh.shopping.card.application.rest.RestClient;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;
import pl.agh.shopping.card.mysql.repository.ShoppingCardItemRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@Sql({"classpath:schema-shopping.sql", "classpath:data-shopping.sql"})
public class CreateShoppingCardItemControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ShoppingCardItemRepository shoppingCardItemRepository;
    @MockBean
    private RestClient restClient;

    @Test
    public void successTest() throws Exception {


        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 3)
                .put("title", "Lalka")
                .put("available", true)
                .put("price", 0.99)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/3", Map.class)).thenReturn(book);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(3L, 1);

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.book.id").value("3"))
                .andExpect(jsonPath("quantity").value("1"))
                .andExpect(jsonPath("actualPrice").value(0.99F));

        List<ShoppingCardItem> all = shoppingCardItemRepository.findAll();
        ShoppingCardItem shoppingCardItem = all.get(all.size() - 1);

        assertNotNull(shoppingCardItem);
        assertEquals(3L, shoppingCardItem.getBookId(), 0.01);
        assertEquals(shoppingCardItem.getQuantity(), Integer.valueOf(1));

        shoppingCardItemRepository.delete(shoppingCardItem);
    }

    @Test
    public void sameBookSuccessTest() throws Exception {


        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .put("price", 0.99)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 1);

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.book.id").value("1"))
                .andExpect(jsonPath("quantity").value("4"))
                .andExpect(jsonPath("actualPrice").value(0.99F));

        Optional<ShoppingCardItem> optionalShoppingCardItem = shoppingCardItemRepository.findById(1L);

        assertTrue(optionalShoppingCardItem.isPresent());
        ShoppingCardItem shoppingCardItem = optionalShoppingCardItem.get();
        assertNotNull(shoppingCardItem);
        assertEquals(1L, shoppingCardItem.getBookId(), 0.01);
        assertEquals(shoppingCardItem.getQuantity(), Integer.valueOf(4));

        shoppingCardItemRepository.delete(shoppingCardItem);
    }

    @Test
    public void checkIfActualPriceWasAdded() throws Exception {


        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 3)
                .put("title", "Lalka")
                .put("available", true)
                .put("price", 0.99)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/3", Map.class)).thenReturn(book);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(3L, 10);

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.book.id").value("3"))
                .andExpect(jsonPath("actualPrice").value(0.99F));

        List<ShoppingCardItem> all = shoppingCardItemRepository.findAll();
        ShoppingCardItem shoppingCardItem = all.get(all.size() - 1);

        assertNotNull(shoppingCardItem);
        assertEquals(3L, shoppingCardItem.getBookId(), 0.01);
        assertEquals(shoppingCardItem.getQuantity(), Integer.valueOf(10));

        shoppingCardItemRepository.delete(shoppingCardItem);
    }

    @Test
    public void bookNotAvailableFailedTest() throws Exception {


        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", false)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 1);

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("book with id=[1] -> not available"));
    }

    @Test
    public void bookIsNullFailedTest() throws Exception {


        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(null);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 1);

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("book with id=[1] -> not found"));
    }

    @Test
    public void noQuantityFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = ShoppingCardItemRequestDTO.builder().bookId(1L).build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("quantity=[null] -> must not be null"));
    }


    @Test
    public void noBookIdFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = ShoppingCardItemRequestDTO.builder().quantity(1).build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/1/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("bookId=[null] -> must not be null"));
    }


    @Test
    public void noShoppingCardFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 1);

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards/10/items/").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400));
    }
}
