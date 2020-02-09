package com.smartsoft.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
public class XsdValidator {

  public static ArrayList<String> xsdValidatorExec(File file) {
    var errorsValidator = new ArrayList<String>();

    try {
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = factory.newSchema(new File("check.xsd"));
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(file));

    } catch (SAXException e) {
      errorsValidator.add("SAXException: " + e.getMessage());
    } catch (IOException e) {
      errorsValidator.add("IOException: " + e.getMessage());
    }

    return errorsValidator;
  }

  public static File convertMultiPartToFile(MultipartFile file) throws IOException {
    File convFile = new File(file.getOriginalFilename());
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(file.getBytes());
    fos.close();

    return convFile;
  }


}

