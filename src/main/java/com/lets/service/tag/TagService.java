package com.lets.service.tag;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public Tag findOne(String name){
        Tag findTag = tagRepository.findByName(name)
                .orElseThrow(()->new CustomException(ErrorCode.TAG_NOT_FOUND));
        return findTag;
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll(){
        return tagRepository.findAll();
    }

    public void save(Tag tag){
        tagRepository.save(tag);

    }
}
