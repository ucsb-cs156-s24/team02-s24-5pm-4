package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommomMenuItemRepository;

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

@WebMvcTest(controllers = UCSBDatesController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonMenuItemsControllerTests extends ControllerTestCase {

    @MockBean
    UCSBDiningCommonMenuItemRepository ucsbDiningCommonMenuItemRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/ucsbdates/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonmenuitem/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonmenuitem/all"))
                            .andExpect(status().is(200)); // logged
    }

    public void logged_in_users_can_get_all_menu_items() throws Exception {
        UCSBDiningCommonMenuItems MenuItem1 = UCSBDiningCommonMenuItems.builder()
                                .diningCommonsCode("De La Guerra")
                                .name("Lentil and Brown Rice Soup (vgn)")
                                .station("Blue Plate Special")
                                .build();

        UCSBDiningCommonMenuItems MenuItem2 = UCSBDiningCommonMenuItems.builder()
                                .diningCommonsCode("Carillo")
                                .name("Oatmeal (vgn)")
                                .station("Grill (Cafe)")
                                .build();
        ArrayList<UCSBDiningCommonMenuItems> expectedMenuItems = new ArrayList<>();

        expectedMenuItems.addAll(Arrays.asList(MenuItem1,MenuItem2));

        when(ucsbDiningCommonMenuItemRepository.findAll()).thenReturn(expectedMenuItems);

        //act
        MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonmenuitem/all")).andExpect(status().isOk()).andReturn();
                                
        verify(ucsbDiningCommonMenuItemRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCommons);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
        



    }

    //test for posts
    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                            .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                            .andExpect(status().is(403)); // need ADMIN role to post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_UCSBDiningCommonMenuItem() throws Exception {

        UCSBDiningCommonsMenuItems menuItem1 = menuItem1.builder()
                                .diningCommonsCode("Portola")
                                .name("Sage & Sweet Potato Soup (v)")
                                .station("Greens & Grains")
                                .build();
        
        when(ucsbDiningCommonMenuItemRepository.save(eq(menuItem1))).thenReturn(menuItem1);

        //act
        MvcResult response = mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post")
        .param("diningCommonsCode", "Portola")
        .param("name", "Sage & Sweet Potato Soup (v)")
        .param("station", "Greens & Grains")
        .with(csrf())).andExpect(status().isOk()).andReturn();

        //assert
        verify(ucsbDiningCommonMenuItemRepository,times(1)).save(menuItem1);
        String expectedJson = mapper.writeValueAsString(menuItem1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson,responseString);

    }



}