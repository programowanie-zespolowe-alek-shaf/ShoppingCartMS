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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.application.rest.MicroService;
import pl.agh.shopping.card.application.rest.RestClient;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.nio.charset.Charset;
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
@WithMockUser
@Sql({"classpath:schema-shopping.sql", "classpath:data-shopping.sql"})
public class UpdateShoppingCardControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ShoppingCardRepository shoppingCardRepository;
    @MockBean
    private RestClient restClient;

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Test
    public void successUpdateTest() throws Exception {

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

        ShoppingCard shoppingCardBefore = shoppingCardRepository.findById(1L).orElseThrow(null);

        ShoppingCardRequestDTO shoppingCardRequestDTO = new ShoppingCardRequestDTO();
        shoppingCardRequestDTO.setUsername("updatedUser1");

        String requestJson = mapObjectToStringJson(shoppingCardRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(200))
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("username").value("updatedUser1"))
                .andExpect(jsonPath("createDate").value(LocalDate.now().toString()));

        ShoppingCard shoppingCardAfter = shoppingCardRepository.findById(1L).orElse(null);
        assertNotNull(shoppingCardAfter);
        assertEquals(shoppingCardAfter.getId(), 1L, 0.01);
        assertEquals(shoppingCardAfter.getUsername(), "updatedUser1");
        assertEquals(shoppingCardAfter.getCreateDate(), LocalDate.now());

        shoppingCardRepository.save(shoppingCardBefore);
    }

    @Test
    public void noUsernameFailedTest() throws Exception {
        ShoppingCardRequestDTO shoppingCardRequestDTO = new ShoppingCardRequestDTO();

        String requestJson = mapObjectToStringJson(shoppingCardRequestDTO);

        mvc.perform(MockMvcRequestBuilders.put("/shoppingCards/2").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(200))
                .andExpect(jsonPath("id").value("2"))
                .andExpect(jsonPath("username").doesNotExist())
                .andExpect(jsonPath("createDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("items.count").value(0))
                .andExpect(jsonPath("items.list").isEmpty());
    }

}
