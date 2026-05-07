package com.servicebackend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.pgvector.PGvector;

@Getter
@Setter
@ToString
@Entity
@Table
public class Chunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChunk;

    private String content;

    @Column(
            name = "embedding",
            columnDefinition = "vector(384)"
    )
    @JdbcTypeCode(SqlTypes.OTHER)
    private PGvector embedding;

    @ManyToOne
    @JoinColumn(name = "idFileUploaded")
    private FileUploaded fileUploaded;

}
