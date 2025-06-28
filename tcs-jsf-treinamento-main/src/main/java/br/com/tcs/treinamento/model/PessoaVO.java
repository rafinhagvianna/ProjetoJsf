package br.com.tcs.treinamento.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PessoaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private Integer idade;
    private String email;
    private Date data;
    private String tipoDocumento;
    private String numeroCPF;
    private String numeroCNPJ;
    private String motivoManutencao;
    private Date dataManutencao;
    private Boolean ativo = true;

    // novos campos
    private Boolean fazFaculdade = false;
    private String faculdade;
    private Date dataInicio;
    private Integer periodo;
    private String curso;

    public PessoaVO() {
    }

    public PessoaVO(Long id, String nome, Integer idade, String email, Date data, String tipoDocumento,
                    String numeroCNPJ, String numeroCPF, Date dataManutencao, String motivoManutencao,
                    Boolean ativo, Boolean fazFaculdade, String faculdade, Date dataInicio,
                    Integer periodo, String curso) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.email = email;
        this.data = data;
        this.tipoDocumento = tipoDocumento;
        this.numeroCNPJ = numeroCNPJ;
        this.numeroCPF = numeroCPF;
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

    // novos getters e setters
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

        PessoaVO pessoaVO = (PessoaVO) o;
        return Objects.equals(id, pessoaVO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
