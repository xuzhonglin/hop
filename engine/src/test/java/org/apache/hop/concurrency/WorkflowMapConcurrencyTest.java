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

package org.apache.hop.concurrency;

import org.apache.commons.collections.ListUtils;
import org.apache.hop.workflow.Workflow;
import org.apache.hop.workflow.WorkflowConfiguration;
import org.apache.hop.www.HopServerObjectEntry;
import org.apache.hop.www.WorkflowMap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkflowMapConcurrencyTest {
  public static final String JOB_NAME_STRING = "workflow";
  public static final String JOB_ID_STRING = "workflow";
  public static final int INITIAL_JOB_MAP_SIZE = 100;

  private static final int gettersAmount = 20;
  private static final int replaceAmount = 20;
  private static final int updatersAmount = 5;
  private static final int updatersCycles = 10;

  private static WorkflowMap workflowMap;

  @BeforeClass
  public static void init() {
    workflowMap = new WorkflowMap();
    for ( int i = 0; i < INITIAL_JOB_MAP_SIZE; i++ ) {
      workflowMap.addWorkflow( JOB_NAME_STRING + i, JOB_ID_STRING + i, mockJob( i ), mock( WorkflowConfiguration.class ) );
    }
  }

  private static Workflow mockJob( int id ) {
    Workflow workflow = mock( Workflow.class );
    when( workflow.getContainerObjectId() ).thenReturn( JOB_NAME_STRING + id );
    return workflow;
  }

  @Test
  public void updateGetAndReplaceConcurrently() throws Exception {
    AtomicBoolean condition = new AtomicBoolean( true );
    AtomicInteger generator = new AtomicInteger( 10 );

    List<Updater> updaters = new ArrayList<>();
    for ( int i = 0; i < updatersAmount; i++ ) {
      Updater updater = new Updater( workflowMap, generator, updatersCycles );
      updaters.add( updater );
    }

    List<Getter> getters = new ArrayList<>();
    for ( int i = 0; i < gettersAmount; i++ ) {
      getters.add( new Getter( workflowMap, condition ) );
    }

    List<Replacer> replacers = new ArrayList<>();
    for ( int i = 0; i < replaceAmount; i++ ) {
      replacers.add( new Replacer( workflowMap, condition ) );
    }

    //noinspection unchecked
    ConcurrencyTestRunner.runAndCheckNoExceptionRaised( updaters, ListUtils.union( replacers, getters ), condition );

  }

  private static class Getter extends StopOnErrorCallable<Object> {
    private final WorkflowMap workflowMap;
    private final Random random;

    public Getter( WorkflowMap workflowMap, AtomicBoolean condition ) {
      super( condition );
      this.workflowMap = workflowMap;
      this.random = new Random();
    }

    @Override
    public Object doCall() throws Exception {
      while ( condition.get() ) {

        int i = random.nextInt( INITIAL_JOB_MAP_SIZE );
        HopServerObjectEntry entry = workflowMap.getWorkflowObjects().get( i );

        if ( entry == null ) {
          throw new IllegalStateException(
            String.format( "Returned HopServerObjectEntry must not be null. EntryId = %d", i ) );
        }
        final String workflowName = JOB_NAME_STRING + i;

        Workflow workflow = workflowMap.getWorkflow( entry.getName() );
        if ( workflow == null ) {
          throw new IllegalStateException( String.format( "Returned workflow must not be null. Workflow name = %s", workflowName ) );
        }

        WorkflowConfiguration workflowConfiguration = workflowMap.getConfiguration( entry.getName() );
        if ( workflowConfiguration == null ) {
          throw new IllegalStateException(
            String.format( "Returned workflowConfiguration must not be null. Workflow name = %s", workflowName ) );
        }
      }

      return null;
    }
  }


  private static class Updater implements Callable<Exception> {
    private final WorkflowMap workflowMap;
    private final AtomicInteger generator;
    private final int cycles;

    public Updater( WorkflowMap workflowMap, AtomicInteger generator, int cycles ) {
      this.workflowMap = workflowMap;
      this.generator = generator;
      this.cycles = cycles;
    }

    @Override
    public Exception call() throws Exception {
      Exception exception = null;
      try {
        for ( int i = 0; i < cycles; i++ ) {
          int id = generator.get();
          workflowMap.addWorkflow( JOB_NAME_STRING + id, JOB_ID_STRING + id, mockJob( id ), mock( WorkflowConfiguration.class ) );
        }
      } catch ( Exception e ) {
        exception = e;
      }
      return exception;
    }
  }

  private static class Replacer extends StopOnErrorCallable<Object> {
    private final WorkflowMap workflowMap;
    private final Random random;

    public Replacer( WorkflowMap workflowMap, AtomicBoolean condition ) {
      super( condition );
      this.workflowMap = workflowMap;
      this.random = new Random();
    }

    @Override
    public Object doCall() throws Exception {

      int i = random.nextInt( INITIAL_JOB_MAP_SIZE );

      final String workflowName = JOB_NAME_STRING + i;
      final String jobId = JOB_ID_STRING + i;

      HopServerObjectEntry entry = new HopServerObjectEntry( workflowName, jobId );

      workflowMap.replaceWorkflow( entry, mockJob( i + 1 ), mock( WorkflowConfiguration.class ) );

      return null;
    }
  }
}
