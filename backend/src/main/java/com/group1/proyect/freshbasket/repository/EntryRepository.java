package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Entry;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntryRepository extends GenericRepository<Entry, Long> {

    List<Entry> findByActiveTrue();
}