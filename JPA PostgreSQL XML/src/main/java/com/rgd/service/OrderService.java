package com.rgd.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import com.rgd.controller.OrderController;
import com.rgd.domain.Order;
import com.rgd.form.OrderInitForm;
import com.rgd.repository.CustomerRepository;
import com.rgd.repository.OrderRepository;
import com.rgd.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {
  private CustomerRepository customerRepo;
  private ProductRepository productRepo;
  private OrderRepository orderRepo;

  public OrderService(CustomerRepository customerRepo, ProductRepository productRepo,
      OrderRepository orderRepo) {
    this.customerRepo = customerRepo;
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
  }

  private boolean initOrder(Order order, String getChildNodes, String getTextContent) {
    switch (getChildNodes) {

      case "count":
        order.setCount(Integer.parseInt(getTextContent));
        break;
      case "purchasedate":
        order.setPurchasedate(LocalDate.parse(getTextContent));
        break;
      case "product":
        order.setProduct(productRepo.findById(Long.parseLong(getTextContent)).get());
        break;
      case "customer":
        order.setCustomer(customerRepo.findById(Long.parseLong(getTextContent)).get());
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
          var order = new Order();
          for (int j = 0; j < bookProps.getLength(); j++) {
            Node bookProp = bookProps.item(j);
            if (bookProp.getNodeType() != Node.TEXT_NODE) {
              initOrder(order, bookProp.getNodeName(),
                  bookProp.getChildNodes().item(0).getTextContent());
            }
          }
          initOrder(order, book.getNodeName(), book.getChildNodes().item(0).getTextContent());
          try {
            orderRepo.save(order);
          } catch (Exception e) {
            //
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

  public void addOrder(OrderInitForm orderAddForm) {
    var order = new Order();
    order.setCount(orderAddForm.getCount());
    order.setPurchasedate(orderAddForm.getPurchasedate());
    order.setCustomer(customerRepo.findById((long) orderAddForm.getIdcustomer()).get());
    order.setProduct(productRepo.findById((long) orderAddForm.getIdproduct()).get());
    try {
      orderRepo.save(order);
    } catch (Exception e) {
      //
    }
  }

  public void editOrder(OrderInitForm orderAddForm) {
    var order = orderRepo.findById(orderAddForm.getId()).get();
    order.setCount(orderAddForm.getCount());
    order.setPurchasedate(orderAddForm.getPurchasedate());
    order.setCustomer(customerRepo.findById((long) orderAddForm.getIdcustomer()).get());
    order.setProduct(productRepo.findById((long) orderAddForm.getIdproduct()).get());
    try {
      orderRepo.save(order);
    } catch (Exception e) {
      //
    }
  }

  public void delete(Long id) {
    var order = orderRepo.findById(id).get();
    if (order != null)
      orderRepo.delete(order);
  }

  public OrderInitForm formInit(Long id, OrderInitForm orderInitForm) {
    var order = orderRepo.findById(id).get();
    if (order != null) {
      orderInitForm.setPurchasedate(order.getPurchasedate());
      orderInitForm.setCount(order.getCount());
      orderInitForm.setIdcustomer(order.getCustomer().getId().intValue());
      orderInitForm.setIdproduct(order.getProduct().getId().intValue());
      orderInitForm.setId(id);
    }
    return orderInitForm;
  }

}
