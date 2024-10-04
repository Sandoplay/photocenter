package org.sandopla.photocenter.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "workplace_count", nullable = false)
    private Integer workplaceCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BranchType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_branch_id")
    private Branch parentBranch;

    public enum BranchType {
        MAIN_OFFICE, BRANCH, KIOSK
    }
}