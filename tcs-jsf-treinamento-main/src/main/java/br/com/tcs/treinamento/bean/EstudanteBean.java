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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ManagedBean(name="estudanteBean")
@ViewScoped
public class EstudanteBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Pessoa> estudantes;
    private Pessoa selecionado;
    private String errorMessage;
    private transient PessoaService service = new PessoaServiceImpl();

    @PostConstruct
    public void init() {
        estudantes = service.listar()
                .stream()
                .filter(Pessoa::getFazFaculdade)
                .collect(Collectors.toList());
    }

    public String prepararEdicao(Pessoa p) {
        this.selecionado = p;
        return "alterar?faces-redirect=true&pessoaId="+p.getId()+"&tpManutencao=true";
    }

    public void confirmar() {
        try {
            service.atualizar(selecionado);
            PrimeFaces.current().executeScript("PF('successDialog').show()");
        } catch(Exception e) {
            errorMessage = "Erro: "+e.getMessage();
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    public void confirmarExclusao() {
        try {
            // mantém sua inativação
            selecionado.setAtivo(false);
            service.atualizar(selecionado);
            PrimeFaces.current().executeScript("PF('successDialog').show()");
        } catch(Exception e) {
            errorMessage = "Erro: "+e.getMessage();
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    // Ainda usa confirmarExclusaoModel.xhtml para diálogo

    public void exportarPdf() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Document doc = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);
            doc.open();
            PdfPTable table = new PdfPTable(5);
            Stream.of("Nome","Faculdade","Curso","Período","Início")
                    .forEach(h->table.addCell(new PdfPCell(new Phrase(h))));
            for (Pessoa p : estudantes) {
                table.addCell(p.getNome());
                table.addCell(p.getFaculdade());
                table.addCell(p.getCurso());
                table.addCell(p.getPeriodo()==null?"":p.getPeriodo().toString());
                table.addCell(df.format(p.getDataInicio()));
            }
            doc.add(table);
            doc.close();

            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletResponse resp = (HttpServletResponse)
                    fc.getExternalContext().getResponse();
            resp.reset();
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition","attachment;filename=estudantes.pdf");
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
            Sheet sh = wb.createSheet("Estudantes");
            Row hd = sh.createRow(0);
            String[] cols = {"Nome","Faculdade","Curso","Período","Início"};
            for (int i=0;i<cols.length;i++) hd.createCell(i).setCellValue(cols[i]);
            for (int i=0;i<estudantes.size();i++){
                Pessoa p = estudantes.get(i);
                Row r=sh.createRow(i+1);
                r.createCell(0).setCellValue(p.getNome());
                r.createCell(1).setCellValue(p.getFaculdade());
                r.createCell(2).setCellValue(p.getCurso());
                r.createCell(3).setCellValue(p.getPeriodo()==null?0:p.getPeriodo());
                r.createCell(4).setCellValue(df.format(p.getDataInicio()));
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
            resp.setHeader("Content-Disposition","attachment;filename=estudantes.xlsx");
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
    public List<Pessoa> getEstudantes() { return estudantes; }
    public Pessoa getSelec() { return selecionado; }
    public void setSelec(Pessoa p) { this.selecionado = p; }
    public String getErrorMessage() { return errorMessage; }
}
