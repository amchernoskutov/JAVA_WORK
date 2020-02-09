package com.rgd.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import com.rgd.domain.Product;
import com.rgd.repository.ProductRepository;

@Service
public class ProductService {
  private ProductRepository productRepo;

  public ProductService(ProductRepository productRepo) {
    this.productRepo = productRepo;
  }

  private boolean initProduct(Product product, String getChildNodes, String getTextContent) {

    switch (getChildNodes) {

      case "productname":
        product.setProductname(getTextContent);
        break;
      case "amount":
        product.setAmount(new BigDecimal(getTextContent));
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
          var product = new Product();
          for (int j = 0; j < bookProps.getLength(); j++) {
            Node bookProp = bookProps.item(j);
            if (bookProp.getNodeType() != Node.TEXT_NODE) {
              initProduct(product, bookProp.getNodeName(),
                  bookProp.getChildNodes().item(0).getTextContent());
            }
          }
          initProduct(product, book.getNodeName(), book.getChildNodes().item(0).getTextContent());

          if (productRepo.findByProductnameAllIgnoreCase(product.getProductname()) == null) {
            try {
              productRepo.save(product);
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
