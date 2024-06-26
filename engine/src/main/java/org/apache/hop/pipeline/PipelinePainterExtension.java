/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.pipeline;

import java.util.List;
import java.util.Map;
import org.apache.hop.core.gui.AreaOwner;
import org.apache.hop.core.gui.DPoint;
import org.apache.hop.core.gui.IGc;
import org.apache.hop.pipeline.transform.TransformMeta;

public class PipelinePainterExtension {

  public IGc gc;
  public List<AreaOwner> areaOwners;
  public PipelineMeta pipelineMeta;
  public TransformMeta transformMeta;
  public PipelineHopMeta pipelineHop;
  public int x1;
  public int y1;
  public int x2;
  public int y2;
  public int mx;
  public int my;
  public DPoint offset;
  public int iconSize;
  public Map<String, Object> stateMap;

  public PipelinePainterExtension(
      IGc gc,
      List<AreaOwner> areaOwners,
      PipelineMeta pipelineMeta,
      TransformMeta transformMeta,
      PipelineHopMeta pipelineHop,
      int x1,
      int y1,
      int x2,
      int y2,
      int mx,
      int my,
      DPoint offset,
      int iconSize,
      Map<String, Object> stateMap) {
    super();
    this.gc = gc;
    this.areaOwners = areaOwners;
    this.pipelineMeta = pipelineMeta;
    this.transformMeta = transformMeta;
    this.pipelineHop = pipelineHop;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.mx = mx;
    this.my = my;
    this.offset = offset;
    this.iconSize = iconSize;
    this.stateMap = stateMap;
  }
}
