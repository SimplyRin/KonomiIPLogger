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
import net.simplyrin.konomiiplogger.utils.MySQLManager;
import net.simplyrin.konomiiplogger.utils.Request;
import net.simplyrin.konomiiplogger.utils.Request.Result;

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
public class Main extends Plugin implements Listener, Request.Callback {

	@Getter
	private Configuration config;

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
			config.set("Whitelist-CountryCode", Arrays.asList("JP"));
			config.set("Kick-Message", "&c&lYou can not access the server from your country!");

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

		this.config = Config.getConfig(file);
		this.consoleDebug = this.getConfig().getBoolean("Console-Debug");
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
		File folder = this.getDataFolder();
		if (!folder.exists()) {
			folder.mkdirs();
		}

		File file = new File(folder, "config.yml");

		Config.saveConfig(this.config, file);

		if (this.useMySQL) {
			this.mySQLManager.getEditor().getMySQL().disconnect();
		}
	}

	@EventHandler
	public void onLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();

		new Request(player).postRequest(this);
	}

	@Override
	public void onDone(Result result) {
		ProxiedPlayer player = result.getPlayer();

		if (this.consoleDebug) {
			this.sendMessage("Callback... : " + player.getName());
		}

		if (!this.config.getStringList("Whitelist-CountryCode").contains(result.getCountryCode())) {
			player.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', this.config.getString("Kick-Message"))));
		}

		String key = player.getUniqueId().toString();

		this.set("Name." + player.getName().toLowerCase() + ".UUID", key);

		this.set("UUID." + key + ".Name", player.getName());
		this.set("UUID." + key + ".As", result.getAs());
		this.set("UUID." + key + ".City", result.getCity());
		this.set("UUID." + key + ".Country", result.getCountry());
		this.set("UUID." + key + ".CountryCode", result.getCountryCode());
		this.set("UUID." + key + ".Isp", result.getIsp());
		this.set("UUID." + key + ".Lat", String.valueOf(result.getLat()));
		this.set("UUID." + key + ".Lon", String.valueOf(result.getLon()));
		this.set("UUID." + key + ".Org", result.getOrg());
		this.set("UUID." + key + ".Query", result.getQuery());
		this.set("UUID." + key + ".Region", result.getRegion());
		this.set("UUID." + key + ".RegionName", result.getRegionName());
		this.set("UUID." + key + ".Status", result.getStatus());
		this.set("UUID." + key + ".Timezone", result.getTimezone());
		this.set("UUID." + key + ".Zip", result.getZip());
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
			value = this.config.getString(key);
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
		}
		this.config.set(key, value);
	}

}
