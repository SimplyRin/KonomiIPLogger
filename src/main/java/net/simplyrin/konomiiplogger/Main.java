package net.simplyrin.konomiiplogger;

import java.io.File;
import java.text.Normalizer;
import java.util.Arrays;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.simplyrin.config.Config;
import net.simplyrin.konomiiplogger.command.CommandIPLog;
import net.simplyrin.konomiiplogger.event.KonomiIPCheckEvent;
import net.simplyrin.konomiiplogger.event.KonomiIPInfo;
import net.simplyrin.konomiiplogger.utils.MySQLManager;
import net.simplyrin.konomiiplogger.utils.Request;

/**
 * Created by SimplyRin on 2018/11/30.
 *
 * Copyright (C) 2018 SimplyRin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Main extends Plugin implements Listener {

	@Getter
	private Configuration config;
	@Getter
	private Configuration userData;

	private File userDataFile;

	private boolean consoleDebug;
	private boolean useMySQL = false;
	private MySQLManager mySQLManager;

	@Override
	public void onEnable() {
		File folder = this.getDataFolder();
		if (!folder.exists()) {
			folder.mkdirs();
		}

		File file = new File(folder, "config.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
			}

			Configuration config = Config.getConfig(file);

			config.set("Console-Debug", false);
			config.set("Whitelist.Enabled", false);
			config.set("Whitelist.CountryCode", Arrays.asList("JP"));
			config.set("Whitelist.Message", "&c&lYou can not access the server from your country!");

			config.set("MySQL.Use", false);

			config.set("MySQL.Username", "root");
			config.set("MySQL.Password", "password");
			config.set("MySQL.Address", "localhost:3306");
			config.set("MySQL.Database", "database");
			config.set("MySQL.Table", "core");
			config.set("MySQL.Timezone", "JST");
			config.set("MySQL.UseSSL", false);

			Config.saveConfig(config, file);
		}

		this.userDataFile = new File(folder, "userdata.yml");
		if (!this.userDataFile.exists()) {
			try {
				this.userDataFile.createNewFile();
			} catch (Exception e) {
			}
		}

		this.config = Config.getConfig(file);
		this.userData = Config.getConfig(this.userDataFile);

		this.consoleDebug = this.config.getBoolean("Console-Debug");
		if (this.consoleDebug) {
			this.sendMessage("[KonomiIPLogger] &cConsole notification enabled!");
		}
		if (this.config.getBoolean("MySQL.Use")) {
			this.sendMessage("[KonomiIPLogger] &aUsing Database: &lMySQL");

			this.useMySQL = true;
			this.mySQLManager = new MySQLManager(this);
		} else {
			this.sendMessage("[KonomiIPLogger] &aUsing Database: &lConfiguration");
		}

		this.getProxy().getPluginManager().registerListener(this, this);
		this.getProxy().getPluginManager().registerCommand(this, new CommandIPLog(this));
	}

	@Override
	public void onDisable() {
		if (this.useMySQL) {
			this.mySQLManager.getEditor().getMySQL().disconnect();
		}
	}

	@EventHandler
	public void onLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();

		String address = player.getAddress().getHostString();
		if (address.startsWith("127.")) {
			return;
		}

		new Request(this, player).postRequest();
	}

	@EventHandler
	public void onIPCheck(KonomiIPCheckEvent event) {
		ProxiedPlayer player = event.getPlayer();
		KonomiIPInfo info = event.getIpInfo();

		if (this.consoleDebug) {
			this.sendMessage("Callback... : " + player.getName());
		}

		if (!this.config.getStringList("Whitelist.CountryCode").contains(info.getCountryCode()) && this.config.getBoolean("Whitelist.Enabled")) {
			player.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', this.config.getString("Whitelist.Kick-Message"))));
		}

		String key = player.getUniqueId().toString();

		this.set("Name." + player.getName().toLowerCase() + ".UUID", key);

		this.set("UUID." + key + ".Name", player.getName());
		this.set("UUID." + key + ".As", info.getAs());
		this.set("UUID." + key + ".City", info.getCity());
		this.set("UUID." + key + ".Country", info.getCountry());
		this.set("UUID." + key + ".CountryCode", info.getCountryCode());
		this.set("UUID." + key + ".Isp", info.getIsp());
		this.set("UUID." + key + ".Lat", String.valueOf(info.getLat()));
		this.set("UUID." + key + ".Lon", String.valueOf(info.getLon()));
		this.set("UUID." + key + ".Org", info.getOrg());
		this.set("UUID." + key + ".Query", info.getQuery());
		this.set("UUID." + key + ".Region", info.getRegion());
		this.set("UUID." + key + ".RegionName", info.getRegionName());
		this.set("UUID." + key + ".Status", info.getStatus());
		this.set("UUID." + key + ".Timezone", info.getTimezone());
		this.set("UUID." + key + ".Zip", info.getZip());

		if (!this.useMySQL) {
			if (this.consoleDebug) {
				this.sendMessage("&aSaving config (userdata.yml)");
			}
			Config.saveConfig(this.userData, this.userDataFile);
		}
	}

	public String stripAccents(String value) {
		value = Normalizer.normalize(value, Normalizer.Form.NFD);
		value = value.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return value;
	}

	public void sendMessage(String message) {
		this.getProxy().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public void sendMessage(ProxiedPlayer sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public String getString(String key) {
		String value;
		if (this.useMySQL) {
			value = this.mySQLManager.getEditor().get(key);
		} else {
			value = this.userData.getString(key);
		}

		if (this.consoleDebug) {
			this.sendMessage("GET " + (this.useMySQL ? "(MYSQL)" : "(CONFIG)") + ": " + key + " -> " + value);
		}
		return value;
	}

	public void set(String key, String value) {
		value = this.stripAccents(value);

		if (this.consoleDebug) {
			this.sendMessage("SET " + (this.useMySQL ? "(MYSQL)" : "(CONFIG)") + ": " + key + " -> " + value);
		}

		if (this.useMySQL) {
			this.mySQLManager.getEditor().set(key, value);
		} else {
			this.userData.set(key, value);
		}
	}

}
