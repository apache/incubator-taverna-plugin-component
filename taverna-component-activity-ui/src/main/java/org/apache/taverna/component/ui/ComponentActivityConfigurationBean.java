package org.apache.taverna.component.ui;

import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.COMPONENT_NAME;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.COMPONENT_VERSION;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static org.apache.taverna.component.ui.ComponentConstants.ACTIVITY_URI;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.config.ComponentPropertyNames;
import org.apache.taverna.component.api.profile.ExceptionHandling;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Component activity configuration bean.
 */
public class ComponentActivityConfigurationBean extends Version.Identifier {
	public static final String ERROR_CHANNEL = "error_channel";
	public static final List<String> ignorableNames = Arrays
			.asList(ERROR_CHANNEL);
	private static final long serialVersionUID = 5774901665863468058L;
	private static final Logger logger = getLogger(ComponentActivityConfigurationBean.class);

	private ActivityPortsDefinitionBean ports = null;
	private ComponentFactory factory;
	private ExceptionHandling eh;

	public ComponentActivityConfigurationBean(Version.ID toBeCopied,
			ComponentFactory factory) {
		super(toBeCopied.getRegistryBase(), toBeCopied.getFamilyName(),
				toBeCopied.getComponentName(), toBeCopied.getComponentVersion());
		this.factory = factory;
		try {
			getPorts();
		} catch (ComponentException e) {
			logger.error("failed to get component realization", e);
		}
	}

	public ComponentActivityConfigurationBean(JsonNode json,
			ComponentFactory factory) throws MalformedURLException {
		super(getUrl(json), getFamily(json), getComponent(json),
				getVersion(json));
		this.factory = factory;
	}

	public ComponentActivityConfigurationBean(Configuration configuration,
			ComponentFactory factory) throws MalformedURLException {
		this(configuration.getJson(), factory);
	}

	private static URL getUrl(JsonNode json) throws MalformedURLException {
		return new URL(json.get(REGISTRY_BASE).textValue());
	}

	private static String getFamily(JsonNode json) {
		return json.get(FAMILY_NAME).textValue();
	}

	private static String getComponent(JsonNode json) {
		return json.get(COMPONENT_NAME).textValue();
	}

	private static Integer getVersion(JsonNode json) {
		JsonNode node = json.get(COMPONENT_VERSION);
		if (node == null || !node.isInt())
			return null;
		return node.intValue();
	}

	public Component getComponent() throws ComponentException {
		return factory.getComponent(getRegistryBase(), getFamilyName(),
				getComponentName());
	}

	public Version getVersion() throws ComponentException {
		return factory.getVersion(this);
	}

	private ActivityPortsDefinitionBean getPortsDefinition(WorkflowBundle w) {
		ActivityPortsDefinitionBean result = new ActivityPortsDefinitionBean();

		for (InputWorkflowPort iwp : w.getMainWorkflow().getInputPorts())
			result.inputs.add(makeInputDefinition(iwp));
		for (OutputWorkflowPort owp : w.getMainWorkflow().getOutputPorts())
			result.outputs.add(makeOutputDefinition(getDepth(owp), owp.getName()));

		try {
			eh = factory.getFamily(getRegistryBase(), getFamilyName())
					.getComponentProfile().getExceptionHandling();
			if (eh != null)
				result.outputs.add(makeOutputDefinition(1, ERROR_CHANNEL));
		} catch (org.apache.taverna.component.api.ComponentException e) {
			logger.error("failed to get exception handling for family", e);
		}
		return result;
	}

	private int getDepth(OutputWorkflowPort owp) {
		return 0; //FIXME How to get the depth of an output?
	}

	private InputActivityPort makeInputDefinition(InputWorkflowPort dip) {
		InputActivityPort port = new InputActivityPort();
		port.setName(dip.getName());
		port.setDepth(dip.getDepth());
		return port;
	}

	private OutputActivityPort makeOutputDefinition(int depth, String name) {
		OutputActivityPort port = new OutputActivityPort();
		port.setName(name);
		port.setDepth(depth);
		port.setGranularDepth(depth);
		return port;
	}

	/**
	 * @return the ports
	 */
	public ActivityPortsDefinitionBean getPorts() throws ComponentException {
		if (ports == null)
			ports = getPortsDefinition(getVersion().getImplementation());
		return ports;
	}

	public ExceptionHandling getExceptionHandling() {
		return eh;
	}

	public void installConfiguration(Activity a) {
		Configuration conf = a.createConfiguration(ACTIVITY_URI);
		ObjectNode json = conf.getJsonAsObjectNode();
		json.put(REGISTRY_BASE, getRegistryBase().toExternalForm());
		json.put(FAMILY_NAME, getFamilyName());
		json.put(COMPONENT_NAME, getComponentName());
		json.put(COMPONENT_VERSION, getComponentVersion());
	}

	public static class ActivityPortsDefinitionBean {
		public List<InputActivityPort> inputs = new ArrayList<>();
		public List<OutputActivityPort> outputs = new ArrayList<>();
	}
}