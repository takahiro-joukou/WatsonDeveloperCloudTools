package watson.dialog.tools.editor.pages.edit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import watson.dialog.tools.model.domain.ConceptNode;
import watson.dialog.tools.model.domain.DefaultNode;
import watson.dialog.tools.model.domain.Entity;
import watson.dialog.tools.model.domain.FolderNode;
import watson.dialog.tools.model.domain.GetUserInputNode;
import watson.dialog.tools.model.domain.GoToNode;
import watson.dialog.tools.model.domain.IfNode;
import watson.dialog.tools.model.domain.InputNode;
import watson.dialog.tools.model.domain.OutputNode;
import watson.dialog.tools.model.domain.SearchNode;
import watson.dialog.tools.model.domain.Settings;
import watson.dialog.tools.model.domain.SpecialSetting;
import watson.dialog.tools.model.domain.Var;
import watson.dialog.tools.model.domain.VarFolder;

public class WatsonDialogServiceEditPage extends FormPage {

  private static final String PAGE_ID = "watson.dialog.tools.editors.pages.WatsonDialogServiceEditPage";

  private EditBlock editBlock;

  public WatsonDialogServiceEditPage(FormEditor editor) {
    super(editor, PAGE_ID, "Edit");
    editBlock = new EditBlock();
  }

  protected void createFormContent(IManagedForm managedForm) {
    managedForm.getForm().setText("Edit Watson Dialog Service");
    managedForm.setInput(getEditorInput());
    editBlock.createContent(managedForm);
  }

  private class EditBlock extends MasterDetailsBlock {
    @Override
    protected void createMasterPart(IManagedForm managedForm, Composite parent) {
      new EditPageMasterSectionPart(parent, managedForm);
    }

    @Override
    protected void registerPages(DetailsPart detailsPart) {
      detailsPart.registerPage(FolderNode.class, new FolderNodeDetailsPage());
      detailsPart.registerPage(InputNode.class, new InputNodeDetailsPage());
      detailsPart.registerPage(OutputNode.class, new OutputNodeDetailsPage());
      detailsPart.registerPage(GetUserInputNode.class, new GetUserInputNodeDetailsPage());
      detailsPart.registerPage(SearchNode.class, new SearchNodeDetailsPage());
      detailsPart.registerPage(IfNode.class, new IfNodeDetailsPage());
      detailsPart.registerPage(GoToNode.class, new GoToNodeDetailsPage());
      detailsPart.registerPage(DefaultNode.class, new DefaultNodeDetailsPage());
      detailsPart.registerPage(ConceptNode.class, new ConceptNodeDetailsPage());
      detailsPart.registerPage(Entity.class, new EntityDetailsPage());
      detailsPart.registerPage(VarFolder.class, new VarFolderDetailsPage());
      detailsPart.registerPage(Var.class, new VarDetailsPage());
      detailsPart.registerPage(Settings.class, new SettingsDetailsPage());
      detailsPart.registerPage(SpecialSetting.class, new SpecialSettingDetailsPage());
    }

    @Override
    protected void createToolBarActions(IManagedForm managedForm) {
    }
  }
}
