package ru.bestcham.cham.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import lombok.Data;

@Data
@Entity
public class Syte {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private Date placedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categorymenu_id", nullable = false)
  private СategoryMenu categorymenu;  
  
  @NotNull
  private String name; // Название сайта

  private String url; // Путь к сайту

  @ManyToMany(targetEntity = CategorySyte.class, fetch = FetchType.EAGER)
  private List<CategorySyte> categorysytes = new ArrayList<>(); // Жанр

  @Lob
  @Type( type = "org.hibernate.type.TextType" )
  private String description; // Описание

  private String picture; // Картинка

  @PrePersist
  void placedAt() {
    this.placedAt = new Date();
  }

}

