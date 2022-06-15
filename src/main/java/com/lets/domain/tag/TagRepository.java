package com.lets.domain.tag;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    @Query("select t from Tag t where t.name in (:tags)")
    List<Tag> findAllByName(@Param("tags") List<String> tags);
}
