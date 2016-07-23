package watson.dialog.tools.model.dao;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

import watson.dialog.tools.model.dao.xsd.DialogType;
import watson.dialog.tools.model.dao.xsd.FolderNodeType;

public class WatsonDialogDocumentTest {

  private static final String DEFALUT_DIALOG_SERVICE_FILE = "DefaultDialogService.xml";

  @Test
  public void testReadDefaultFile() throws Exception {
    URL url = WatsonDialogXsdDao.class.getResource(DEFALUT_DIALOG_SERVICE_FILE);
    DialogType dialogType = WatsonDialogXsdDao.read(new File(new URI(url.toString())));
    assertThat(dialogType.getFlow().getFolder().size(), is(4));
  }

  @Test
  public void testWriteFile() throws Exception {
    URL url = WatsonDialogXsdDao.class.getResource(DEFALUT_DIALOG_SERVICE_FILE);
    DialogType dialogType = WatsonDialogXsdDao.read(new File(new URI(url.toString())));

    FolderNodeType folder = new FolderNodeType();
    folder.setLabel("Test");
    folder.setId("TestId");
    dialogType.getFlow().getFolder().add(folder);

    File file = new File("dummy.xml");
    WatsonDialogXsdDao.write(dialogType, file);

    file.delete();
  }
}
