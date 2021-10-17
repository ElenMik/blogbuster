package net.ausiasmarch.blogbuster2021;

import java.time.LocalDateTime;
import java.util.Date;

public class PostBean {

    private Integer id;
    private String titulo;
    private String cuerpo;
    private LocalDateTime fecha;
    private String etiquetas;
    private Boolean visible;

    public PostBean(){        
    }
        
    public PostBean(Integer id, String titulo, LocalDateTime fecha) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
    }

    public PostBean(Integer id, String titulo, String cuerpo, LocalDateTime fecha, String etiquetas, Boolean visible) {
        this.id = id;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.fecha = fecha;
        this.etiquetas = etiquetas;
        this.visible = visible;
    }    
        
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(String etiquetas) {
        this.etiquetas = etiquetas;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    
    
    
}
