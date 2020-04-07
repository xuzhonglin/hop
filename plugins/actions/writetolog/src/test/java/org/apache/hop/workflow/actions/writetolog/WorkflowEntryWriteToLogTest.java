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
package org.apache.hop.workflow.actions.writetolog;

import org.apache.hop.core.Const;
import org.apache.hop.core.Result;
import org.apache.hop.core.logging.HopLogStore;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.logging.LogLevel;
import org.apache.hop.workflow.Workflow;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Christopher Songer
 */
public class WorkflowEntryWriteToLogTest {

  private Workflow parentWorkflow;
  private ActionWriteToLog jobEntry;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    HopLogStore.init();
  }

  @Before
  public void setUp() throws Exception {
    parentWorkflow = mock( Workflow.class );
    doReturn( false ).when( parentWorkflow ).isStopped();

    jobEntry = new ActionWriteToLog();
    jobEntry = spy( jobEntry );
  }

  @Test
  public void errorMessageIsNotLoggedWhenParentJobLogLevelIsNothing() {
    verifyErrorMessageForParentJobLogLevel( LogLevel.NOTHING, never() );
  }

  @Test
  public void errorMessageIsLoggedWhenParentJobLogLevelIsError() {
    verifyErrorMessageForParentJobLogLevel( LogLevel.ERROR, times( 1 ) );
  }

  @Test
  public void errorMessageIsLoggedWhenParentJobLogLevelIsMinimal() {
    verifyErrorMessageForParentJobLogLevel( LogLevel.MINIMAL, times( 1 ) );
  }

  private void verifyErrorMessageForParentJobLogLevel( LogLevel parentJobLogLevel, VerificationMode mode ) {
    jobEntry.setLogMessage( "TEST" );
    jobEntry.setEntryLogLevel( LogLevel.ERROR );

    doReturn( parentJobLogLevel ).when( parentWorkflow ).getLogLevel();
    jobEntry.setParentWorkflow( parentWorkflow );

    ILogChannel logChannel = spy( jobEntry.createLogChannel() );
    doReturn( logChannel ).when( jobEntry ).createLogChannel();

    jobEntry.evaluate( new Result() );
    verify( logChannel, mode ).logError( "TEST" + Const.CR );
  }
}
