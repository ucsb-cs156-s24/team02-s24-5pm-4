package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonMenuItems;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonMenuItemRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.pro.packaged.iD;
import liquibase.pro.packaged.id;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;


@Tag(name = "UCSBDiningCommonMenuItems")
@RequestMapping("/api/ucsbdiningcommonmenuitems")
@RestController
@Slf4j

public class UCSBDiningCommonMenuItemsController extends ApiController {

    @Autowired
    UCSBDiningCommonMenuItemRepository ucsbDiningCommonMenuItemRepository;

    @Operation(summary= "List all ucsb dining common menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonMenuItems> allUCSBDiningCommonsMenuItems() {
        Iterable<UCSBDiningCommonMenuItems> items  = ucsbDiningCommonMenuItemRepository.findAll();
        return items;
    }
    @Operation(summary= "Create a new commons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonMenuItems postCommonMenuItems (
        @Parameter(name="diningCommonsCode") @RequestParam String code,
        @Parameter(name="name") @RequestParam String name,
        @Parameter(name="station") @RequestParam String station
        ){
            UCSBDiningCommonMenuItems items = new UCSBDiningCommonMenuItems();
            items.setDiningCommonsCode(code);
            items.setName(name);
            items.setStation(station);
            UCSBDiningCommonMenuItems saveditems = ucsbDiningCommonMenuItemRepository.save(items);
            return saveditems;
        }

        @Operation(summary= "Get a single item")
        @PreAuthorize("hasRole('ROLE_USER')")
        @GetMapping("")
        public UCSBDiningCommonMenuItems getById(
                @Parameter(name="id") @RequestParam long id) {
                    UCSBDiningCommonMenuItems item = ucsbDiningCommonMenuItemRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonMenuItems.class, id));
    
            return item;
        }
}