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

package org.apache.hop.www;

import org.apache.hop.core.gui.AreaOwner;
import org.apache.hop.core.gui.Point;
import org.apache.hop.core.gui.SwingGC;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.workflow.Workflow;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.WorkflowPainter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class GetWorkflowImageServlet extends BaseHttpServlet implements IHopServerPlugin {

  private static final long serialVersionUID = -4365372274638005929L;

  private static Class<?> PKG = GetPipelineStatusServlet.class; // for i18n purposes, needed by Translator!!

  public static final String CONTEXT_PATH = "/hop/workflowImage";

  /**
   * <div id="mindtouch">
   * <h1>/hop/workflowImage</h1>
   * <a name="GET"></a>
   * <h2>GET</h2>
   * <p>Generates and returns image of the specified workflow.
   * Generates PNG image of the specified workflow currently present on HopServer server. Workflow name and HopServer workflow ID (optional)
   * is used for specifying workflow to get information for. Response is binary of the PNG image.</p>
   *
   * <p><b>Example Request:</b><br />
   * <pre function="syntax.xml">
   * GET /hop/workflowImage?name=dummyWorkflow
   * </pre>
   *
   * </p>
   * <h3>Parameters</h3>
   * <table class="hop-table">
   * <tbody>
   * <tr>
   * <th>name</th>
   * <th>description</th>
   * <th>type</th>
   * </tr>
   * <tr>
   * <td>name</td>
   * <td>Name of the workflow to be used for image generation.</td>
   * <td>query</td>
   * </tr>
   * <tr>
   * <td>id</td>
   * <td>HopServer id of the workflow to be used for image generation.</td>
   * <td>query, optional</td>
   * </tr>
   * </tbody>
   * </table>
   *
   * <h3>Response Body</h3>
   *
   * <table class="hop-table">
   * <tbody>
   * <tr>
   * <td align="right">binary streak:</td>
   * <td>image</td>
   * </tr>
   * <tr>
   * <td align="right">media types:</td>
   * <td>image/png</td>
   * </tr>
   * </tbody>
   * </table>
   * <p>A binary PNG image or empty response if no workflow is found.</p>
   *
   *
   * <h3>Status Codes</h3>
   * <table class="hop-table">
   * <tbody>
   * <tr>
   * <th>code</th>
   * <th>description</th>
   * </tr>
   * <tr>
   * <td>200</td>
   * <td>Request was processed.</td>
   * </tr>
   * <tr>
   * <td>500</td>
   * <td>Internal server error occurs during request processing.</td>
   * </tr>
   * </tbody>
   * </table>
   * </div>
   */
  public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
    IOException {
    if ( isJettyMode() && !request.getContextPath().startsWith( CONTEXT_PATH ) ) {
      return;
    }

    if ( log.isDebug() ) {
      logDebug( BaseMessages.getString( PKG, "GetWorkflowImageServlet.Log.WorkflowImageRequested" ) );
    }

    String workflowName = request.getParameter( "name" );
    String id = request.getParameter( "id" );

    // ID is optional...
    //
    Workflow workflow;
    HopServerObjectEntry entry;
    if ( Utils.isEmpty( id ) ) {
      // get the first pipeline that matches...
      //
      entry = getWorkflowMap().getFirstCarteObjectEntry( workflowName );
      if ( entry == null ) {
        workflow = null;
      } else {
        id = entry.getId();
        workflow = getWorkflowMap().getWorkflow( entry );
      }
    } else {
      // Take the ID into account!
      //
      entry = new HopServerObjectEntry( workflowName, id );
      workflow = getWorkflowMap().getWorkflow( entry );
    }

    try {
      if ( workflow != null ) {

        response.setStatus( HttpServletResponse.SC_OK );

        response.setCharacterEncoding( "UTF-8" );
        response.setContentType( "image/png" );

        // Generate xform image
        //
        BufferedImage image = generateJobImage( workflow.getWorkflowMeta() );
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
          ImageIO.write( image, "png", os );
        } finally {
          os.flush();
        }
        response.setContentLength( os.size() );

        OutputStream out = response.getOutputStream();
        out.write( os.toByteArray() );

      }
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  private BufferedImage generateJobImage( WorkflowMeta workflowMeta ) throws Exception {
    float magnification = 1.0f;
    Point maximum = workflowMeta.getMaximum();
    maximum.multiply( magnification );

    SwingGC gc = new SwingGC( null, maximum, 32, 0, 0 );
    WorkflowPainter workflowPainter = new WorkflowPainter( gc, workflowMeta, maximum, null, null, null, null, null, new ArrayList<AreaOwner>(), 32, 1, 0, "Arial", 10, 1.0d );
    workflowPainter.setMagnification( magnification );
    workflowPainter.drawJob();

    BufferedImage image = (BufferedImage) gc.getImage();

    return image;
  }

  public String toString() {
    return "Workflow Image IHandler";
  }

  public String getService() {
    return CONTEXT_PATH + " (" + toString() + ")";
  }

  public String getContextPath() {
    return CONTEXT_PATH;
  }
}
