package net.simplyrin.konomiiplogger.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by SimplyRin on 2018/12/17.
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
@Getter
@AllArgsConstructor
public class KonomiIPInfo {

	private ProxiedPlayer player;
	private String as;
	private String city;
	private String country;
	private String countryCode;
	private String isp;
	private double lat;
	private double lon;
	private String org;
	private String query;
	private String region;
	private String regionName;
	private String status;
	private String timezone;
	private String zip;

}
