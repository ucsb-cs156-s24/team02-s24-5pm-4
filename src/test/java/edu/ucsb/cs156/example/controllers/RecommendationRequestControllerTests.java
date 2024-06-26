package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

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
@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase{
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequest/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequest/all"))
                .andExpect(status().is(200));
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void logged_in_user_can_get_all_recommendationrequests() throws Exception{
        LocalDateTime first = LocalDateTime.parse("2024-04-26T08:08:00");
        LocalDateTime second = LocalDateTime.parse("2024-04-27T08:08:00");
        LocalDateTime third = LocalDateTime.parse("2024-04-28T08:08:00");
        LocalDateTime fourth = LocalDateTime.parse("2024-04-29T08:08:00");

        RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                .requesterEmail("djensen2@outlook.com")
                .professorEmail("pconrad@ucsb.edu")
                .explanation("masters program")
                .dateRequested(first)
                .dateNeeded(second)
                .done(false)
                .build();

        RecommendationRequest recommendationRequest2 = RecommendationRequest.builder()
                .requesterEmail("djensen@ucsb.edu")
                .professorEmail("zmatni@ucsb.edu")
                .explanation("phd program")
                .dateRequested(third)
                .dateNeeded(fourth)
                .done(true)
                .build();

        ArrayList<RecommendationRequest> expected = new ArrayList<>();
        expected.addAll(Arrays.asList(recommendationRequest1,recommendationRequest2));

        when(recommendationRequestRepository.findAll()).thenReturn(expected);

        MvcResult response = mockMvc.perform(get("/api/recommendationrequest/all"))
                .andExpect(status().isOk()).andReturn();

        verify(recommendationRequestRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expected);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson,responseString);

    }

    //POST tests
    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequest/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequest/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void an_admin_can_post_new_recrequest() throws Exception {
        LocalDateTime first = LocalDateTime.parse("2024-04-26T08:08:00");
        LocalDateTime second = LocalDateTime.parse("2024-04-27T08:08:00");

        RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                .requesterEmail("djensen2@outlook.com")
                .professorEmail("pconrad@ucsb.edu")
                .explanation("masters program")
                .dateRequested(first)
                .dateNeeded(second)
                .done(true)
                .build();

        when(recommendationRequestRepository.save(eq(recommendationRequest1))).thenReturn(recommendationRequest1);

        MvcResult response = mockMvc.perform(
                post("/api/recommendationrequest/post?requesterEmail=djensen2@outlook.com&professorEmail=pconrad@ucsb.edu&explanation=masters program&dateRequested=2024-04-26T08:08:00&dateNeeded=2024-04-27T08:08:00&done=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(recommendationRequestRepository, times(1)).save(recommendationRequest1);
        String expectedJson = mapper.writeValueAsString(recommendationRequest1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    //GET tests

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/recommendationrequest?id=123"))
                .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER"})
    @Test
    public void can_get_by_id_when_logged_in_and_exists() throws Exception {
        LocalDateTime first = LocalDateTime.parse("2024-04-26T08:08:00");
        LocalDateTime second = LocalDateTime.parse("2024-04-27T08:08:00");

        RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                .requesterEmail("djensen2@outlook.com")
                .professorEmail("pconrad@ucsb.edu")
                .explanation("masters program")
                .dateRequested(first)
                .dateNeeded(second)
                .done(true)
                .build();
        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequest1));

        MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=7"))
                .andExpect(status().isOk()).andReturn();

        verify(recommendationRequestRepository,times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(recommendationRequest1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void test_that_can_get_when_doesnt_exist() throws Exception {
        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=7"))
                .andExpect(status().isNotFound()).andReturn();

        verify(recommendationRequestRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
    }

    //PUT tests
    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void admin_can_edit_existing() throws Exception {
        LocalDateTime first = LocalDateTime.parse("2024-04-26T08:08:00");
        LocalDateTime second = LocalDateTime.parse("2024-04-27T08:08:00");
        LocalDateTime third = LocalDateTime.parse("2024-04-28T08:08:00");
        LocalDateTime fourth = LocalDateTime.parse("2024-04-29T08:08:00");

        RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                .requesterEmail("djensen2@outlook.com")
                .professorEmail("pconrad@ucsb.edu")
                .explanation("masters program")
                .dateRequested(first)
                .dateNeeded(second)
                .done(false)
                .build();

        RecommendationRequest recommendationRequest2 = RecommendationRequest.builder()
                .requesterEmail("djensen@ucsb.edu")
                .professorEmail("zmatni@ucsb.edu")
                .explanation("phd program")
                .dateRequested(third)
                .dateNeeded(fourth)
                .done(true)
                .build();

        String requestBody = mapper.writeValueAsString(recommendationRequest2);

        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequest1));

        MvcResult response = mockMvc.perform(
                        put("/api/recommendationrequest?id=7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(requestBody)
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(recommendationRequestRepository, times(1)).findById(7L);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest2);
        String responseContent = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseContent);
    }

    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void admin_cant_edit_if_doesnt_exist() throws Exception{
        LocalDateTime first = LocalDateTime.parse("2024-04-26T08:08:00");
        LocalDateTime second = LocalDateTime.parse("2024-04-27T08:08:00");
        RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                .requesterEmail("djensen2@outlook.com")
                .professorEmail("pconrad@ucsb.edu")
                .explanation("masters program")
                .dateRequested(first)
                .dateNeeded(second)
                .done(false)
                .build();

        String requestBody = mapper.writeValueAsString(recommendationRequest1);

        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(
                        put("/api/recommendationrequest?id=7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(requestBody)
                                .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        verify(recommendationRequestRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
    }

    //DELETE tests
    @WithMockUser(roles = {"ADMIN","USER"})
    @Test
    public void admin_can_delete_RR() throws Exception{
        LocalDateTime first = LocalDateTime.parse("2024-04-26T08:08:00");
        LocalDateTime second = LocalDateTime.parse("2024-04-27T08:08:00");
        RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                .requesterEmail("djensen2@outlook.com")
                .professorEmail("pconrad@ucsb.edu")
                .explanation("masters program")
                .dateRequested(first)
                .dateNeeded(second)
                .done(false)
                .build();

        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequest1));

        MvcResult response = mockMvc.perform(
                        delete("/api/recommendationrequest?id=7")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(recommendationRequestRepository, times(1)).findById(eq(7L));
        verify(recommendationRequestRepository, times(1)).delete(any());

        Map<String, Object> json = responseToJson(response);
        assertEquals("Recommendation request with id 7 deleted", json.get("message"));
    }
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void error_when_delete_nonexistent_RR()
            throws Exception {
        // arrange

        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                        delete("/api/recommendationrequest?id=7")
                                .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findById(7L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
    }

    @Test
    public void rr_get_coverage(){
        LocalDateTime first = LocalDateTime.parse("2024-04-26T08:08:00");
        LocalDateTime second = LocalDateTime.parse("2024-04-27T08:08:00");
        LocalDateTime third = LocalDateTime.parse("2024-04-28T08:08:00");
        LocalDateTime fourth = LocalDateTime.parse("2024-04-29T08:08:00");

        RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                .requesterEmail("djensen2@outlook.com")
                .professorEmail("pconrad@ucsb.edu")
                .explanation("masters program")
                .dateRequested(first)
                .dateNeeded(second)
                .done(false)
                .build();

        RecommendationRequest recommendationRequest2 = RecommendationRequest.builder()
                .requesterEmail("djensen@ucsb.edu")
                .professorEmail("zmatni@ucsb.edu")
                .explanation("phd program")
                .dateRequested(third)
                .dateNeeded(fourth)
                .done(true)
                .build();

        assertEquals(recommendationRequest1.getDone(), false);
        assertEquals(recommendationRequest2.getDone(), true);
    }
}
