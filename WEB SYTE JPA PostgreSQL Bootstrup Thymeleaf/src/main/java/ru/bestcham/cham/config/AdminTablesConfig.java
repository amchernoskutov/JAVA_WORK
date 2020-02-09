package ru.bestcham.cham.config;

public enum AdminTablesConfig {
  CATEGORYFILM("Таблица категорий фильмов"), 
  CATEGORYSYTE("Таблица категорий сайтов"),
  CATEGORYYOUTUBE("Таблица категорий YouTube каналов"),
  FILM("Таблица фильмов"),
  SYTE("Таблица сайтов"),
  YOUTUBE("Таблица YouTube каналов");
  
  private String code;

  AdminTablesConfig(String code){
    this.code = code;
  }
  public String getCode(){ return code;}

  @Override
  public String toString() {
      return name().toLowerCase();
  }
  
}

