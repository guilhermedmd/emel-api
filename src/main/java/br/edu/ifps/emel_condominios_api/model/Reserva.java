package br.edu.ifps.emel_condominios_api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "morador_id")
    private Morador morador;

    @ManyToOne
    @JoinColumn(name = "area_lazer_id")
    private AreaLazer areaLazer;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_hora_inicio")
    private LocalDateTime dataHoraInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    @Column(name = "tipo_evento")
    private String tipoEvento;

    @Column(name = "qtd_convidados")
    private Integer qtdConvidados;

    private String status;
    private String observacoes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;

    public Reserva(Long id) {
        this.id = id;
    }

    public Reserva(Morador morador, AreaLazer areaLazer, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String tipoEvento, Integer qtdConvidados, String status, String observacoes, LocalDateTime dataSolicitacao) {
        this.morador = morador;
        this.areaLazer = areaLazer;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.tipoEvento = tipoEvento;
        this.qtdConvidados = qtdConvidados;
        this.status = status;
        this.observacoes = observacoes;
        this.dataSolicitacao = dataSolicitacao;
    }

    public Reserva(Morador morador, AreaLazer areaLazer, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String tipoEvento, Integer qtdConvidados, String observacoes) {
        this.morador = morador;
        this.areaLazer = areaLazer;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.tipoEvento = tipoEvento;
        this.qtdConvidados = qtdConvidados;
        this.observacoes = observacoes;
    }

    public Long getMoradorId() {
        return this.morador != null ? this.morador.getId() : null;
    }

    public void setMoradorId(Long moradorId) {
        if (moradorId != null) {
            if (this.morador == null) {
                this.morador = new Morador();
            }
            this.morador.setId(moradorId);
        }
    }

    public Long getAreaLazerId() {
        return this.areaLazer != null ? this.areaLazer.getId() : null;
    }

    public void setAreaLazerId(Long areaLazerId) {
        if (areaLazerId != null) {
            if (this.areaLazer == null) {
                this.areaLazer = new AreaLazer();
            }
            this.areaLazer.setId(areaLazerId);
        }
    }
}
