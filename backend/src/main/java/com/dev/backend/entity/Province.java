package com.dev.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "provinces")
@Entity
public class Province {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "slug")
    private String slug;

    @Column(name = "name")
    private String name;
}
