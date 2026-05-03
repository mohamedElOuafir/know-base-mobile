package com.rag.knowbase.model;

import java.sql.Date;

public class CollectionUser {

    private Long idCollection;
    private String name;
    private Date createdAt;

    public CollectionUser(Long idCollection, String name, Date createdAt){
        this.idCollection = idCollection;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getIdCollection(){
        return idCollection;
    }

    public void setIdCollection(Long idCollection){
        this.idCollection = idCollection;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

}
