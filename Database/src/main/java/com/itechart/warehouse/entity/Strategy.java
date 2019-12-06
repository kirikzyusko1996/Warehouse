package com.itechart.warehouse.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Created by Lenovo on 14.10.2017.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table (name = "strategy")
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_strategy")
    private Long idStrategy;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "description", length = 50)
    private String description;
}
