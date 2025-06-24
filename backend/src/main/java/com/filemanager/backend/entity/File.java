package com.filemanager.backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing metadata of a file stored on disk.
 * Linked to a specific user and (optionally) a folder.
 */
@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"owner", "folder"}) // Prevents recursive logging
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // Fixes serialization of lazy-loaded entities
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Name of the file (without path).
     */
    @Column(nullable = false)
    private String name;

    /**
     * Absolute or relative path to the file on disk.
     */
    @Column(nullable = false)
    private String path;

    /**
     * File size in bytes.
     */
    @Column(nullable = false)
    private Long size;

    /**
     * Owner of the file. Every file is tied to a user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Optional folder where the file is located.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    /**
     * Access level of the file (PRIVATE or PUBLIC).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    /**
     * Timestamp when the file was first created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Auto-set creation and update time before saving.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    /**
     * Auto-update timestamp before database update.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Enum for access control.
     */
    public enum Visibility {
        PRIVATE, PUBLIC
    }
}
