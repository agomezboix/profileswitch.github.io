package fingerprint.distance;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Plugin {
	public String name;
	public String file;
	String description;
	int major;
	int minor;
	String current;

	public Plugin(String name, String file, int major, int minor, String current) {
		super();
		this.name = name;
		this.file = file;
		this.major = major;
		this.minor = minor;
		this.current = current;
		description = "";
	}

	public Plugin(String name, String file, String version) throws Exception {
		super();
		this.name = name;
		this.file = file;
		current = "";
		description = "";
		major = -1;
		minor = -1;
		if (!version.equals("") && !version.equals(" ") && matchVersion(version)) {
			// extract info
			String[] n = version.split("\\.");
				switch (n.length) {
				case 1:
					major = Integer.parseInt(n[0]);
					break;
				case 2:
					major = Integer.parseInt(n[0]);
					minor = Integer.parseInt(n[1]);
					break;
				default:
					major = Integer.parseInt(n[0]);
					minor = Integer.parseInt(n[1]);
					for (int i = 2; i < n.length - 1; i++) {
						current += n[i] + ".";
					}
					current += n[n.length - 1];
					break;
				}
			
		} else {
			major = minor = -1;
			current = "";
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Plugin [name=" + name + ", file=" + file + ", description=" + description + ", major=" + major
				+ ", minor=" + minor + ", current=" + current + "]";
	}

	public boolean equalNames(Plugin plugin) {
		return (plugin.name.equals(name) && plugin.file.equals(file));
	}

	// _.v > plugin.v --> 1 _.v = plugin.v --> 0 _.v < plugin.v --> -1
	public int compareVersion(Plugin plugin) throws Exception {
		if (this.major == -1 && plugin.major == -1) {
			return 0;
		}
		if (this.major == -1) {
			return -1;
		}
		if (plugin.major == -1) {
			return 0;
		}
		int val = 1;

		try {
			if (this.major > plugin.major) {
				val = 1;
			} else if (this.major < plugin.major) {
				val = -1;
			} else {
				if (this.minor > plugin.minor) {
					val = 1;
				} else if (this.minor < plugin.minor) {
					val = -1;
				} else {
					if (this.current.equals(plugin.current)) {
						val = 0;
					} else {

						if (!this.current.equals("") && !plugin.current.equals("")) {
							String[] v1 = (this.current).split("\\.");
							String[] v2 = plugin.current.split("\\.");

							Integer[] c1 = new Integer[v1.length], c2 = new Integer[v2.length];
							for (int i = 0; i < v1.length; i++) {
								c1[i] = Integer.parseInt(v1[i]);
							}
							for (int i = 0; i < v2.length; i++) {
								c2[i] = Integer.parseInt(v2[i]);
							}
							for (int i = 0; i < Math.min(c1.length, c2.length); i++) {
								if (c1[i] > c2[i]) {
									val = 1;
									break;
								} else if (c1[i] < c2[i]) {
									val = -1;
									break;
								}
							}
							if (c1.length == c2.length) {
								val = 0;
							} else if (c1.length > c2.length) {
								val = 1;
							} else {
								val = -1;
							}
						} else {
							if (this.current.equals("") && plugin.current.equals("")) {
								val = 0;
							} else if (this.current.equals("")) {
								val = -1;
							} else {
								val = 1;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(toString());
			System.out.println(plugin.toString());
			e.printStackTrace();
		}
		return val;
	}

	@Override
	public boolean equals(Object o) {
		Plugin p = (Plugin) o;
		boolean eq = false;
		try {
			eq = this.name.equals(p.name) && compareVersion(p) == 0 && this.file.equals(p.file)
					&& this.description.equals(p.description);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eq;
	}

	private Set<Plugin> getListPluginAmIUnique(String line) {
		Set<Plugin> plugins = new TreeSet<Plugin>();
		String[] parts = line.split("Plugin \\d+: ");
		String[] vals;
		String l, name, file = "", version = "";
		for (int k = ((parts[0].equals("") ? 1 : 0)); k < parts.length; k++) {
			l = parts[k];
			if (l.equals("no JS") || l.equals("?")) {
				name = l;
			} else {
				vals = extractInfo(l);
				name = vals[0];
				file = vals[1];
				version = vals[2];
			}
			try {
				plugins.add(new Plugin(name, file, version));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return plugins;
	}

	public static String[] extractInfo(String plugin) {

		// 0->name 1->file 2->version
		String[] info = new String[3];

		String[] fields = plugin.split(";");
		info[0] = info[1] = info[2] = "";
		switch (fields.length) {
		case 1:
			info[0] = plugin;
			break;
		case 2:
			info[0] = extractName(fields[0]);
			info[2] = extractVersion(fields[0]);
			break;
		case 3:
			info[0] = extractName(fields[0]);
			info[1] = extractFile(fields[2]);
			info[2] = extractVersion(fields[0]);
			if ("".equals(info[2])) {
				info[2] = extractVersion(fields[1]);
			}
			break;
		case 4:
			info[0] = extractName(fields[0]);
			info[1] = extractFile(fields[3]);
			info[2] = extractVersion(fields[0]);
			if ("".equals(info[2])) {
				info[2] = extractVersion(fields[2]);
			}
			break;
		}
		return info;
	}

	public static String extractVersion(String string) {
		String[] tmp = string.split("\\s");
		int i = 0;
		while (i < tmp.length && !matchVersion(tmp[i])) {
			i++;
		}
		return (i < tmp.length) ? tmp[i] : "";
	}

	public static String extractFile(String string) {
		if (string.length() > 0) {
			int idx = string.length() - 1;
			while (idx > 0 && string.substring(idx, idx + 1).matches("[^a-zA-Z]")) {
				idx--;
			}
			string = string.substring(0, idx + 1);
		}
		String[] tmp = string.split("\\s");
		int i = 0;
		while (i < tmp.length && !matchFileExt(tmp[i])) {
			i++;
		}
		return (i < tmp.length) ? tmp[i] : "";
	}

	public static String extractName(String string) {
		String[] tmp = string.split(" ");
		String name = "";
		int i = 0;
		while (i < tmp.length && !matchVersion(tmp[i])) {
			i++;
		}
		if (i < tmp.length) {
			int j = 0;
			for (; j < i - 1; j++) {
				name += (tmp[j] + " ");
			}
			name += tmp[j];
		} else {
			name = string;
		}
		return name;
	}

	public static boolean matchVersion(String string) {
		String typeVersion = "[0-9]{1,5}(\\.[0-9]{1,5})*";
		return string.matches(typeVersion);
	}

	public static boolean matchFileExt(String string) {
		String fileExt = "[\\w-]+\\.[a-zA-Z]{2,}";
		return string.matches(fileExt);
	}

}
