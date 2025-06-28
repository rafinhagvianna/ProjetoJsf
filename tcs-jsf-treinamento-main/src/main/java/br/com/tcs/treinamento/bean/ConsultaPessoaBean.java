package br.com.tcs.treinamento.bean;

import br.com.tcs.treinamento.entity.Pessoa;
import br.com.tcs.treinamento.service.PessoaService;
import br.com.tcs.treinamento.service.impl.PessoaServiceImpl;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ManagedBean(name="consultaPessoaBean")
@ViewScoped
public class ConsultaPessoaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Pessoa> pessoas;
    private Pessoa pessoaSelecionada;
    private String errorMessage;
    private Long pessoaId;
    private Boolean tpManutencao;
    private transient PessoaService pessoaService = new PessoaServiceImpl();

    @PostConstruct
    public void init() {
        Map<String,String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("pessoaId");
        if (idParam!=null && !idParam.trim().isEmpty()) {
            try {
                pessoaId = Long.valueOf(idParam);
                pessoaSelecionada = pessoaService.buscarPorId(pessoaId);
            } catch(NumberFormatException e) {
                errorMessage = "ID inválido.";
            }
        }
        String tpParam = params.get("tpManutencao");
        tpManutencao = tpParam!=null ? Boolean.valueOf(tpParam) : true;

        // só quem NÃO faz faculdade
        pessoas = pessoaService.listar()
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getFazFaculdade()))
                .collect(Collectors.toList());
    }

    public String prepararEdicao(Pessoa p) {
        this.pessoaSelecionada = p;
        return "alterar?faces-redirect=true&pessoaId="+p.getId()+"&tpManutencao=true";
    }

    public String prepararExclusao(Pessoa p) {
        this.pessoaSelecionada = p;
        return "excluir?faces-redirect=true&pessoaId="+p.getId()+"&tpManutencao=false";
    }

    public String atualizarConsulta() {
        pessoaService.atualizar(pessoaSelecionada);
        init();
        return "consultaPessoas?faces-redirect=true";
    }

    public void limparAlteracoes() {
        if (pessoaSelecionada != null) {
            pessoaSelecionada = pessoaService.buscarPorId(pessoaSelecionada.getId());
        }
    }

    private Pessoa mapPessoaEntity() {
        Pessoa p = new Pessoa();
        p.setId(pessoaSelecionada.getId());
        p.setNome(pessoaSelecionada.getNome());
        p.setIdade(pessoaSelecionada.getIdade());
        p.setEmail(pessoaSelecionada.getEmail());
        p.setData(pessoaSelecionada.getData());
        p.setTipoDocumento(pessoaSelecionada.getTipoDocumento());
        p.setNumeroCPF(pessoaSelecionada.getNumeroCPF());
        p.setNumeroCNPJ(pessoaSelecionada.getNumeroCNPJ());
        p.setDataManutencao(new Date());
        p.setAtivo(tpManutencao);
        p.setFazFaculdade(false);
        return p;
    }

    public void validarCampos() {
        List<String> erros = new java.util.ArrayList<>();
        if (pessoaSelecionada.getNome()==null||pessoaSelecionada.getNome().trim().isEmpty())
            erros.add("Nome não informado.");
        if (pessoaSelecionada.getIdade()==null)
            erros.add("Idade não informada.");
        if (pessoaSelecionada.getEmail()==null||pessoaSelecionada.getEmail().trim().isEmpty())
            erros.add("E-mail não informado.");
        if (pessoaSelecionada.getData()==null)
            erros.add("Data de nascimento não informada.");
        if (pessoaSelecionada.getTipoDocumento()==null||pessoaSelecionada.getTipoDocumento().trim().isEmpty())
            erros.add("Tipo de documento não informado.");
        else if ("CPF".equals(pessoaSelecionada.getTipoDocumento())) {
            if (pessoaSelecionada.getNumeroCPF()==null||pessoaSelecionada.getNumeroCPF().trim().length()<11)
                erros.add("CPF incompleto.");
        } else {
            if (pessoaSelecionada.getNumeroCNPJ()==null||pessoaSelecionada.getNumeroCNPJ().trim().length()<14)
                erros.add("CNPJ incompleto.");
        }
        if (!erros.isEmpty()) {
            errorMessage = String.join("<br/>", erros);
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        } else {
            PrimeFaces.current().executeScript("PF('confirmDialog').show()");
        }
    }

    public void confirmar() {
        try {
            pessoaService.atualizar(mapPessoaEntity());
            PrimeFaces.current().executeScript("PF('successDialog').show()");
        } catch(Exception e) {
            errorMessage = "Erro: "+e.getMessage();
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    public void confirmarExclusao() {
        try {
            // sua lógica original de inativar
            Pessoa p = mapPessoaEntity();
            p.setAtivo(false);
            pessoaService.atualizar(p);
            PrimeFaces.current().executeScript("PF('successDialog').show()");
        } catch(Exception e) {
            errorMessage = "Erro: "+e.getMessage();
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    // --- NOVIDADES: Export PDF/Excel funcionando ---

    public void exportarPdf() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            List<Pessoa> lista = pessoas; // já filtrada

            Document doc = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            PdfPTable table = new PdfPTable(6);
            Stream.of("Nome","Idade","Email","Nascimento","Doc","Status")
                    .forEach(h -> table.addCell(new PdfPCell(new Phrase(h))));
            for (Pessoa p : lista) {
                table.addCell(p.getNome());
                table.addCell(p.getIdade()==null?"":p.getIdade().toString());
                table.addCell(p.getEmail());
                table.addCell(df.format(p.getData()));
                table.addCell("CPF".equals(p.getTipoDocumento())
                        ? p.getNumeroCPF() : p.getNumeroCNPJ());
                table.addCell(p.getAtivo()?"Ativo":"Inativo");
            }
            doc.add(table);
            doc.close();

            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletResponse resp = (HttpServletResponse)
                    fc.getExternalContext().getResponse();
            resp.reset();
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition",
                    "attachment;filename=consulta_pessoas.pdf");
            try (ServletOutputStream out = resp.getOutputStream()) {
                out.write(baos.toByteArray());
                out.flush();
            }
            fc.responseComplete();
        } catch(Exception e) {
            e.printStackTrace();
            errorMessage = "Falha exportar PDF.";
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    public void exportarExcel() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Workbook wb = new XSSFWorkbook();
            Sheet sh = wb.createSheet("Pessoas");
            Row hd = sh.createRow(0);
            String[] cols = {"Nome","Idade","Email","Nascimento","Doc","Status"};
            for(int i=0;i<cols.length;i++) hd.createCell(i).setCellValue(cols[i]);

            for(int i=0;i<pessoas.size();i++){
                Pessoa p = pessoas.get(i);
                Row r = sh.createRow(i+1);
                r.createCell(0).setCellValue(p.getNome());
                r.createCell(1).setCellValue(p.getIdade()==null?0:p.getIdade());
                r.createCell(2).setCellValue(p.getEmail());
                r.createCell(3).setCellValue(df.format(p.getData()));
                r.createCell(4).setCellValue(
                        "CPF".equals(p.getTipoDocumento())
                                ? p.getNumeroCPF() : p.getNumeroCNPJ()
                );
                r.createCell(5).setCellValue(p.getAtivo()?"Ativo":"Inativo");
            }

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            wb.write(baos);
            wb.close();

            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletResponse resp = (HttpServletResponse)
                    fc.getExternalContext().getResponse();
            resp.reset();
            resp.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            resp.setHeader("Content-Disposition",
                    "attachment;filename=consulta_pessoas.xlsx");
            try (ServletOutputStream out = resp.getOutputStream()) {
                out.write(baos.toByteArray());
                out.flush();
            }
            fc.responseComplete();
        } catch(Exception e) {
            e.printStackTrace();
            errorMessage = "Falha exportar Excel.";
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    // getters & setters
    public List<Pessoa> getPessoas() { return pessoas; }
    public Pessoa getPessoaSelecionada() { return pessoaSelecionada; }
    public void setPessoaSelecionada(Pessoa ps) { this.pessoaSelecionada = ps; }
    public String getErrorMessage() { return errorMessage; }
    public Boolean getTpManutencao() { return tpManutencao; }
    public void setTpManutencao(Boolean tp) { this.tpManutencao = tp; }
}
