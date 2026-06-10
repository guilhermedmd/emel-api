package br.edu.ifps.emel_condominios_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Visitante {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "visitante_id")
    private long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
}
