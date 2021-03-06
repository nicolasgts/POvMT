package com.projectles.povmt.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Atividade implements Comparable<Atividade> , Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descricao;
    private Date criacao;
    private Date atualizacao;
    private double tisum;
    private List<TempoInvestido> tis;

    public Atividade(String nome, String descricao) {
        this.tisum = 0;
        this.nome = nome;
        this.descricao = descricao;
        this.criacao = new Date();
        this.tis = new ArrayList<>();
        this.atualizacao = new Date();
    }

    public Atividade() {
        this.tis = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public List<TempoInvestido> getTis() {
        return tis;
    }

    public void setTis(List<TempoInvestido> tis) {
        this.tis = tis;
    }

    public void addTempoInvestido(TempoInvestido tempoInvestido) {
        tisum += tempoInvestido.getTi();
        tis.add(tempoInvestido);
    }

    public double getTisum() {
        return tisum;
    }

    public Date getAtualizacao() {
        return atualizacao;
    }

    public Date getCriacao() {
        return criacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int compareTo(Atividade another) {
        if (atualizacao == null || another.atualizacao == null)
            return 0;
        else
            return atualizacao.compareTo(another.atualizacao);
    }

    public void setAtualizacao(Date atualizacao) {
        this.atualizacao = atualizacao;
    }
}