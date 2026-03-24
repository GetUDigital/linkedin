package com.boinew.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_size")
    private CompanySize companySize;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    private CompanyType companyType;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(length = 255)
    private String website;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(length = 255)
    private String headquarters;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum CompanySize {
        SIZE_1_10("1-10"), SIZE_11_50("11-50"), SIZE_51_200("51-200"),
        SIZE_201_500("201-500"), SIZE_501_1000("501-1000"), SIZE_1000_PLUS("1000+");

        private final String label;
        CompanySize(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum CompanyType { Public, Private, NonProfit, Government }
}
