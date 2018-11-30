package net.simplyrin.konomiiplogger.utils;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.simplyrin.konomiiplogger.Main;
import net.simplyrin.konomiiplogger.utils.MySQL.Editor;

/**
 * Created by SimplyRin on 2018/09/04.
 *
 * Copyright (c) 2018 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class MySQLManager {

	private Main instance;

	@Getter
	private Editor editor;

	public MySQLManager(Main instance) {
		this.instance = instance;

		this.loginToMySQL();
	}

	public void loginToMySQL() {
		Configuration config = this.instance.getConfig();

		MySQL mySQL = new MySQL(config.getString("MySQL.Username"), config.getString("MySQL.Password"));
		mySQL.setAddress(config.getString("MySQL.Address"));
		mySQL.setDatabase(config.getString("MySQL.Database"));
		mySQL.setTable(config.getString("MySQL.Table"));
		mySQL.setTimezone(config.getString("MySQL.Timezone"));
		mySQL.setUseSSL(config.getBoolean("MySQL.UseSSL"));

		try {
			this.editor = mySQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.instance.getProxy().getScheduler().runAsync(this.instance, () -> {
			this.autoReconnect();
		});
	}

	public void autoReconnect() {
		try {
			TimeUnit.MINUTES.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			this.editor = this.editor.getMySQL().reconnect();
		} catch (SQLException e) {
			this.autoReconnect();
			return;
		}
		this.autoReconnect();
	}

}
