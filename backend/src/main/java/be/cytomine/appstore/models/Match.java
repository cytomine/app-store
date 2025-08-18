package be.cytomine.appstore.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import be.cytomine.appstore.models.task.Parameter;

@Entity
@Table(name = "match")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Match extends BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matching_id")
    private Parameter matching;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matched_id")
    private Parameter matched;

    private CheckTime checkTime;

    public Match(
        Parameter matching,
        Parameter matched,
        CheckTime checkTime) {

        this.matching = matching;
        this.matched = matched;
        this.checkTime = checkTime;
    }
}
