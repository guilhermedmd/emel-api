package br.edu.ifps.emel_condominios_api.model;

import java.time.LocalDate;

import org.hibernate.annotations.GeneratedColumn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "notificacao_id")
    private Long id;
    private String titulo, descricao, tipo;
    private LocalDate data;
    @ManyToOne
    @JoinColumn(name = "morador_id")
    private Morador morador;
    
    public Notificacao(String titulo, String descricao, String tipo, LocalDate data, Morador morador) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.data = data;
        this.morador = morador;
    }

    
}
