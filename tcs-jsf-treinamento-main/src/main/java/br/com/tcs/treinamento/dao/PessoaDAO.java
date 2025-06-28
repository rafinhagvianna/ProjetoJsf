package br.com.tcs.treinamento.dao;

import br.com.tcs.treinamento.entity.Pessoa;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PessoaDAO {

    private EntityManager em;
    private static EntityManagerFactory emf;

    public PessoaDAO() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("myPU");
        }
        em = emf.createEntityManager();
    }

    public void cadastrar(Pessoa pessoa) {
        try {
            em.getTransaction().begin();
            em.persist(pessoa);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public Pessoa buscarPorId(Long id) {
        return em.find(Pessoa.class, id);
    }

    public List<Pessoa> listar() {
        return em.createQuery("SELECT p FROM Pessoa p", Pessoa.class).getResultList();
    }

    public Pessoa atualizar(Pessoa pessoa) {
        try {
            em.getTransaction().begin();
            Pessoa p = em.merge(pessoa);
            em.getTransaction().commit();
            return p;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public void excluir(Pessoa pessoa) {
        try {
            em.getTransaction().begin();
            em.remove(em.contains(pessoa) ? pessoa : em.merge(pessoa));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
}
