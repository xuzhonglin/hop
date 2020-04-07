/*! ******************************************************************************
 *
 * Hop : The Hop Orchestration Platform
 *
 * http://www.project-hop.org
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.apache.hop.ui.workflow.action;

import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.logging.LoggingObjectType;
import org.apache.hop.core.logging.SimpleLoggingObject;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.apache.hop.metastore.api.IMetaStore;
import org.apache.hop.pipeline.transform.ITransform;
import org.apache.hop.ui.core.PropsUI;
import org.apache.hop.ui.core.database.dialog.DatabaseDialog;
import org.apache.hop.ui.core.widget.MetaSelectionLine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

/**
 * The ActionDialog class is responsible for constructing and opening the settings dialog for the action. Whenever
 * the user opens the action settings in HopGui, it will instantiate the dialog class passing in the IAction
 * object and call the
 *
 * <pre>
 * open()
 * </pre>
 * <p>
 * method on the dialog. SWT is the native windowing environment of HopGui, and it is typically the framework used for
 * implementing action dialogs.
 */
public class ActionDialog extends Dialog {

  /**
   * The package name, used for internationalization.
   */
  private static Class<?> PKG = ITransform.class; // for i18n purposes, needed by Translator!!

  /**
   * The loggingObject for the dialog
   */
  public static final ILoggingObject loggingObject = new SimpleLoggingObject( "Action dialog", LoggingObjectType.JOBENTRYDIALOG, null );

  /**
   * A reference to the action interface
   */
  protected IAction jobEntryInt;

  /**
   * the MetaStore
   */
  protected IMetaStore metaStore;

  /**
   * The workflow metadata object.
   */
  protected WorkflowMeta workflowMeta;

  /**
   * A reference to the shell object
   */
  protected Shell shell;

  /**
   * A reference to the properties user interface
   */
  protected PropsUI props;

  /**
   * A reference to the parent shell
   */
  protected Shell parent;

  /**
   * A reference to a database dialog
   */
  protected DatabaseDialog databaseDialog;

  /**
   * Instantiates a new action dialog.
   *
   * @param parent   the parent shell
   * @param jobEntry the action interface
   * @param workflowMeta  the workflow metadata object
   */
  public ActionDialog( Shell parent, IAction jobEntry, WorkflowMeta workflowMeta ) {
    super( parent, SWT.NONE );
    props = PropsUI.getInstance();

    this.jobEntryInt = jobEntry;
    this.workflowMeta = workflowMeta;
    this.shell = parent;
  }

  /**
   * Adds the connection line for the given parent and previous control, and returns a meta selection manager control
   *
   * @param parent   the parent composite object
   * @param previous the previous control
   * @param
   * @return the combo box UI component
   */
  public MetaSelectionLine<DatabaseMeta> addConnectionLine( Composite parent, Control previous, DatabaseMeta selected, ModifyListener lsMod ) {

    final MetaSelectionLine<DatabaseMeta> wConnection = new MetaSelectionLine<>(
      workflowMeta,
      metaStore,
      DatabaseMeta.class, parent, SWT.NONE,
      BaseMessages.getString( PKG, "BaseTransformDialog.Connection.Label" ),
      "Select the relational database connection to use" // TODO : i18n
    );
    wConnection.addToConnectionLine( parent, previous, selected, lsMod );
    return wConnection;
  }

  public IMetaStore getMetaStore() {
    return metaStore;
  }

  public void setMetaStore( IMetaStore metaStore ) {
    this.metaStore = metaStore;
  }


}
