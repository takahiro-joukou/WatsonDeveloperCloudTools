package watson.dialog.tools.model.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import watson.dialog.tools.exception.WatsonDialogToolsException;
import watson.dialog.tools.model.dao.xsd.DialogType;
import watson.dialog.tools.model.dao.xsd.ObjectFactory;

public class WatsonDialogXsdDao {

  /** encoding */
  private static final String FILE_ENCODING = "UTF-8";

  /** schema file name */
  private static final String SCHEMA_FILE_NAME = "WatsonDialogDocument_1.0.xsd";

  /**
   *
   */
  private WatsonDialogXsdDao() {
    //singleton
  }

  /**
   *
   * @param file
   * @return
   */
  public static DialogType read(File file) {
    try {
      DialogType dialogType = JAXB.unmarshal(file, DialogType.class);
      return dialogType;
    } catch (DataBindingException e) {
      throw new WatsonDialogToolsException(e);
    }
  }

  /**
   *
   * @param input
   * @return
   */
  public static DialogType read(InputStream input) {
    try {
      DialogType dialogType = JAXB.unmarshal(input, DialogType.class);
      return dialogType;
    } catch (DataBindingException e) {
      throw new WatsonDialogToolsException(e);
    }
  }

  /**
   *
   * @param dialogType
   * @param file
   */
  public static void write(DialogType dialogType, File file) {
    try {
      FileOutputStream out = new FileOutputStream(file);
      writeFile(dialogType, out);
      out.close();
    } catch (IOException | XMLStreamException | JAXBException e) {
      throw new WatsonDialogToolsException(e);
    }
  }

  public static InputStream getWriteStream(DialogType dialogType) {
    PipedOutputStream out = new PipedOutputStream();
    try {
      PipedInputStream input = new PipedInputStream(out, 1024 * 10);
      writeFile(dialogType, out);
      return input;
    } catch (IOException | XMLStreamException | JAXBException e) {
      throw new WatsonDialogToolsException(e);
    } finally {
      try {
        out.close();
      } catch (IOException e) {
        throw new WatsonDialogToolsException(e);
      }
    }
  }

  /**
   *
   * @param dialogType
   * @param out
   * @throws XMLStreamException
   * @throws JAXBException
   */
  private static void writeFile(DialogType dialogType, OutputStream out) throws XMLStreamException, JAXBException {
    XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
    XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(out, FILE_ENCODING);
    xmlStreamWriter.writeStartDocument(FILE_ENCODING, "1.0");

    JAXBContext context = JAXBContext.newInstance(DialogType.class);
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_ENCODING, FILE_ENCODING);
    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
    marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, SCHEMA_FILE_NAME);
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

    ObjectFactory objectFactory = new ObjectFactory();
    JAXBElement<DialogType> element = objectFactory.createDialog(dialogType);
    marshaller.marshal(element, xmlStreamWriter);
    xmlStreamWriter.writeEndDocument();
    xmlStreamWriter.close();
  }
}
