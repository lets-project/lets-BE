package com.lets.domain.tag;

import com.lets.config.QueryDslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
public class TagRepositoryTest {
    @Autowired
    TagRepository tagRepository;

    @AfterEach
    public void teardown(){
        tagRepository.deleteAllInBatch();
    }
    @DisplayName("태그 이름으로 태그 단건을 조회합니다.")
    @Test
    public void findByName() {
        //given
        Tag tag = Tag.createTag("spring");
        tagRepository.save(tag);

        //when
        Optional<Tag> findTag = tagRepository.findByName("spring");

        //then
        assertThat(findTag.get().getId()).isEqualTo(tag.getId());
    }
    @DisplayName("태그 이름으로 모든 태그를 조회합니다.")
    @Test
    public void findAllByName() {
        //given
        Tag tag1 = Tag.createTag("spring");
        tagRepository.save(tag1);

        Tag tag2 = Tag.createTag("jpa");
        tagRepository.save(tag2);

        //when
        List<Tag> result = tagRepository.findAllByName(Arrays.asList("spring", "jpa"));

        //then
        assertThat(result.size()).isEqualTo(2);
    }
}
