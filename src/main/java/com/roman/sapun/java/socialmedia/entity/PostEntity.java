package com.roman.sapun.java.socialmedia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(name = "creation_time")
    private Timestamp creationTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "post_tags",
            joinColumns = {@JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id")},
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false, referencedColumnName = "id"))
    private Set<TagEntity> tags;
}
