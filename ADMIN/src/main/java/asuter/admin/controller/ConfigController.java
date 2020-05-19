package asuter.admin.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import asuter.admin.domain.SystemName;
import asuter.admin.service.SystemNameService;

@Controller
public class ConfigController {

  @Autowired
  SystemNameService systemNameService;

  @GetMapping("/asuteradmin/systemname/edit/{id}")
  public String getEditSystemName(@PathVariable Integer id, Map<String, Object> model) {
    Map<String, String> systemNameItemError = new HashMap<String, String>();
    SystemName systemName = null;
    
    try {
      systemName = systemNameService.findOne(id);
    } catch (Exception e) {
      systemNameItemError.put("updateSystemName", "Ошибка поиска записи в базе данных " + e.getMessage());
    }
    if (systemName == null) {
      systemName = new SystemName();
      systemName.setIdSystem(0);
      systemName.setMnemSystem("");
      systemName.setDestination("");
      systemNameItemError.put("updateSystemName", "Запись в базе данных не найдена");
    }
    model.put("systemNameItemError", systemNameItemError);
    model.put("updateSystemNameItem", systemName);
    model.put("deleteSystemNameItem", new SystemName());
    model.put("addsystemNameItem", new SystemName());
    try {
      model.put("systemNames", systemNameService.findByAll());
    } catch (Exception e) {
      //
    }

    return "navmenu3";
  }

  @PostMapping("/asuteradmin/systemname/edit/{id}")
  public String postEditSystemName(@PathVariable Integer id, SystemName systemName, Map<String, Object> model) {
    Map<String, String> systemNameItemError = new HashMap<String, String>();
    systemName.setMnemSystem(systemName.getMnemSystem().replace(",", ""));
    systemName.setDestination(systemName.getDestination().replace(",", ""));
    systemName.setIdSystem(id);
    
    if ((systemName.getMnemSystem().isEmpty())|("".equals(systemName.getMnemSystem().trim()))) {
      systemNameItemError.put("mnemSystem", "Код системы не указан");
    }
    if ((systemName.getDestination().isEmpty())|("".equals(systemName.getDestination().trim()))) {
      systemNameItemError.put("destination", "Наименование системы не указано");
    }
    
    model.put("systemNameItemError", systemNameItemError);
    
    if (systemNameItemError.isEmpty()) {
      try {
        systemNameService.update(id, systemName.getMnemSystem(), systemName.getDestination());
        systemName = new SystemName();
      } catch (Exception e) {
        systemNameItemError.put("updateSystemName", "Ошибка редактирования записи в базе данных " + e.getMessage());
      }
    }
    
    model.put("addsystemNameItem", new SystemName());
    model.put("updateSystemNameItem", systemName);
    model.put("deleteSystemNameItem", new SystemName());
    try {
      model.put("systemNames", systemNameService.findByAll());
    } catch (Exception e) {
      //
    }

    return "navmenu3";
  }

  @PostMapping("/asuteradmin/systemname/delete/{id}")
  public String postDeleteSystemName(@PathVariable Integer id, Map<String, Object> model) {
    Map<String, String> systemNameItemError = new HashMap<String, String>();
    
    try {
      systemNameService.delete(id);
    } catch (Exception e) {
      if (e.getMessage().contains("violates foreign key constraint")) {
        systemNameItemError.put("deleteSystemName", "Ошибка удаления, на запись есть ссылки из других таблиц");
      } else {
        systemNameItemError.put("deleteSystemName", "Ошибка удаления записи из базы данных " + e.getMessage());
      }
    }
    
    try {
      model.put("systemNames", systemNameService.findByAll());
      model.put("systemNameItemError", systemNameItemError);
      model.put("addsystemNameItem", new SystemName());
      model.put("updateSystemNameItem", new SystemName());
      model.put("deleteSystemNameItem", new SystemName());
    } catch (Exception e) {
      //
    }


    return "navmenu3";
  }

  @GetMapping("/asuteradmin/systemname/delete/{id}")
  public String getDeleteSystemName(@PathVariable Integer id, Map<String, Object> model) {
    Map<String, String> systemNameItemError = new HashMap<String, String>();
    SystemName systemName = new SystemName();
    systemName.setIdSystem(id);
    
    
    model.put("systemNameItemError", systemNameItemError);
    model.put("deleteSystemNameItem", systemName);
    model.put("updateSystemNameItem", new SystemName());
    model.put("addsystemNameItem", new SystemName());
    try {
      model.put("systemNames", systemNameService.findByAll());
    } catch (Exception e) {
      //
    }

    return "navmenu3";
  }

  @PostMapping("/asuteradmin/systemname/add")
  public String postAddSystemName(SystemName systemName, Map<String, Object> model) {
    Map<String, String> systemNameItemError = new HashMap<String, String>();
    systemName.setMnemSystem(systemName.getMnemSystem().replace(",", ""));
    systemName.setDestination(systemName.getDestination().replace(",", ""));
    
    if ((systemName.getMnemSystem().isEmpty())|("".equals(systemName.getMnemSystem().trim()))) {
      systemNameItemError.put("mnemSystem", "Код системы не указан");
    }
    if ((systemName.getDestination().isEmpty())|("".equals(systemName.getDestination().trim()))) {
      systemNameItemError.put("destination", "Наименование системы не указано");
    }
    
    model.put("systemNameItemError", systemNameItemError);
    
    if (systemNameItemError.isEmpty()) {
      //Добавляем в базу
      try {
        systemNameService.add(systemName.getMnemSystem(), systemName.getDestination());
        systemName = new SystemName();
      } catch (Exception e) {
        systemNameItemError.put("insertSystemName", "Ошибка добавления записи в базу данных " + e.getMessage());
      }
    }
    
    model.put("addsystemNameItem", systemName);
    model.put("updateSystemNameItem", new SystemName());
    model.put("deleteSystemNameItem", new SystemName());
    try {
      model.put("systemNames", systemNameService.findByAll());
    } catch (Exception e) {
      //
    }


    return "navmenu3";
  }

}
