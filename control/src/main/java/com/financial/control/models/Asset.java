package com.financial.control.models;


import com.financial.control.models.enums.AssetType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

}
