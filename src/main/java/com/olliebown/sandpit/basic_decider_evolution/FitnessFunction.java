package com.olliebown.sandpit.basic_decider_evolution;

import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;

public interface FitnessFunction {
	
	public double evaluate(DynamicSystem d);
	
}
