package br.edu.ifps.emel_condominios_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "area_lazer")
public class AreaLazer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_lazer_id")
    private Long id;

    private String nome;
    private String descricao;

    @Column(name = "capacidade_maxima")
    private Integer capacidadeMaxima;

    private String status;

    public AreaLazer(Long id) {
        this.id = id;
    }

    public AreaLazer(String nome, String descricao, Integer capacidadeMaxima, String status) {
        this.nome = nome;
        this.descricao = descricao;
        this.capacidadeMaxima = capacidadeMaxima;
        this.status = status;
    }
}
