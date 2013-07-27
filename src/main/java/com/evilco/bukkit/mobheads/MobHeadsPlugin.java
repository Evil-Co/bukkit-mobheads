/**
 * This file is part of Mob Heads.
 *
 * Copyright (C) 2013 Evil-Co <http://www.evil-co.com>
 * Mob Heads is licensed under the GNU Lesser General Public License.
 *
 * Mob Heads is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.evilco.bukkit.mobheads;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author			Johannes Donath <johannesd@evil-co.com>
 * @copyright		(C) 2013 Evil-Co <http://www.evil-co.com>
 * @license			GNU Lesser General Public License <http://www.gnu.org/licenses/lgpl.txt>
 * @package			com.evilco.bukkit.mobheads
 */
public class MobHeadsPlugin extends JavaPlugin implements Listener {
	
	/**
	 * Stores the plugin version.
	 */
	public static final String VERSION_STRING;
	
	/**
	 * Static init
	 */
	static {
		String versionString = null;
		
		// load version
		try {
			Package p = MobHeadsPlugin.class.getPackage();
			versionString = p.getImplementationVersion();
		} catch (Exception ex) { } // ignore
		
		// store
		VERSION_STRING = versionString;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	public void onEnable() {
		this.getLogger().info("Enabling Protective Mob Heads " + (VERSION_STRING != null ? "v" + VERSION_STRING : "(custom) ..."));
		
		// hook events
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	public void onDisable() {
		this.getLogger().info("Disabling Protective Mob Heads " + (VERSION_STRING != null ? "v" + VERSION_STRING : "(custom) ..."));
	}
	
	/**
	 * Handles entity target events.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityTarget(EntityTargetEvent event) {
		// ignore everything else than players
		if (!(event.getTarget() instanceof Player)) return;
		
		// we don't care about players
		// who randomly attach mobs!
		if (event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY) return;
		
		// check type
		switch(event.getEntityType()) {
			case CREEPER:
			case SKELETON:
			case ZOMBIE: break;
			default: return;
		}
		
		// get player instance
		Player player = ((Player) event.getTarget());
		
		// check inventory for head
		if (player.getInventory().getHelmet().getType() != Material.SKULL_ITEM) return;
		
		// get type
		EntityType helmetType = null;
		boolean isWitherSkeleton = false;
		
		// parse data
		switch(player.getInventory().getHelmet().getData().getData()) {
			case 0:
				helmetType = EntityType.SKELETON;
				break;
			case 1:
				helmetType = EntityType.SKELETON;
				isWitherSkeleton = true;
				break;
			case 2:
				helmetType = EntityType.ZOMBIE;
				break;
			case 4:
				helmetType = EntityType.CREEPER;
				break;
			default: return;
		}
		
		// verify against entity type
		if (event.getEntity().getType() != helmetType) return;
		
		// support for wither skeletons
		if (event.getEntity().getType() == EntityType.SKELETON) {
			// convert to skeleton instance
			Skeleton skel = ((Skeleton) event.getEntity());
			
			// verify type
			if (skel.getSkeletonType() == SkeletonType.WITHER && !isWitherSkeleton) return;
			if (skel.getSkeletonType() == SkeletonType.NORMAL && isWitherSkeleton) return;
		}
		
		// cancel event
		event.setCancelled(true);
	}
}
