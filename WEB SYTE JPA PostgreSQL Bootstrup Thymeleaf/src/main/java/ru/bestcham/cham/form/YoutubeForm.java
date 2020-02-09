package ru.bestcham.cham.form;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategorySyte;
import ru.bestcham.cham.domain.CategoryYoutube;
import ru.bestcham.cham.domain.СategoryMenu;

@Slf4j
@Data
public class YoutubeForm {
  private String youtubenameadd;
  private String youtubeurladd;
  private String youtubedescriptionadd;
  private String picture; 
  
  private String youtubeidedit;
  private String youtubenameedit;
  private String youtubeurledit;
  private String youtubedescriptionedit;

  private СategoryMenu categoryMenu;
  private List<Integer> categoryyoutubes; 
  private List<CategoryYoutube> ganres; 

  public YoutubeForm() {
    youtubenameadd = "Введите название youtube канала";
    youtubeurladd = "Введите url";
    youtubedescriptionadd = "Введите описание youtube канала";
    categoryyoutubes = new ArrayList<>();
    ganres = new ArrayList<>();
  }

}
