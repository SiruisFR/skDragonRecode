/*
	This file is part of skDragon - A Skript addon
      
	Copyright (C) 2016 - 2021  Sashie

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package me.sashie.skdragon.skript.conditions;

import javax.annotation.Nullable;

import ch.njol.skript.Skript;
import me.sashie.skdragon.particles.ParticleProperty;
import org.bukkit.Particle;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;

/**
 * Created by Sashie on 12/12/2016.
 */
@Name("Particles - Requires material")
@Description("Checks whether a particle requires material.")
@Examples({"particle redstone requires material #returns false"})
public class CondParticleRequiresData extends Condition {

	static {
		Skript.registerCondition(CondParticleRequiresData.class, "particle %particle% requires material", "particle %particle% does not require material");
	}

	private Expression<Particle> particle;

	@SuppressWarnings({"unchecked"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		particle = (Expression<Particle>) exprs[0];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(final Event e) {
		return particle.check(e, new Checker<Particle>() {
			@Override
			public boolean check(final Particle p) {
				return ParticleProperty.REQUIRES_DATA.hasProperty(p);
			}
		}, isNegated());
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return particle.toString(e, debug) + " particle " + (isNegated() ? "does not require" : "requires") + " material";
	}
}