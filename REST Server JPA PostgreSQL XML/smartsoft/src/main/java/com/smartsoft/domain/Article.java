package com.smartsoft.domain;

public enum Article {
  TV("Телевизор"), 
  SMARTFON("Смартфон"), 
  JUICER("Соковыжималка"), 
  HEADPHONES("Наушники"), 
  KEYBOARD("Клавиатура");

  private String code;

  Article(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
