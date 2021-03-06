package io.github.taverna_extras.component.profile;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.List;

import io.github.taverna_extras.component.api.profile.PortProfile;
import io.github.taverna_extras.component.api.profile.SemanticAnnotationProfile;

import io.github.taverna_extras.component.api.profile.doc.Port;
import io.github.taverna_extras.component.api.profile.doc.SemanticAnnotation;

/**
 * Specifies the semantic annotations that a port must have.
 * 
 * @author David Withers
 */
public class PortProfileImpl implements PortProfile {
	private final ComponentProfileImpl componentProfile;
	private final Port port;

	public PortProfileImpl(ComponentProfileImpl componentProfile, Port port) {
		this.componentProfile = componentProfile;
		this.port = port;
	}

	@Override
	public List<SemanticAnnotationProfile> getSemanticAnnotations() {
		List<SemanticAnnotationProfile> saProfiles = new ArrayList<>();
		for (SemanticAnnotation annotation : port.getSemanticAnnotation())
			saProfiles.add(new SemanticAnnotationProfileImpl(componentProfile,
					annotation));
		return saProfiles;
	}

	@Override
	public String toString() {
		return "PortProfile \n  SemanticAnnotations : "
				+ getSemanticAnnotations();
	}
}
