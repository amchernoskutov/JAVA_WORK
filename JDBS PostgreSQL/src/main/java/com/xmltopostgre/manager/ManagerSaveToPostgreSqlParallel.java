package com.xmltopostgre.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import com.xmltopostgre.xml.DataMarshrut;
import com.xmltopostgre.xml.Marshrut;

public class ManagerSaveToPostgreSqlParallel {
//  public static final String URL = "jdbc:postgresql://172.31.54.133:5432/asu_ter";
//  public static final String USERNAME = "adm_db-ter";
//  public static final String PASSWORD = "psw:7-45-64adm";
  
  public static final String URL = "jdbc:postgresql://localhost:5433/asu_ter";
  public static final String USERNAME = "postgres";
  public static final String PASSWORD = "1";
  public static final Integer COUNT_ACTIV_THREAD = 90;
  
  public ManagerSaveToPostgreSqlParallel() {
    //
  }

  private void writeToMarshrut(int id, Marshrut marshrut) {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    try {
      jdbcTemplate.update("insert into testspeed.marshrut ("
          + "m_id, id_mm, m_depo, m_nmarsh, m_daten, m_datetimeps, m_realdatetimeps, status, m_tabnmash, m_tabnp1,\r\n"
          + "m_tabnp2, m_datetimebr, m_datetimeprl, m_datetimekpvi, m_datetimekpvx, m_datetimesdl, m_datetimek,\r\n"
          + "m_datetimeprek, m_datetimeprekpvx, s_datetimepre, idseance, dtoper\r\n"
          + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", id,
          marshrut.getId_mm(), marshrut.getM_depo(), marshrut.getM_nmarsh(),
          new SimpleDateFormat("yyyy-MM-dd").parse(marshrut.getM_daten()),
          marshrut.getM_datetimeps(), marshrut.getM_datetimeps(), marshrut.getStatus(),
          marshrut.getM_tabnmash(), marshrut.getM_tabnp1(), null, marshrut.getM_datetimebr(),
          marshrut.getM_datetimeprl(), marshrut.getM_datetimekpvi(), marshrut.getM_datetimekpvx(),
          marshrut.getM_datetimesdl(), marshrut.getM_datetimek(), marshrut.getM_datetimeprek(),
          null, marshrut.getS_datetimepre(), null, null);
    } catch (DataAccessException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private void writeToSection(int m_id, Marshrut marshrut) {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final int[] count = {1};
    marshrut.getM_section().forEach(section -> {
      try {
        jdbcTemplate.update("insert into testspeed.section ("
            + "id, m_id, s_nstr, s_koddor, s_koddepo, s_ser, s_nlok, s_sek, id_cs, khit, krecup,\r\n"
            + "ktract, is_first, is_tax, ls_id, c_id\r\n"
            + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", count[0], m_id,
            section.getS_nstr(), section.getS_koddor(), section.getS_koddepo(), section.getS_ser(),
            section.getS_nlok(), section.getS_sek(), null, section.getKhit(), section.getKrecup(),
            section.getKtract(), section.getIs_first(), section.getIs_tax(), null, null);
        count[0]++;
      } catch (DataAccessException e) {
        e.printStackTrace();
      }
    });
  }

  private void writeToToplivo(Integer m_id, Marshrut marshrut) {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final int[] count = {1};
    marshrut.getM_toplivo().forEach(toplivo -> {
      try {

        jdbcTemplate.update(
            "insert into testspeed.toplivo ("
                + "id, m_id, s_nstr, s_toppred, s_topn, s_topk, s_recn, s_reck, s_topp,\r\n"
                + "s_tops, f_stbr, f_stwbr, f_heat, f_recup, f_sum\r\n"
                + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            count[0], m_id, toplivo.getS_nstr(), toplivo.getS_toppred(),
            toplivo.getS_topn(), toplivo.getS_topk(), toplivo.getS_recn(), toplivo.getS_reck(),
            toplivo.getS_topp(), toplivo.getS_tops(), toplivo.getF_stbr(), null,
            toplivo.getF_heat(), toplivo.getF_recup(), null);
        count[0]++;
      } catch (DataAccessException e) {
        e.printStackTrace();
      }
    });

  }

  private void writeToPoezdka(Integer m_id, Marshrut marshrut) {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final int[] count = {1};
    marshrut.getM_poezdka().forEach(poezdka -> {
      try {
        jdbcTemplate.update("insert into testspeed.poezdka ("
            + "id, m_id, p_nstr, p_esr, p_poezd, p_datetimepr, p_datetimeotpr, p_netto, p_brutto, p_manevr,\r\n"
            + "p_otkl, p_prodzs, p_rodrabot, s_kolaxis, p_probeg\r\n"
            + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", count[0], m_id,
            poezdka.getP_nstr(), poezdka.getP_esr(), poezdka.getP_poezd(),
            poezdka.getP_datetimepr(), poezdka.getP_datetimeotpr(), poezdka.getP_netto(),
            poezdka.getP_brutto(), poezdka.getP_manevr(), poezdka.getP_otkl(),
            poezdka.getP_prodzs(), poezdka.getP_rodrabot(), poezdka.getS_kolaxis(),
            poezdka.getP_probeg());
        count[0]++;
      } catch (DataAccessException e) {
        e.printStackTrace();
      }
    });

  }

  private void writeToSostav(Integer m_id, Marshrut marshrut) {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final int[] count = {1};
    marshrut.getM_sostav().forEach(sostav -> {
      try {
        jdbcTemplate.update(
            "insert into testspeed.sostav (" + "id, m_id, p_nstr, s_rodwag, s_kolemp, s_kolful\r\n"
                + ") values (?, ?, ?, ?, ?, ?)",
            count[0], m_id, sostav.getP_nstr(), sostav.getS_rodwag(), sostav.getS_kolemp(),
            sostav.getS_kolful());
        count[0]++;
      } catch (DataAccessException e) {
        e.printStackTrace();
      }
    });

  }

  private void writeToSled(Integer m_id, Marshrut marshrut) {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    final int[] count = {1};
    marshrut.getM_sled().forEach(sled -> {
      try {
        jdbcTemplate.update(
            "insert into testspeed.sled ("
                + "id, m_id, s_nstr, st_dep, km_dep, st_arr, km_arr, p_vidsled\r\n"
                + ") values (?, ?, ?, ?, ?, ?, ?, ?)",
            count[0], m_id, sled.getS_nstr(), null, null, null, null, null);
        count[0]++;
      } catch (DataAccessException e) {
        e.printStackTrace();
      }
    });

  }

  public void writeToPostgerSqlParallel(DataMarshrut dataMarshrut) {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    jdbcTemplate.execute("delete from testspeed.marshrut");
    jdbcTemplate.execute("delete from testspeed.section");
    jdbcTemplate.execute("delete from testspeed.toplivo");
    jdbcTemplate.execute("delete from testspeed.poezdka");
    jdbcTemplate.execute("delete from testspeed.sostav");
    jdbcTemplate.execute("delete from testspeed.sled");

    var threads = new HashSet<Thread>();

    Date startDate;
    Date finishDate;
    startDate = new Date();

    dataMarshrut.getMarshruts().forEach(marshrut -> {

      var thread1 = new Thread(new Runnable() {
        @Override
        public void run() {
          writeToMarshrut(dataMarshrut.getMarshruts().indexOf(marshrut)+1, marshrut);
        }
      });
      thread1.start();
      threads.add(thread1);

      var thread2 = new Thread(new Runnable() {
        @Override
        public void run() {
          if (marshrut.getM_section() != null) writeToSection(dataMarshrut.getMarshruts().indexOf(marshrut)+1, marshrut);
        }
      });
      thread2.start();
      threads.add(thread2);

      var thread3 = new Thread(new Runnable() {
        @Override
        public void run() {
          if (marshrut.getM_toplivo() != null) writeToToplivo(dataMarshrut.getMarshruts().indexOf(marshrut)+1, marshrut);
        }
      });
      thread3.start();
      threads.add(thread3);

      var thread4 = new Thread(new Runnable() {
        @Override
        public void run() {
          if (marshrut.getM_poezdka() != null) writeToPoezdka(dataMarshrut.getMarshruts().indexOf(marshrut)+1, marshrut);
        }
      });
      thread4.start();
      threads.add(thread4);

      var thread5 = new Thread(new Runnable() {
        @Override
        public void run() {
          if (marshrut.getM_sostav() != null)  writeToSostav(dataMarshrut.getMarshruts().indexOf(marshrut)+1, marshrut);
        }
      });
      thread5.start();
      threads.add(thread5);

      var thread6 = new Thread(new Runnable() {
        @Override
        public void run() {
          if (marshrut.getM_sled() != null) writeToSled(dataMarshrut.getMarshruts().indexOf(marshrut)+1, marshrut);
        }
      });
      thread6.start();
      threads.add(thread6);
      
      while (threads.stream().filter(f -> f.isAlive()).count()>COUNT_ACTIV_THREAD) {
        //
      }

    });
    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    finishDate = new Date();

    System.out.println("\nНачало записи в базу postgresql: " + startDate.getTime());
    System.out.println("Завершение записи в базу postgresql: " + finishDate.getTime());
    long milliseconds = startDate.getTime() - finishDate.getTime();

    System.out.println("Разница в секундах (postgresql): " + ((int) (milliseconds / (1000))));
    System.out.println("Разница в миллисекундах (postgresql): " + milliseconds);
  }
}
