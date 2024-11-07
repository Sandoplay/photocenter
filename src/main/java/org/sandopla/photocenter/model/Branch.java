package org.sandopla.photocenter.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_branch_id")
    private Branch parentBranch;

    public enum BranchType {
        MAIN_OFFICE, BRANCH, KIOSK
    }

    @Override
    public String toString() {
        return "Branch(id=" + id + ", name=" + name + ", address=" + address + ")";
    }
}