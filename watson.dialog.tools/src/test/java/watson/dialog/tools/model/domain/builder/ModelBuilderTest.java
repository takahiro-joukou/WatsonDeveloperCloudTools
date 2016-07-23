package watson.dialog.tools.model.domain.builder;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

import watson.dialog.tools.model.dao.WatsonDialogXsdDao;
import watson.dialog.tools.model.dao.xsd.DialogType;
import watson.dialog.tools.model.domain.Dialog;

public class ModelBuilderTest {

  @Test
  public void testDialogService001() throws Exception {
    URL url = DomainBuilder.class.getResource("DialogService001.xml");
    DialogType dialogType = WatsonDialogXsdDao.read(new File(new URI(url.toString())));

    Dialog dialog = DomainBuilder.build(dialogType);
    assertThat(dialog.getFlow(), notNullValue());
  }

  @Test
  public void testDialogService002() throws Exception {
    URL url = DomainBuilder.class.getResource("DialogService002.xml");
    DialogType dialogType = WatsonDialogXsdDao.read(new File(new URI(url.toString())));

    Dialog dialog = DomainBuilder.build(dialogType);
    assertThat(dialog.getFlow(), notNullValue());
  }
}
