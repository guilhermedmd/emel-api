package br.edu.ifps.emel_condominios_api.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Notificacao {
    @Id
    @Column(name = "notificacao_id")
    private long id;
    private String titulo, descricao, tipo;
    private LocalDate data;
    @ManyToOne
    @JoinColumn(name = "morador_id")
    private Morador morador;
}
