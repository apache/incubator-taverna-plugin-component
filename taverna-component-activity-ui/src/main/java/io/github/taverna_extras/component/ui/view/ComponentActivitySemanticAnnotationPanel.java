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

package io.github.taverna_extras.component.ui.view;

import static java.awt.Color.GRAY;
import static java.awt.Color.WHITE;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.SOUTHEAST;
import static java.lang.String.format;
import static io.github.taverna_extras.component.ui.annotation.SemanticAnnotationUtils.getDisplayName;
import static io.github.taverna_extras.component.ui.annotation.SemanticAnnotationUtils.getObjectName;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import io.github.taverna_extras.component.api.profile.SemanticAnnotationProfile;

import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Statement;

public class ComponentActivitySemanticAnnotationPanel extends JPanel {
	private static final long serialVersionUID = 3599768150252711758L;
	private static final String ANNTYPE_MSG = "Annotation type : %s";
	private static final String NONE_MSG = "No semantic annotations";	
	private SemanticAnnotationProfile profile;
	private final Set<Statement> statements;

	public ComponentActivitySemanticAnnotationPanel(
			SemanticAnnotationProfile profile, Set<Statement> statements) {
		this.profile = profile;
		this.statements = statements;
		initialize();
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		setBorder(new AbstractBorder() {
			private static final long serialVersionUID = -5921448975807056953L;

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y,
					int width, int height) {
				g.setColor(GRAY);
				g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = SOUTHEAST;
		c.fill = BOTH;
		c.weightx = 1;
		c.gridx = 0;

		OntProperty predicate = profile.getPredicate();
		c.gridwidth = 2;
		JLabel label = new JLabel(format(ANNTYPE_MSG, getDisplayName(predicate)));
		label.setBorder(new EmptyBorder(5, 5, 5, 5));
		label.setBackground(WHITE);
		label.setOpaque(true);
		add(label, c);

		c.insets = new Insets(5, 7, 0, 0);
		c.anchor = EAST;
		c.fill = HORIZONTAL;
		if (statements.isEmpty()) {
			c.gridwidth = 2;
			// c.weightx = 1;
			// c.gridy++;
			add(new JLabel(NONE_MSG), c);
		} else {
			c.gridwidth = 1;
			for (Statement statement : statements) {
				c.gridx = 0;
				c.weightx = 1;
				JTextArea value = new JTextArea(getObjectName(statement));
				value.setBackground(WHITE);
				value.setOpaque(true);
				value.setBorder(new EmptyBorder(2, 4, 2, 4));
				add(value, c);
			}
		}
	}
}
