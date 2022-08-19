package com.lets.web.controller;


import com.lets.domain.tag.Tag;
import com.lets.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    /**
     * 전체 태그 검색
     */
    @GetMapping
    public List<String> findAll(){
        List<Tag> findTags = tagService.findAll();
        return findTags.stream().map(tag -> tag.getName()).collect(Collectors.toList());
    }
}
