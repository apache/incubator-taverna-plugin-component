/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.component.ui.annotation;

import java.util.Arrays;
import java.util.List;

import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Version;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.port.ActivityPort;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

/**
 * @author David Withers
 */
public class SemanticAnnotationContextualViewFactory implements
		ContextualViewFactory<AbstractNamed> {
	private FileManager fileManager;
	private ComponentFactory factory;

	private WorkflowBundle bundle;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileManager(FileManager fm) {
		this.fileManager = fm;
	}

	@Override
	public boolean canHandle(Object selection) {
		bundle = fileManager.getCurrentDataflow();
		return fileManager.getDataflowSource(bundle) instanceof Version.ID
				&& selection instanceof AbstractNamed
				&& !(selection instanceof Activity || selection instanceof ActivityPort);
	}

	@Override
	public List<ContextualView> getViews(AbstractNamed selection) {
		return Arrays.asList(new SemanticAnnotationContextualView(fileManager,
				factory, selection), new TurtleContextualView(selection, bundle));
	}
}
