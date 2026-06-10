package br.edu.ifps.emel_condominios_api.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "visita_id")
    private long id;
    private String tipo;
    private LocalDate data;    
    @ManyToMany
    @JoinTable(
        name = "visitante_visita",
        joinColumns = @JoinColumn(name = "visita_id"),
        inverseJoinColumns = @JoinColumn(name = "visitante_id")
    )
    private List<Visitante> visitantes;
    @ManyToOne
    @JoinColumn(name = "morador_id")
    private Morador morador;
}
