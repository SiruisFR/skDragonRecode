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

package me.sashie.skdragon.skript.expressions.uncommon;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import me.sashie.skdragon.effects.EffectData;
import me.sashie.skdragon.effects.EffectProperty;
import me.sashie.skdragon.effects.properties.IAxis;
import me.sashie.skdragon.particles.Value3d;
import me.sashie.skdragon.skript.expressions.CustomEffectPropertyExpression;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Sashie on 12/12/2016.
 */
@Name("Particles - Effect rotation axis")
@Description({"Rotates specific effects using a vector"})
@Examples({"set rotation axis of effect \"uniqueID\" to {_v}"})
public class ExprEffectRotation extends CustomEffectPropertyExpression<Vector> {

	static {
		register(ExprEffectRotation.class, Vector.class, "rotation axis");
	}

	@Override
	public Vector getPropertyValue(EffectData effect) {
		if (effect instanceof IAxis) {
			Value3d axis = ((IAxis) effect).getAxisProperty().getAxis();
			return new Vector(axis.getX(), axis.getY(), axis.getZ());
		}
		return null;
	}

	@Override
	public void setPropertyValue(EffectData effect, Object[] delta) {
		if (effect instanceof IAxis) {
			Vector v = (Vector) (delta[0]);
			((IAxis) effect).getAxisProperty().setAxis(v);
		}
	}

	@Override
	public @NotNull Class<? extends Vector> getReturnType() {
		return Vector.class;
	}

	@Override
	public String getPropertyName() {
		return "rotation axis";
	}

	@Override
	protected EffectProperty getEffectProperty() {
		return EffectProperty.AXIS;
	}
}
