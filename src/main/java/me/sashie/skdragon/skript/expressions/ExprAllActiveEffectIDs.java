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

package me.sashie.skdragon.skript.expressions;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.sashie.skdragon.EffectAPI;

import java.util.Set;

/**
 * Created by Sashie on 12/12/2016.
 */

@Name("Particles - All active effects")
@Description({"Lists all currently running effects"})
@Examples({	"set {list::*} to all active particle effects"})
public class ExprAllActiveEffectIDs extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprAllActiveEffectIDs.class, String.class, ExpressionType.SIMPLE,
				"[all] active particle effects");
	}

	@Override
	public boolean init(Expression<?>[] args, int arg1, Kleenean arg2, ParseResult arg3) {
		return true;
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		Set<String> effectIDs = EffectAPI.ALL_EFFECTS.keySet();
		return effectIDs.toArray(new String[effectIDs.size()]);
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "all active particle effects";
	}

	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}
}