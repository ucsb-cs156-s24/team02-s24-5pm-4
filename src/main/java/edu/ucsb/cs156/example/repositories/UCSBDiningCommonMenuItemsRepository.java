package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonMenuItems;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UCSBDiningCommonMenuItemsRepository extends CrudRepository<UCSBDiningCommonMenuItems, Long> {
}