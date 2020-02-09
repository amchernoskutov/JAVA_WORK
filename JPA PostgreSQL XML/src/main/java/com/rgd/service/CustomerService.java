package com.rgd.service;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import com.rgd.domain.Customer;
import com.rgd.domain.Customer.Gender;
import com.rgd.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {
  private CustomerRepository customerRepo;

  public CustomerService(CustomerRepository customerRepo) {
    this.customerRepo = customerRepo;
  }

  private boolean initCustomer(Customer customer, String getChildNodes, String getTextContent) {

    switch (getChildNodes) {

      case "customername":
        customer.setCustomername(getTextContent);
        break;
      case "lastname":
        customer.setLastname(getTextContent);
        break;
      case "age":
        customer.setAge(Integer.parseInt(getTextContent));
        break;
      case "gender":
        if (Gender.MAN.name().equals(getTextContent))
          customer.setGender(Gender.MAN);
        if (Gender.WOMAN.name().equals(getTextContent))
          customer.setGender(Gender.WOMAN);
        break;
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
          var customer = new Customer();
          for (int j = 0; j < bookProps.getLength(); j++) {
            Node bookProp = bookProps.item(j);
            if (bookProp.getNodeType() != Node.TEXT_NODE) {
              initCustomer(customer, bookProp.getNodeName(),
                  bookProp.getChildNodes().item(0).getTextContent());
            }
          }
          initCustomer(customer, book.getNodeName(), book.getChildNodes().item(0).getTextContent());

          if (customerRepo.findByCustomernameAndLastnameAndAgeAndGenderAllIgnoreCase(
              customer.getCustomername(), customer.getLastname(), customer.getAge(),
              customer.getGender()) == null) {
            try {
              customerRepo.save(customer);
            } catch (Exception e) {
              //
            }
          }
        }
      }
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
