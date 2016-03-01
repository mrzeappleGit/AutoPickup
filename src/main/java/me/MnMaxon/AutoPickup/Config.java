package me.MnMaxon.AutoPickup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {
	public static YamlConfiguration Load(String FileLocation) {
        File f=new File(FileLocation);
		if (!f.exists())
			try {
                f.getParentFile().mkdir();
				f.createNewFile();
				Bukkit.getServer().getLogger().log(Level.INFO, "New Config Created at: " + FileLocation);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		YamlConfiguration cfg = new YamlConfiguration();
		try {
			cfg.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return cfg;
	}

	public static void Save(YamlConfiguration cfg, String FileLocation) {
		try {
			cfg.save(new File(FileLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
