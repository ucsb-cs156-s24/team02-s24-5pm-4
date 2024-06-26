package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)

public class MenuItemReviewsControllerTests extends ControllerTestCase{
    
        @MockBean
        MenuItemReviewRepository menuItemReviewRepository;
        
        @MockBean
        UserRepository userRepository;
    
        // Tests for GET /api/menuitemreview/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }
    
        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().is(200)); // logged
        }
    
        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_menuitemreview() throws Exception {
    
                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
    
                MenuItemReview menuItemReview1 = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("test@gmail.com")
                                .stars(5)
                                .dateReviewed(ldt1)
                                .comments("good")
                                .build();
    
                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");
    
                MenuItemReview menuItemReview2 = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("test2@gmail.com")
                                .stars(2)
                                .dateReviewed(ldt2)
                                .comments("bad")
                                .build();
    
                ArrayList<MenuItemReview> expectedReviews = new ArrayList<>();
                expectedReviews.addAll(Arrays.asList(menuItemReview1, menuItemReview2));
    
                when(menuItemReviewRepository.findAll()).thenReturn(expectedReviews);
    
                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().isOk()).andReturn();
    
                // assert
    
                verify(menuItemReviewRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedReviews);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
    
        // Tests for POST /api/menuitemreview/post...
    
        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/menuitemreview/post"))
                                .andExpect(status().is(403));
        }
    
        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/menuitemreview/post"))
                                .andExpect(status().is(403)); // only admins can post
        }
    
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_menuitemreview() throws Exception {
                // arrange
    
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
    
                MenuItemReview menuItemReview1 = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("test@email.com")
                                .stars(5)
                                .dateReviewed(ldt1)
                                .comments("good")
                                .build();
    
                when(menuItemReviewRepository.save(eq(menuItemReview1))).thenReturn(menuItemReview1);
    
                // act
                MvcResult response = mockMvc.perform(
                                post("/api/menuitemreview/post?itemId=1&reviewerEmail=test@email.com&stars=5&dateReviewed=2022-01-03T00:00:00&comments=good")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();
    
                // assert
                verify(menuItemReviewRepository, times(1)).save(menuItemReview1);
                String expectedJson = mapper.writeValueAsString(menuItemReview1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
    
        // Tests for GET /api/menuitemreview?id=...
    
            @Test
            public void logged_out_users_cannot_get_by_id() throws Exception {
                    mockMvc.perform(get("/api/menuitemreview?id=3"))
                                    .andExpect(status().is(403)); // logged out users can't get by id
            }
    
            @WithMockUser(roles = { "USER" })
            @Test
            public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
    
                    // arrange
                    LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");
    
                    MenuItemReview menuItemReview = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("test@gmail.com")
                                .stars(5)
                                .dateReviewed(ldt)
                                .comments("good")
                                .build();
    
                    when(menuItemReviewRepository.findById(eq(1L))).thenReturn(Optional.of(menuItemReview));
    
                    // act
                    MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=1"))
                                    .andExpect(status().isOk()).andReturn();
    
                    // assert
    
                    verify(menuItemReviewRepository, times(1)).findById(eq(1L));
                    String expectedJson = mapper.writeValueAsString(menuItemReview);
                    String responseString = response.getResponse().getContentAsString();
                    assertEquals(expectedJson, responseString);
            }
    
            @WithMockUser(roles = { "USER" })
            @Test
            public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
    
                    // arrange
    
                    when(menuItemReviewRepository.findById(eq(1L))).thenReturn(Optional.empty());
    
                    // act
                    MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=1"))
                                    .andExpect(status().isNotFound()).andReturn();
    
                    // assert
    
                    verify(menuItemReviewRepository, times(1)).findById(eq(1L));
                    Map<String, Object> json = responseToJson(response);
                    assertEquals("EntityNotFoundException", json.get("type"));
                    assertEquals("MenuItemReview with id 1 not found", json.get("message"));
            }


        // Tests for DELETE /api/ucsbdates?id=... 

            @WithMockUser(roles = { "ADMIN", "USER" })
            @Test
            public void admin_can_delete_a_review() throws Exception {
                    // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
    
                MenuItemReview menuItemReview1 = MenuItemReview.builder()
                                .itemId(1)
                                .reviewerEmail("test@gmail.com")
                                .stars(1)
                                .dateReviewed(ldt1)
                                .comments("test")
                                .build();
    
                    when(menuItemReviewRepository.findById(eq(2L))).thenReturn(Optional.of(menuItemReview1));
    
                    // act
                    MvcResult response = mockMvc.perform(
                                    delete("/api/menuitemreview?id=2")
                                                    .with(csrf()))
                                    .andExpect(status().isOk()).andReturn();
    
                    // assert
                    verify(menuItemReviewRepository, times(1)).findById(2L);
                    verify(menuItemReviewRepository, times(1)).delete(any());
    
                    Map<String, Object> json = responseToJson(response);
                    assertEquals("MenuItemReview with id 2 deleted", json.get("message"));
            }
    
            @WithMockUser(roles = { "ADMIN", "USER" })
            @Test
            public void admin_tries_to_delete_non_existant_review_and_gets_right_error_message()
                            throws Exception {
                    // arrange
    
                    when(menuItemReviewRepository.findById(eq(32L))).thenReturn(Optional.empty());
    
                    // act
                    MvcResult response = mockMvc.perform(
                                    delete("/api/menuitemreview?id=32")
                                                    .with(csrf()))
                                    .andExpect(status().isNotFound()).andReturn();
    
                    // assert
                    verify(menuItemReviewRepository, times(1)).findById(32L);
                    Map<String, Object> json = responseToJson(response);
                    assertEquals("MenuItemReview with id 32 not found", json.get("message"));
            }
    
        // // Tests for PUT /api/menuitemreview?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_review() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

                MenuItemReview menuItemReviewOrig = MenuItemReview.builder()
                            .itemId(1)
                            .reviewerEmail("test@email.com")
                            .stars(1)
                            .dateReviewed(ldt1)
                            .comments("original")
                            .build();

                MenuItemReview menuItemReviewChanged = MenuItemReview.builder()
                            .itemId(2)
                            .reviewerEmail("test2@gmailcom")
                            .stars(2)
                            .dateReviewed(ldt2)
                            .comments("changed")
                            .build();

                String requestBody = mapper.writeValueAsString(menuItemReviewChanged);

                when(menuItemReviewRepository.findById(eq(5L))).thenReturn(Optional.of(menuItemReviewOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreview?id=5")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(5L);
                verify(menuItemReviewRepository, times(1)).save(menuItemReviewChanged); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_review_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime date1 = LocalDateTime.parse("2022-01-03T00:00:00");

                MenuItemReview editedReview = MenuItemReview.builder()
                            .itemId(3)
                            .reviewerEmail("test@gmail.com")
                            .stars(3)
                            .dateReviewed(date1)
                            .comments("test")
                            .build();

                String requestBody = mapper.writeValueAsString(editedReview);

                when(menuItemReviewRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreview?id=7")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(7L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 7 not found", json.get("message"));

        }
}
