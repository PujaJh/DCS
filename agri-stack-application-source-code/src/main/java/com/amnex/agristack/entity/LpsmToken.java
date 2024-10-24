package com.amnex.agristack.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class LpsmToken extends BaseEntity {
	


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Long lpsmId;

    @Column(columnDefinition = "TEXT")
    private String token;
    
}
