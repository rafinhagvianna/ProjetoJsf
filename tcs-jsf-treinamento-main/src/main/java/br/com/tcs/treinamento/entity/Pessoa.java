package br.com.tcs.treinamento.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "pessoa")
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Integer idade;

    @Column(nullable = false)
    private String email;

    @Temporal(TemporalType.DATE)
    private Date data;

    private String tipoDocumento;
    private String numeroCPF;
    private String numeroCNPJ;
    private String motivoManutencao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataManutencao;

    private Boolean ativo = true;

    // Novos campos para faculdade
    private Boolean fazFaculdade = false;
    private String faculdade;

    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    private Integer periodo;
    private String curso;

    public Pessoa() {
    }

    public Pessoa(String nome, Integer idade, String email, Date data, String tipoDocumento,
                  String numeroCPF, String numeroCNPJ, String motivoManutencao, Date dataManutencao,
                  Boolean ativo, Boolean fazFaculdade, String faculdade, Date dataInicio,
                  Integer periodo, String curso) {
        this.nome = nome;
        this.idade = idade;
        this.email = email;
        this.data = data;
        this.tipoDocumento = tipoDocumento;
        this.numeroCPF = numeroCPF;
        this.numeroCNPJ = numeroCNPJ;
        this.motivoManutencao = motivoManutencao;
        this.dataManutencao = dataManutencao;
        this.ativo = ativo;
        this.fazFaculdade = fazFaculdade;
        this.faculdade = faculdade;
        this.dataInicio = dataInicio;
        this.periodo = periodo;
        this.curso = curso;
    }

    // getters e setters existentes...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroCPF() { return numeroCPF; }
    public void setNumeroCPF(String numeroCPF) { this.numeroCPF = numeroCPF; }

    public String getNumeroCNPJ() { return numeroCNPJ; }
    public void setNumeroCNPJ(String numeroCNPJ) { this.numeroCNPJ = numeroCNPJ; }

    public String getMotivoManutencao() { return motivoManutencao; }
    public void setMotivoManutencao(String motivoManutencao) { this.motivoManutencao = motivoManutencao; }

    public Date getDataManutencao() { return dataManutencao; }
    public void setDataManutencao(Date dataManutencao) { this.dataManutencao = dataManutencao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    // getters e setters novos
    public Boolean getFazFaculdade() { return fazFaculdade; }
    public void setFazFaculdade(Boolean fazFaculdade) { this.fazFaculdade = fazFaculdade; }

    public String getFaculdade() { return faculdade; }
    public void setFaculdade(String faculdade) { this.faculdade = faculdade; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Integer getPeriodo() { return periodo; }
    public void setPeriodo(Integer periodo) { this.periodo = periodo; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return Objects.equals(id, pessoa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                // ... demais campos
                ", fazFaculdade=" + fazFaculdade +
                ", faculdade='" + faculdade + '\'' +
                ", dataInicio=" + dataInicio +
                ", periodo=" + periodo +
                ", curso='" + curso + '\'' +
                '}';
    }
}
