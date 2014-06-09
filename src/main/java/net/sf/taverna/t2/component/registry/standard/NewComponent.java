package net.sf.taverna.t2.component.registry.standard;

import static java.lang.String.format;
import static net.sf.taverna.t2.component.registry.standard.NewComponentRegistry.logger;
import static net.sf.taverna.t2.component.registry.standard.Policy.getPolicy;
import static net.sf.taverna.t2.component.utils.SystemUtils.getElementString;
import static net.sf.taverna.t2.component.utils.SystemUtils.getValue;

import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.IllegalFormatException;

import net.sf.taverna.t2.component.api.Family;
import net.sf.taverna.t2.component.api.License;
import net.sf.taverna.t2.component.api.Registry;
import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.api.SharingPolicy;
import net.sf.taverna.t2.component.registry.Component;
import net.sf.taverna.t2.component.registry.ComponentVersion;
import net.sf.taverna.t2.component.utils.SystemUtils;
import uk.org.taverna.component.api.ComponentType;
import uk.org.taverna.component.api.Description;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

class NewComponent extends Component {
	static final String ELEMENTS = "title,description";
	static final String EXTRA = "license-type,permissions";

	private final SystemUtils system;
	final NewComponentRegistry registry;
	final NewComponentFamily family;
	private final String id;
	private final String title;
	private final String description;
	private final String resource;

	NewComponent(NewComponentRegistry registry, NewComponentFamily family,
			Description cd, SystemUtils system) throws RegistryException {
		super(cd.getUri());
		this.system = system;
		this.registry = registry;
		this.family = family;
		id = cd.getId().trim();
		title = getElementString(cd, "title");
		description = getElementString(cd, "description");
		resource = cd.getResource();
	}

	NewComponent(NewComponentRegistry registry, NewComponentFamily family,
			ComponentType ct, SystemUtils system) {
		super(ct.getUri());
		this.system = system;
		this.registry = registry;
		this.family = family;
		id = ct.getId().trim();
		title = ct.getTitle().trim();
		description = ct.getDescription().trim();
		resource = ct.getResource();
	}

	public ComponentType getCurrent(String elements) throws RegistryException {
		return registry.getComponentById(id, null, elements);
	}

	@Override
	protected String internalGetName() {
		return title;
	}

	@Override
	protected String internalGetDescription() {
		return description;
	}

	@Override
	protected void populateComponentVersionMap() {
		try {
			for (Description d : getCurrent("versions").getVersions()
					.getWorkflow())
				versionMap.put(d.getVersion(), new Version(d.getVersion(),
						getValue(d)));
		} catch (RegistryException e) {
			logger.warn("failed to retrieve version list: " + e.getMessage());
		}
	}

	@Override
	protected Version internalAddVersionBasedOn(WorkflowBundle bundle,
			String revisionComment) throws RegistryException {
		/*
		 * Only fetch the license and sharing policy now; user might have
		 * updated them on the site and we want to duplicate.
		 */
		ComponentType ct = getCurrent(EXTRA);
		License license = registry.getLicense(getValue(ct.getLicenseType())
				.trim());
		SharingPolicy sharingPolicy = getPolicy(ct.getPermissions());

		return (Version) registry.createComponentVersionFrom(this, title,
				revisionComment, bundle, license, sharingPolicy);
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NewComponent) {
			NewComponent other = (NewComponent) o;
			return registry.equals(other.registry) && id.equals(other.id);
		}
		return false;
	}

	public String getResourceLocation() {
		return resource;
	}

	private static final int BASEHASH = NewComponent.class.hashCode();

	@Override
	public int hashCode() {
		return BASEHASH ^ registry.hashCode() ^ id.hashCode();
	}

	class Version extends ComponentVersion {
		private int version;
		private String description;
		private String location;
		private SoftReference<WorkflowBundle> bundleRef;

		private static final String htmlPageTemplate = "%1$s/workflows/%2$s/versions/%3$s.html";

		protected Version(Integer version, String description, WorkflowBundle bundle) {
			super(NewComponent.this);
			this.version = version;
			this.description = description;
			this.bundleRef = new SoftReference<>(bundle);
		}

		protected Version(Integer version, String description) {
			super(NewComponent.this);
			this.version = version;
			this.description = description;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Version) {
				Version other = (Version) o;
				return version == other.version
						&& NewComponent.this.equals(other.getComponent());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return NewComponent.this.hashCode() ^ (version << 16)
					^ (version >> 16);
		}

		@Override
		protected Integer internalGetVersionNumber() {
			return version;
		}

		@Override
		protected String internalGetDescription() {
			return description;
		}

		private String getLocationUri() throws RegistryException {
			if (location == null)
				location = registry.getComponentById(id, version,
						"content-uri").getContentUri();
			return location;
		}

		@Override
		protected synchronized WorkflowBundle internalGetImplementation()
				throws RegistryException {
			if (bundleRef == null || bundleRef.get() == null) {
				String contentUri = getLocationUri();
				try {
					WorkflowBundle result = system.getBundleFromUri(contentUri
							+ "?version=" + version);
					bundleRef = new SoftReference<>(result);
					return result;
				} catch (Exception e) {
					throw new RegistryException("Unable to open dataflow", e);
				}
			}
			return bundleRef.get();
		}

		@Override
		public URL getHelpURL() {
			try {
				return new URL(format(htmlPageTemplate,
						registry.getRegistryBaseString(), getId(), version));
			} catch (IllegalFormatException | MalformedURLException e) {
				logger.error(e);
				return null;
			}
		}
	}

	@Override
	public Registry getRegistry() {
		return registry;
	}

	@Override
	public Family getFamily() {
		return family;
	}
}
