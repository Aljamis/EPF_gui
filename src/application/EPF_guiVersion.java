package application;

public class EPF_guiVersion {
	
	private static final String VERSION;
	static {
		if (EPF_guiVersion.class.getPackage().getImplementationVersion() == null)
			VERSION = "Local not version-ized";
		else VERSION = EPF_guiVersion.class.getPackage().getImplementationVersion();
	}
	
	
	private EPF_guiVersion() { }  //  Restrict Instantiation
	
	
	public static String getVersion() {
		return VERSION;
	}
}
