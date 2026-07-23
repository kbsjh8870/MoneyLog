package org.example.backend.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.global.entity.BaseTimeEntity;
import org.example.backend.user.entity.User;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false,length=30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @Column(length = 10, columnDefinition = "VARCHAR(10) CHARACTER SET utf8mb4")
    private String emoji;

    @Builder
    public Category(User user, String name, CategoryType type, String emoji){
        this.user = user;
        this.name = name;
        this.type = type;
        this.emoji = emoji;
    }

    public void update(String categoryName, String emoji, CategoryType type){
        this.name = categoryName;
        this.emoji = emoji;
        this.type = type;
    }
}
