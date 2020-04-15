package loader.svltr.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import loader.svltr.restclients.RestClient;

@EnableScheduling
@Service
public class ManagerTokenScheduler {
  private static final int FIXED_DELAY = 23 * 60 *60 * 1000 + 15 * 60 * 1000;

  @Autowired
  private RestClient restClient; 

  /** 
   * Планировщик каждые 23 часа и 45 минут запрашивает новый token,
   * так как срок его действия 24 часа.
   * 
   */
  
  @Scheduled(initialDelay=0, fixedDelay = FIXED_DELAY)
  public void getToken() {
    restClient.getToken();
  }  

}
