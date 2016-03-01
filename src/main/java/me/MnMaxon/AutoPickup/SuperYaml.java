package me.MnMaxon.AutoPickup;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SuperYaml {
	private String fileLocation;
	public YamlConfiguration config;

	public SuperYaml(String fileLocation) {
		this.fileLocation = fileLocation;
		reload();
	}

	public void reload() {
		config = Config.Load(fileLocation);
	}

	public void save() {
		Config.Save(config, fileLocation);
	}

	public void set(String path, Object value) {
		if (value instanceof Location)
			setLocation(path, (Location) value);
		else
			config.set(path, value);
	}

	public Object get(String path) {
		return config.get(path);
	}

	public int getInt(String path) {
		return config.getInt(path);
	}

	public Boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	public double getDouble(String path) {
		return config.getDouble(path);
	}

	public ItemStack getItemStack(String path) {
		return config.getItemStack(path);
	}

	public ConfigurationSection getConfigurationSection(String path) {
		return config.getConfigurationSection(path);
	}

	public String getString(String path) {
		return config.getString(path);
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public Location getLocation(String name) {
		if (config.get(name) == null || Bukkit.getWorld(config.getString(name + ".World")) == null)
			return null;
		else {
			Location loc = new Location(Bukkit.getWorld(config.getString(name + ".World")), config.getDouble(name
					+ ".X"), config.getDouble(name + ".Y"), config.getDouble(name + ".Z"),
					(float) config.getDouble(name + ".Yaw"), (float) config.getDouble(name + ".Pitch"));
			return loc.add(0, .1, 0);
		}
	}

	public void setLocation(String name, Location loc) {
		set(name + ".World", loc.getWorld().getName());
		set(name + ".X", loc.getX());
		set(name + ".Y", loc.getY());
		set(name + ".Z", loc.getZ());
		set(name + ".Yaw", loc.getYaw());
		set(name + ".Pitch", loc.getPitch());
	}
}
