package pl.agh.shopping.card.application.controller.shopping.card.update;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.agh.shopping.card.application.config.TestUtils;
import pl.agh.shopping.card.application.config.WithCustomUser;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.application.rest.MicroService;
import pl.agh.shopping.card.application.rest.RestClient;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;
import pl.agh.shopping.card.mysql.repository.ShoppingCardItemRepository;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.agh.shopping.card.application.config.TestUtils.mapObjectToStringJson;


@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"classpath:schema-shopping.sql", "classpath:data-shopping.sql"})
public class UpdateShoppingCardItemControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ShoppingCardItemRepository shoppingCardItemRepository;
    @Autowired
    private ShoppingCardRepository shoppingCardRepository;

    @MockBean
    private RestClient restClient;

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Test
    public void notLoggedUserCanAddItemToCardWithoutOwner() throws Exception {
        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .put("price", 21.03)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 11);
        ShoppingCard shoppingCard = new ShoppingCard();
        shoppingCard.setCreateDate(LocalDate.now());
        shoppingCardRepository.save(shoppingCard);

        String requestJson = mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/3/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(200))
                .andExpect(jsonPath("quantity").value("11"));

        ShoppingCardItem shoppingCardItemAfter = shoppingCardItemRepository.findById(1L).orElse(null);
        assertNotNull(shoppingCardItemAfter);
        assertEquals(1L, shoppingCardItemAfter.getId(), 0.01);
        assertEquals(shoppingCardItemAfter.getQuantity(), Integer.valueOf(11));
    }

    @Test
    @WithCustomUser(roles = "ADMIN")
    public void adminCanAddItemToAnyCard() throws Exception {

        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .put("price", 21.03)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 11);

        String requestJson = mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(200))
                .andExpect(jsonPath("quantity").value("11"));

        ShoppingCardItem shoppingCardItemAfter = shoppingCardItemRepository.findById(1L).orElse(null);
        assertNotNull(shoppingCardItemAfter);
        assertEquals(1L, shoppingCardItemAfter.getId(), 0.01);
        assertEquals(shoppingCardItemAfter.getQuantity(), Integer.valueOf(11));
    }

    @Test
    @WithCustomUser("user1")
    public void checkForUpdateInActualPrice() throws Exception {
        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .put("price", 0.99)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);

        ShoppingCardItem shoppingCardItemBefore = shoppingCardItemRepository.findById(1L).orElseThrow(null);

        // initial value set in sql script. Keep it in sync fo #1 element
        assertEquals(shoppingCardItemBefore.getActualPrice(), 1.234, 0.001);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 11);

        String requestJson = mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(200))
                .andExpect(jsonPath("quantity").value("11"))
                .andExpect(jsonPath("actualPrice").value(0.99));

        ShoppingCardItem shoppingCardItemAfter = shoppingCardItemRepository.findById(1L).orElse(null);
        assertNotNull(shoppingCardItemAfter);
        assertEquals(0.99, shoppingCardItemAfter.getActualPrice(), 0.001);
        assertEquals(1L, shoppingCardItemAfter.getId(), 0.01);
        assertEquals(Integer.valueOf(11), shoppingCardItemAfter.getQuantity());
    }

    @Test
    @WithCustomUser("user1")
    public void loggedInSuccessUpdateTest() throws Exception {

        Map<String, Object> book = ImmutableMap.<String, Object>builder()
                .put("id", 1)
                .put("title", "Lalka")
                .put("available", true)
                .put("price", 23.99)
                .build();

        Mockito.when(restClient.get(MicroService.PRODUCT_MS, "/books/1", Map.class)).thenReturn(book);

        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 11);

        String requestJson = mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(200))
                .andExpect(jsonPath("quantity").value("11"));

        ShoppingCardItem shoppingCardItemAfter = shoppingCardItemRepository.findById(1L).orElse(null);
        assertNotNull(shoppingCardItemAfter);
        assertEquals(1L, shoppingCardItemAfter.getId(), 0.01);
        assertEquals(shoppingCardItemAfter.getQuantity(), Integer.valueOf(11));
    }

    @Test
    @WithCustomUser("anotherUser")
    public void otherSuccessUpdateTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 11);

        String requestJson = mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(403));
    }

    @Test
    public void noQuantityFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = ShoppingCardItemRequestDTO.builder().bookId(1L).build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("quantity=[null] -> must not be null"));
    }

    @Test
    @WithCustomUser(roles = "ADMIN")
    public void adminNoQuantityFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = ShoppingCardItemRequestDTO.builder().bookId(1L).build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("quantity=[null] -> must not be null"));
    }

    @Test
    @WithCustomUser("user1")
    public void loggedInNoQuantityFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = ShoppingCardItemRequestDTO.builder().bookId(1L).build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("quantity=[null] -> must not be null"));
    }

    @Test
    @WithCustomUser("anotherUser")
    public void otherNoQuantityFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = ShoppingCardItemRequestDTO.builder().bookId(1L).build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("quantity=[null] -> must not be null"));
    }

    @Test
    public void noBookIdFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = ShoppingCardItemRequestDTO.builder().quantity(1).build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400))
                .andExpect(jsonPath("error").value("bookId=[null] -> must not be null"));
    }

    @Test
    public void noShoppingCardFailedTest() throws Exception {
        ShoppingCardItemRequestDTO shoppingCardItemRequestDTO = new ShoppingCardItemRequestDTO(1L, 1);

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardItemRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/10/items/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(400));
    }

}
