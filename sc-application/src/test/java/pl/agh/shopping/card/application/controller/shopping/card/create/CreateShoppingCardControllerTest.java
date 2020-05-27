package pl.agh.shopping.card.application.controller.shopping.card.create;

import org.junit.Test;
import org.junit.runner.RunWith;
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
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.application.rest.url.URLProvider;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@Sql({"classpath:schema-shopping.sql", "classpath:data-shopping.sql"})
public class CreateShoppingCardControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ShoppingCardRepository shoppingCardRepository;
    @MockBean
    private URLProvider urlProvider;

    @Test
    public void successTest() throws Exception {

        ShoppingCardRequestDTO shoppingCardRequestDTO = ShoppingCardRequestDTO.builder().username("user123").build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(201))
                .andExpect(jsonPath("username").value("user123"));

        ShoppingCard shoppingCard = shoppingCardRepository.findById(3l).orElse(null);

        assertNotNull(shoppingCard);
        assertEquals("user123", shoppingCard.getUsername());
        assertEquals(LocalDate.now(), shoppingCard.getCreateDate());

        shoppingCardRepository.delete(shoppingCard);
    }

    @Test
    public void noUsernameSuccessTest() throws Exception {
        ShoppingCardRequestDTO shoppingCardRequestDTO = ShoppingCardRequestDTO.builder().build();

        String requestJson = TestUtils.mapObjectToStringJson(shoppingCardRequestDTO);

        mvc.perform(MockMvcRequestBuilders.post("/shoppingCards").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().is(201))
                .andExpect(jsonPath("id").value("3"))
                .andExpect(jsonPath("username").doesNotExist())
                .andExpect(jsonPath("createDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("items.count").value(0))
                .andExpect(jsonPath("items.list").isEmpty());
    }
}
