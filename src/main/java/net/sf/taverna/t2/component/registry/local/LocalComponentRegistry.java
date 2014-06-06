/**
 *
 */
package net.sf.taverna.t2.component.registry.local;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import net.sf.taverna.t2.component.api.Family;
import net.sf.taverna.t2.component.api.License;
import net.sf.taverna.t2.component.api.Profile;
import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.api.SharingPolicy;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.profile.ComponentProfile;
import net.sf.taverna.t2.component.registry.ComponentRegistry;
import net.sf.taverna.t2.component.registry.ComponentUtil;
import net.sf.taverna.t2.component.utils.SystemUtils;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 * 
 */
class LocalComponentRegistry extends ComponentRegistry {
	private static final Logger logger = getLogger(LocalComponentRegistry.class);
	static final String ENC = "utf-8";
	private ComponentUtil util;
	private SystemUtils system;
	private File baseDir;

	@SuppressWarnings("unused")
	private static final String BASE_PROFILE_ID = "http://purl.org/wfever/workflow-base-profile";
	@SuppressWarnings("unused")
	private static final String BASE_PROFILE_FILENAME = "BaseProfile.xml";

	public LocalComponentRegistry(File registryDir, ComponentUtil util, SystemUtils system)
			throws RegistryException {
		super(registryDir);
		baseDir = registryDir;
		this.util = util;
		this.system = system;
	}

	@Override
	public Family internalCreateComponentFamily(String name,
			Profile componentProfile, String description, License license,
			SharingPolicy sharingPolicy) throws RegistryException {
		File newFamilyDir = new File(getComponentFamiliesDir(), name);
		newFamilyDir.mkdirs();
		File profileFile = new File(newFamilyDir, "profile");
		try {
			writeStringToFile(profileFile, componentProfile.getName(), ENC);
		} catch (IOException e) {
			throw new RegistryException("Could not write out profile", e);
		}
		File descriptionFile = new File(newFamilyDir, "description");
		try {
			writeStringToFile(descriptionFile, description, ENC);
		} catch (IOException e) {
			throw new RegistryException("Could not write out description", e);
		}
		return new LocalComponentFamily(this, newFamilyDir, util, system);
	}

	@Override
	protected void populateFamilyCache() throws RegistryException {
		File familiesDir = getComponentFamiliesDir();
		for (File subFile : familiesDir.listFiles()) {
			if (!subFile.isDirectory())
				continue;
			LocalComponentFamily newFamily = new LocalComponentFamily(this,
					subFile, util, system);
			familyCache.put(newFamily.getName(), newFamily);
		}
	}

	@Override
	protected void populateProfileCache() throws RegistryException {
		File profilesDir = getComponentProfilesDir();
		for (File subFile : profilesDir.listFiles())
			if (subFile.isFile() && (!subFile.isHidden())
					&& subFile.getName().endsWith(".xml"))
				try {
					profileCache.add(new ComponentProfile(this,
							subFile.toURI(), util.getBaseProfileLocator()));
				} catch (MalformedURLException e) {
					logger.error("Unable to read profile", e);
				}
	}

	@Override
	protected void internalRemoveComponentFamily(Family componentFamily)
			throws RegistryException {
		try {
			deleteDirectory(new File(getComponentFamiliesDir(),
					componentFamily.getName()));
		} catch (IOException e) {
			throw new RegistryException("Unable to delete component family", e);
		}
	}

	private File getBaseDir() {
		baseDir.mkdirs();
		return baseDir;
	}

	private File getComponentFamiliesDir() {
		File componentFamiliesDir = new File(getBaseDir(), "componentFamilies");
		componentFamiliesDir.mkdirs();
		return componentFamiliesDir;
	}

	private File getComponentProfilesDir() {
		File componentProfilesDir = new File(getBaseDir(), "componentProfiles");
		componentProfilesDir.mkdirs();
		return componentProfilesDir;
	}

	@Override
	public Profile internalAddComponentProfile(Profile componentProfile,
			License license, SharingPolicy sharingPolicy)
			throws RegistryException {
		String name = componentProfile.getName().replaceAll("\\W+", "")
				+ ".xml";
		String inputString = componentProfile.getXML();
		File outputFile = new File(getComponentProfilesDir(), name);
		try {
			writeStringToFile(outputFile, inputString);
		} catch (IOException e) {
			throw new RegistryException("Unable to save profile", e);
		}

		try {
			return new ComponentProfile(this, outputFile.toURI(),
					util.getBaseProfileLocator());
		} catch (MalformedURLException e) {
			throw new RegistryException("Unable to create profile", e);
		}

	}

	@Override
	public int hashCode() {
		return 31 + ((baseDir == null) ? 0 : baseDir.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalComponentRegistry other = (LocalComponentRegistry) obj;
		if (baseDir == null)
			return (other.baseDir == null);
		return baseDir.equals(other.baseDir);
	}

	@Override
	public void populatePermissionCache() {
		return;
	}

	@Override
	public void populateLicenseCache() {
		return;
	}

	@Override
	public License getPreferredLicense() {
		return null;
	}

	@Override
	public Set<Version.ID> searchForComponents(String prefixString, String text)
			throws RegistryException {
		throw new RegistryException("Local registries cannot be searched yet");
	}

	@Override
	public String getRegistryTypeName() {
		return "File System";
	}
}
