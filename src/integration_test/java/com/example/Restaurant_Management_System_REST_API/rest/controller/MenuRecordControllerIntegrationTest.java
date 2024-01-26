package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
class MenuRecordControllerIntegrationTest {

    private Set<String> ingredients;
    @Autowired
    private WebTestClient webTestClient;
    private String basicAuthHeader;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        ingredients = new HashSet<>();
        ingredients.add("water");
        ingredients.add("hops");
        ingredients.add("barley");

        //this block is for logging purpose
        String originalPassword = "lala";
        String encodedPassword = passwordEncoder.encode(originalPassword); //I use PasswordEncoder in SecurityConfig therefore it expects encoded password
        Authority authority = new Authority(null, "ROLE_OWNER");
        authorityRepository.save(authority);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        Customer owner = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, //here should be encoded password
                true, true, true, true, "lala@wp.pl",
                authorities);
        customerRepository.save(owner);

        basicAuthHeader = "Basic " + Base64.getEncoder()
                .encodeToString((owner.getEmailAddress() + ":" + originalPassword).getBytes());// here I need to provide a raw password
    }

    @Test
    public void create_ShouldAddMenuRecordToDatabaseAndReturnDTOResponse_WhenDTORequestIsGiven() {

        MenuRecordDTORequest menuRecordDTORequest = new MenuRecordDTORequest(1L, ingredients, Category.BEVERAGE, true);
        //I don't have constructor for CatalogItem (DTORequest extends it) therefore I add the required fields below as follows:
        menuRecordDTORequest.setName("Beverage Name");
        menuRecordDTORequest.setDescription("Beverage Description");
        menuRecordDTORequest.setPrice(10.0);

        webTestClient.post()
                .uri("/api/menu/record/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(menuRecordDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuRecordDTOResponse.class)
                .consumeWith(response -> {
                            MenuRecordDTOResponse menuDTOResponse = response.getResponseBody();
                            assertNotNull(menuDTOResponse);
                            assertIterableEquals(menuRecordDTORequest.getIngredients(), menuDTOResponse.getIngredients());
                            assertEquals(menuRecordDTORequest.getCategory(), menuDTOResponse.getCategory());
                            assertEquals(menuRecordDTORequest.getIsAvailable(), menuDTOResponse.getIsAvailable());
                            assertEquals(menuRecordDTORequest.getName(), menuDTOResponse.getName());
                            assertEquals(menuRecordDTORequest.getDescription(), menuDTOResponse.getDescription());
                            assertEquals(menuRecordDTORequest.getPrice(), menuDTOResponse.getPrice());
                        }
                );
    }
}