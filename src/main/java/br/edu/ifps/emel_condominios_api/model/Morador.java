package br.edu.ifps.emel_condominios_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Morador {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "morador_id")
    private long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;

}
