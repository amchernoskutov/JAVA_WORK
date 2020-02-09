package ru.bestcham.cham.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import lombok.Data;

@Data
@Entity
public class Film {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  
  private Date placedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categorymenu_id", nullable = false)
  private СategoryMenu categorymenu;  
  
  @NotNull
  private String name; // Название фильма
  
  private String originalfilmname; // Оригинальное название фильма

  @Digits(integer=4, fraction=0, message="Не более четырех знаков")
  private Integer yearofmanufacture; // Год выпуска
  
  @ManyToMany(targetEntity=CategoryFilm.class, fetch = FetchType.EAGER)
  private List<CategoryFilm> categoryfilms = new ArrayList<>(); // Жанр
  
  private String producer; // Режиссер

  private String intotherole; // В ролях

  @Digits(integer=3, fraction=0, message="Не более трех знаков")
  private Integer durationminute; // Продолжительность в минутах
  
  @Lob
  @Type( type = "org.hibernate.type.TextType" )
  private String description; // Описание

  private String picture; // Картинка
  
  @PrePersist
  void placedAt() {
    this.placedAt = new Date();
  }

}
