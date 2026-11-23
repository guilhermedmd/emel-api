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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Morador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "morador_id")
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String senha;

    public Morador(Long id) {
        this.id = id;
    }

    public Morador(String nome, String email, String telefone, String cpf, String senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.senha = senha;
    }

    public Morador(String nome, String email, String telefone, String cpf) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }
}
