package br.com.tcs.treinamento.bean;

import br.com.tcs.treinamento.entity.Pessoa;
import br.com.tcs.treinamento.model.PessoaVO;
import br.com.tcs.treinamento.service.PessoaService;
import br.com.tcs.treinamento.service.impl.PessoaServiceImpl;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name="cadastroBean")
@ViewScoped
public class CadastroBean implements Serializable {
    private static final long serialVersionUID = 3450069247988201468L;

    private PessoaVO cadastrarPessoa = new PessoaVO();
    private String errorMessage;

    // Serviço
    private transient PessoaService pessoaService = new PessoaServiceImpl();

    // Novos campos faculdade
    private Boolean fazFaculdade = false;
    private String faculdade;
    private Date dataInicio;
    private Integer periodo;
    private String curso;
    private List<String> cursosDisponiveis;

    public CadastroBean() {
        cursosDisponiveis = List.of(
                "Engenharia de Software",
                "Ciência da Computação",
                "Administração",
                "Design Gráfico",
                "Direito"
        );
    }

    public void validarCampos() {
        List<String> erros = new ArrayList<>();

        // validações originais
        if (cadastrarPessoa.getNome()==null||cadastrarPessoa.getNome().trim().isEmpty())
            erros.add("Nome não informado.");
        if (cadastrarPessoa.getIdade()==null)
            erros.add("Idade não informada.");
        if (cadastrarPessoa.getEmail()==null||cadastrarPessoa.getEmail().trim().isEmpty())
            erros.add("E-mail não informado.");
        if (cadastrarPessoa.getData()==null)
            erros.add("Data de nascimento não informada.");
        if (cadastrarPessoa.getTipoDocumento()==null||cadastrarPessoa.getTipoDocumento().trim().isEmpty()) {
            erros.add("Tipo de documento não informado.");
        } else {
            if ("CPF".equals(cadastrarPessoa.getTipoDocumento())) {
                String cpf = cadastrarPessoa.getNumeroCPF();
                if (cpf==null||cpf.trim().length()<11)
                    erros.add("CPF não informado ou incompleto (11 dígitos).");
            } else {
                String cnpj = cadastrarPessoa.getNumeroCNPJ();
                if (cnpj==null||cnpj.trim().length()<14)
                    erros.add("CNPJ não informado ou incompleto (14 dígitos).");
            }
        }

        // validação faculdade
        if (Boolean.TRUE.equals(fazFaculdade)) {
            if (faculdade==null||faculdade.trim().isEmpty())
                erros.add("Nome da faculdade não informado.");
            if (dataInicio==null)
                erros.add("Data de início não informada.");
            if (periodo==null)
                erros.add("Período não informado.");
            else if (periodo<1||periodo>10)
                erros.add("Insira um número válido para o período (1-10).");
            if (curso==null||curso.trim().isEmpty())
                erros.add("Curso não selecionado.");
        }

        if (!erros.isEmpty()) {
            errorMessage = String.join("<br/>", erros);
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        } else {
            PrimeFaces.current().executeScript("PF('confirmDialog').show()");
        }
    }

    public void confirmar() {
        Pessoa p = new Pessoa();
        // dados básicos
        p.setNome(cadastrarPessoa.getNome());
        p.setIdade(cadastrarPessoa.getIdade());
        p.setEmail(cadastrarPessoa.getEmail());
        p.setData(cadastrarPessoa.getData());
        p.setTipoDocumento(cadastrarPessoa.getTipoDocumento());
        p.setNumeroCPF(cadastrarPessoa.getNumeroCPF());
        p.setNumeroCNPJ(cadastrarPessoa.getNumeroCNPJ());
        p.setAtivo(true);
        p.setDataManutencao(new Date());

        // dados faculdade
        p.setFazFaculdade(fazFaculdade);
        if (Boolean.TRUE.equals(fazFaculdade)) {
            p.setFaculdade(faculdade);
            p.setDataInicio(dataInicio);
            p.setPeriodo(periodo);
            p.setCurso(curso);
        }

        try {
            pessoaService.cadastrar(p);
            PrimeFaces.current().executeScript("PF('successDialog').show()");
        } catch (Exception e) {
            errorMessage = "Erro ao cadastrar pessoa: " + e.getMessage();
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    public void limpar() {
        cadastrarPessoa = new PessoaVO();
        errorMessage = null;
        fazFaculdade = false;
        faculdade = null;
        dataInicio = null;
        periodo = null;
        curso = null;
    }

    // readObject para re-inicializar serviço após deserialização
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.pessoaService = new PessoaServiceImpl();
    }

    // getters & setters

    public PessoaVO getCadastrarPessoa() { return cadastrarPessoa; }
    public void setCadastrarPessoa(PessoaVO cadastrarPessoa) { this.cadastrarPessoa = cadastrarPessoa; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

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

    public List<String> getCursosDisponiveis() { return cursosDisponiveis; }

    public PessoaService getPessoaService() { return pessoaService; }
    public void setPessoaService(PessoaService pessoaService) { this.pessoaService = pessoaService; }
}
