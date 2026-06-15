package br.edu.ifps.emel_condominios_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Sindico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sindico_id")
    Long id;
    String email;
    String senha;

    public Sindico() {
    }

    public Sindico(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    
}
