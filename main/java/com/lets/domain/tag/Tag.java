package com.lets.domain.tag;

import com.lets.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "name"
        })

})
@Entity
public class Tag extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    private Tag(String name){
        this.name = name;
    }

    public static Tag createTag(String name){
        Tag tag = new Tag(name);
        return tag;
    }
}
