package br.edu.ifps.emel_condominios_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sindico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sindico_id")
    private Long id;

    private String email;

    @JsonIgnore
    private String senha;

    public Sindico(Long id) {
        this.id = id;
    }

    public Sindico(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
