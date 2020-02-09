package ru.bestcham.cham.form;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CategoryYoutubeForm {
  private String categoryyoutubenameadd;
  private String categoryyoutubeidedit;
  private String categoryyoutubenameedit;

  public CategoryYoutubeForm() {
    categoryyoutubenameadd = "Введите категорию youtube канала";
  }

}
