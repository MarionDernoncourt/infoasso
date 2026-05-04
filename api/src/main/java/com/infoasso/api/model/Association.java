package com.infoasso.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "associations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rnaNumber;

    @Column(nullable = false)
    private String officialName;

    @Column(nullable = false)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    private String website;

    private String streetAddress;
    private String zipCode;
    private String city;

    private boolean isPublished = false; // Visibilité sur le site
    private boolean isVerified = false;  // La fameuse pastille bleue

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}