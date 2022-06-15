package com.lets.domain.postTechStack;


import com.lets.domain.BaseTimeEntity;
import com.lets.domain.post.Post;
import com.lets.domain.tag.Tag;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PostTechStack extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_tech_stack_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private PostTechStack(Tag tag, Post post){

        this.tag = tag;
        this.post = post;
    }

    public static PostTechStack createPostTechStack(Tag tag, Post post){
        PostTechStack postTechStack = new PostTechStack(tag, post);

        return postTechStack;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
