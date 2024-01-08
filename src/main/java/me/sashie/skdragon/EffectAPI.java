package me.sashie.skdragon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.sashie.skdragon.util.pool.ObjectPoolManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import me.sashie.skdragon.debug.SkriptNode;
import me.sashie.skdragon.effects.EffectData;
import me.sashie.skdragon.effects.EffectProperty;
import me.sashie.skdragon.effects.TargetEffect;
import me.sashie.skdragon.effects.ParticleEffect;
import me.sashie.skdragon.debug.ParticleException;
import me.sashie.skdragon.runnable.EffectRunnable;
import me.sashie.skdragon.runnable.RunnableType;
import me.sashie.skdragon.util.DynamicLocation;

/**
 * Created by Sashie on 12/12/2016.
 */


public class EffectAPI {

	/**
	 * Determine if this effect has a specific property
	 * 
	 * @return Whether it has the property or not
	 */
	public static boolean hasProperty(EffectData effect, EffectProperty property) {
		for(EffectProperty p: effect.getEffectProperties()){
			if(p.equals(property))
				return true;
		}
		return false;
	}

	/**
	 * Gets a list of properties for this effect
	 * 
	 * @return List of properties
	 */
	public static List<EffectProperty> getProperties(EffectData effect) {
		return Arrays.asList(effect.getEffectProperties());
	}

	public static HashMap<String, EffectData> ALL_EFFECTS = new HashMap<>();
	public static HashMap<String, Integer> ACTIVE_RUNNABLES = new HashMap<>();

	/**
	 * Registers an effect
	 * 
	 * @param id Id of the effect
	 * @param particle The type of particle
	 * @param location Location of the effect
	 */
	//public void register(String id, ParticleEffect[] particle, DynamicLocation[] location) {
	//	register(id, effect, particle, location);
	//}

	/**
	 * Registers an effect with a target location
	 * 
	 * @param id Id of the effect
	 * @param particle The type of particle
	 * @param location Start location of the effect
	 * @param target Location of the target
	 */
	//public void register(String id, ParticleEffect[] particles, DynamicLocation[] location, DynamicLocation target) {
	//	register(id, effect, particles, location, target);
	//}

	/**
	 * Static method for registering an effect externally
	 *  
	 * @param id Id of the effect
	 * @param effectName The effect to register
	 */
	public static EffectData register(String id, ParticleEffect effectName, SkriptNode skriptNode) {
		EffectData effect = effectName.getEffectData();
		return set(id, effect, skriptNode);
	}

	private static EffectData set(String id, EffectData effect, SkriptNode skriptNode) {
		if (ALL_EFFECTS.containsKey(id)) {
			EffectData data = ALL_EFFECTS.get(id);
			synchronized (data) {
				//if (isRunning(id)) {
				if (ACTIVE_RUNNABLES.containsKey(id)) {
					stop(id, skriptNode);
				}
				releasePools(data);
				ALL_EFFECTS.remove(id);
			}
		}
		ALL_EFFECTS.put(id, effect);

		return effect;
	}

	/**
	 * Unregister an effect
	 * 
	 * @param id Id of the effect
	 */
	public static boolean unregister(String id, SkriptNode skriptNode) {
		if (!ALL_EFFECTS.containsKey(id)) {
			SkDragonRecode.warn("Effect with id '" + id + "' does not exist!", skriptNode);
			return false;
		}
		if (isRunning(id)) {
			stop(id, skriptNode);
			EffectData data = ALL_EFFECTS.get(id);
			releasePools(data);
		}
		ALL_EFFECTS.remove(id);
		return true;
	}

	/**
	 * Unregister all effects
	 */
	public static void unregisterAll() {
		stopAll();
		for (EffectData data : ALL_EFFECTS.values()) {
			releasePools(data);
		}
		ALL_EFFECTS.clear();
	}

	private static void releasePools(EffectData data) {
		for (DynamicLocation location : data.getLocations()) {
			ObjectPoolManager.getDynamicLocationPool().release(location);
		}
		data.onUnregister();
	}

	/**
	 * Gets a registered effect if one exists with the input id
	 * 
	 * @param id Id of the effect
	 * @return The effect
	 */
	public static EffectData get(String id, SkriptNode skriptNode) {
		if (ALL_EFFECTS.containsKey(id)) 
			return ALL_EFFECTS.get(id);
		SkDragonRecode.warn("Effect with id '" + id + "' does not exist!", skriptNode);
		return null;
	}

	/**
	 * General method for starting an effect
	 * 
	 * @param id Id of the effect
	 * @param runType {@link RunnableType RunnableType} to use
	 * @param iterations Number of iterations the effect should run for
	 * @param ticks Amount of ticks per pulse
	 * @param async Whether the effect should run async or not
	 */
	public static void start(String id, RunnableType runType, int iterations, long ticks, boolean async, DynamicLocation[] locations, SkriptNode skriptNode) {
		start(id, runType, iterations, 0, ticks, async, locations, skriptNode);
	}

	/**
	 * General method for starting an effect with a delay before it
	 * 
	 * @param id Id of the effect
	 * @param runType {@link RunnableType RunnableType} to use
	 * @param iterations Number of iterations the effect should run for
	 * @param delay Number of ticks before effect starts only used if RunnableType is 'DELAYED'
	 * @param ticks Amount of ticks per pulse
	 * @param sync Whether the effect should run async or sync
	 * @param locations Locations that an effect will target
	 */
	public static void start(String id, RunnableType runType, int iterations, long delay, long ticks, boolean sync, DynamicLocation[] locations, SkriptNode skriptNode) throws ParticleException {
		EffectData effect = get(id, skriptNode);
		if (effect == null)
			return;	//Error already handled TODO add exception...
		if (effect instanceof TargetEffect)
			throw new ParticleException("A 'TargetEffect' requires a target location, effect not started", skriptNode);

		effect.setLocations(locations);
		effect.saveStartLocations();

		if (effect.getLocations() == null || effect.getLocations().length == 0)
			throw new ParticleException("No location provided for effect " + /*'" + effect.getName() + "'*/ "with id  '" + id + "'", skriptNode);

		start(id, effect, runType, iterations, delay, ticks, sync, skriptNode);
	}

	/**
	 * General method for starting an effect with a delay before it
	 *
	 * @param id Id of the effect
	 * @param runType {@link RunnableType RunnableType} to use
	 * @param iterations Number of iterations the effect should run for
	 * @param delay Number of ticks before effect starts only used if RunnableType is 'DELAYED'
	 * @param ticks Amount of ticks per pulse
	 * @param sync Whether the effect should run async or sync
	 * @param locations Locations that an effect will target
	 * @param targets Targets locations that a {@link TargetEffect TargetEffect} effect will target
	 */
	public static void start(String id, RunnableType runType, int iterations, long delay, long ticks, boolean sync, DynamicLocation[] locations, DynamicLocation[] targets, SkriptNode skriptNode) throws ParticleException {
		EffectData effect = get(id, skriptNode);
		if (effect == null)
			return;	//Error already handled

		effect.setLocations(locations);
		effect.saveStartLocations();

		if (!(effect instanceof TargetEffect))
			throw new ParticleException("Only a 'TargetEffect' can have a target location, " + /* '" + effect.getName() + "'*/ " effect with id '" + id + "' not started", skriptNode);

		((TargetEffect) effect).setTargets(targets);

		if (effect.getLocations() == null || effect.getLocations().length == 0)
			throw new ParticleException("No location provided for effect " + /*'" + effect.getName() + "'*/ "with id  '" + id + "'", skriptNode);

		start(id, effect, runType, iterations, delay, ticks, sync, skriptNode);
	}

	private static void start(String id, EffectData effect, RunnableType runType, int iterations, long delay, long ticks, boolean sync, SkriptNode skriptNode) throws ParticleException {
		if (!isRunning(id)) {
			EffectRunnable runnable = new EffectRunnable(effect, iterations);

			switch (runType) {
				case INSTANT:
					if (!sync)
						runnable.runTaskAsynchronously();
					else
						runnable.runTask();
					break;
				case DELAYED:
					if (!sync)
						runnable.runTaskLaterAsynchronously(delay);
					else
						runnable.runTaskLater(delay);
					break;
				case REPEATING:
					if (!sync) {
						runnable.runTaskTimerAsynchronously(delay, ticks);
					} else
						runnable.runTaskTimer(delay, ticks);
					break;
			}

			ACTIVE_RUNNABLES.put(id, runnable.getTaskId());
		} else {
			throw new ParticleException("Effect " + /*'" + effect.getName() + "'*/ "with id '" + id + "' is already running", skriptNode);
		}
	}

	private static void restart(String id, RunnableType runType, int iterations, long delay, long ticks, boolean sync, SkriptNode skriptNode) throws ParticleException {
		//Reset the effect if any start types are changed or if the effect isn't running
		EffectData effect = get(id, skriptNode);
		if (effect == null)
			return;	//Error already handled

		if (!isRunning(id)) {
			EffectRunnable runnable = new EffectRunnable(effect, iterations);

			switch (runType) {
				case INSTANT:
					if(!sync)
						runnable.runTaskAsynchronously();
					else
						runnable.runTask();
					break;
				case DELAYED:
					if (!sync)
						runnable.runTaskLaterAsynchronously(delay);
					else
						runnable.runTaskLater(delay);
					break;
				case REPEATING:
					if (!sync)
						runnable.runTaskTimerAsynchronously(delay, ticks);
					else
						runnable.runTaskTimer(delay, ticks);
					break;
			}
			ACTIVE_RUNNABLES.put(id, runnable.getTaskId());
		} else {
			throw new ParticleException("Effect " + /*'" + effect.getName() + "'*/ "with id '" + id + "' is already running", skriptNode);
		}
	}

	/**
	 * Stops an effect if one exists
	 * 
	 * @param id Id of the effect
	 */
	public static void stop(String id, SkriptNode skriptNode) {
		Integer taskId = ACTIVE_RUNNABLES.get(id);
		if (taskId != null) {
			Bukkit.getScheduler().cancelTask(taskId);
			ACTIVE_RUNNABLES.remove(id);
		} else {
		 	SkDragonRecode.warn("Effect with id '" + id + "' does not exist!", skriptNode);
		}
	}

	/**
	 * Stops all effects
	 */
	public static void stopAll() {
		if (ACTIVE_RUNNABLES == null || ACTIVE_RUNNABLES.isEmpty())
			return;

		for (Integer taskId : ACTIVE_RUNNABLES.values()) {
			Bukkit.getScheduler().cancelTask(taskId);
		}
		ACTIVE_RUNNABLES.clear();
	}

	/**
	 * Checks whether an effect is running or not
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isRunning(String id) {
		Integer taskId = ACTIVE_RUNNABLES.get(id);
		if (taskId != null) {
			return true; // Bukkit.getScheduler().isCurrentlyRunning(taskId); // for some reason this returns false on running effect?
		}
		return false;
	}
}