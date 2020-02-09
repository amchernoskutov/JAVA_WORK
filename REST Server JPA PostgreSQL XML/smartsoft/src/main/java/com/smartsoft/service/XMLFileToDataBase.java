package com.smartsoft.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import com.smartsoft.domain.Article;
import com.smartsoft.domain.Customer;
import com.smartsoft.domain.Purchase;
import com.smartsoft.domain.Shoppinglist;
import com.smartsoft.repository.CustomerRepository;
import com.smartsoft.repository.PurchaseRepository;
import com.smartsoft.repository.ShoppinglistRepository;

@Service
public class XMLFileToDataBase {
  private Customer customer;
  private Purchase purchase;
  private Shoppinglist shoppinglist;
  private Set<Article> atricles;
  private ShoppinglistRepository shoppinglistRepo;
  private PurchaseRepository purchaseRepo;
  private CustomerRepository customerRepo;

  public XMLFileToDataBase(ShoppinglistRepository shoppinglistRepo, PurchaseRepository purchaseRepo,
      CustomerRepository customerRepo) {
    this.customer = new Customer();
    this.purchase = new Purchase();
    this.shoppinglist = new Shoppinglist();
    atricles = new HashSet<Article>();
    this.shoppinglistRepo = shoppinglistRepo;
    this.purchaseRepo = purchaseRepo;
    this.customerRepo = customerRepo;
  }

  private boolean initDataBase(String getChildNodes, String getTextContent) {

    switch (getChildNodes) {

      case "purchasename":
        purchase.setPurchasename(getTextContent);
        break;
      case "customername":
        customer.setCustomername(getTextContent);
        break;
      case "lastname":
        customer.setLastname(getTextContent);
        break;
      case "age":
        customer.setAge(Integer.parseInt(getTextContent));
        break;
      case "count":
        shoppinglist.setCount(Integer.parseInt(getTextContent));
        break;
      case "amount":
        shoppinglist.setAmount(new BigDecimal(getTextContent));
        break;
      case "purchasedate":
        shoppinglist.setPurchasedate(LocalDate.parse(getTextContent));
        break;
      case "articlename":
        if (Article.HEADPHONES.getCode().equalsIgnoreCase(getTextContent))
          atricles.add(Article.HEADPHONES);
        if (Article.JUICER.getCode().equalsIgnoreCase(getTextContent))
          atricles.add(Article.JUICER);
        if (Article.KEYBOARD.getCode().equalsIgnoreCase(getTextContent))
          atricles.add(Article.KEYBOARD);
        if (Article.SMARTFON.getCode().equalsIgnoreCase(getTextContent))
          atricles.add(Article.SMARTFON);
        if (Article.TV.getCode().equalsIgnoreCase(getTextContent))
          atricles.add(Article.TV);
        shoppinglist.setArticles(atricles);


    }

    return true;
  }

  public boolean exec(File file) {
    try {
      var documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      var document = documentBuilder.parse(file);

      var root = document.getDocumentElement();


      var books = root.getChildNodes();
      for (int i = 0; i < books.getLength(); i++) {
        var book = books.item(i);
        if (book.getNodeType() != Node.TEXT_NODE) {
          var bookProps = book.getChildNodes();
          for (int j = 0; j < bookProps.getLength(); j++) {
            Node bookProp = bookProps.item(j);
            if (bookProp.getNodeType() != Node.TEXT_NODE) {
              initDataBase(bookProp.getNodeName(),
                  bookProp.getChildNodes().item(0).getTextContent());
            }

          }
          initDataBase(book.getNodeName(), book.getChildNodes().item(0).getTextContent());
        }
      }

      if (purchaseRepo.findByPurchasenameIgnoreCase(purchase.getPurchasename()) == null) {
        shoppinglist.setPurchases(purchaseRepo.save(purchase));
      } else {
        shoppinglist
            .setPurchases(purchaseRepo.findByPurchasenameIgnoreCase(purchase.getPurchasename()));
      }
      if (customerRepo.findByCustomernameAndLastnameAndAgeAllIgnoreCase(customer.getCustomername(),
          customer.getLastname(), customer.getAge()) == null) {
        shoppinglist.setCustomers(customerRepo.save(customer));
      } else {
        shoppinglist.setCustomers(customerRepo.findByCustomernameAndLastnameAndAgeAllIgnoreCase(
            customer.getCustomername(), customer.getLastname(), customer.getAge()));
      }
      shoppinglistRepo.save(shoppinglist);

    } catch (ParserConfigurationException ex) {
      ex.printStackTrace(System.out);
    } catch (SAXException ex) {
      ex.printStackTrace(System.out);
    } catch (IOException ex) {
      ex.printStackTrace(System.out);
    }

    return true;
  }
}
