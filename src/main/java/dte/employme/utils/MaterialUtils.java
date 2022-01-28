package dte.employme.utils;

import static dte.employme.utils.java.Predicates.negate;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static org.bukkit.Material.BARRIER;
import static org.bukkit.Material.DEBUG_STICK;
import static org.bukkit.Material.JIGSAW;
import static org.bukkit.Material.KNOWLEDGE_BOOK;
import static org.bukkit.Material.SPAWNER;
import static org.bukkit.Material.STRUCTURE_BLOCK;
import static org.bukkit.Material.STRUCTURE_VOID;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.bukkit.Material;

import com.google.common.collect.Sets;

public class MaterialUtils 
{
	//Container of static methods
	private MaterialUtils(){}
	
	private static final Set<Material> UNOBTAINABLE = Sets.newHashSet(BARRIER, JIGSAW, STRUCTURE_BLOCK, STRUCTURE_VOID, SPAWNER, DEBUG_STICK, KNOWLEDGE_BOOK);
	
	private static final Set<Material> OBTAINABLES = Arrays.stream(Material.values())
			.filter(Material::isItem)
			.filter(negate(Material::isAir))
			.filter(negate(UNOBTAINABLE::contains))
			.collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
	
	public static boolean isObtainable(Material material) 
	{
		return OBTAINABLES.contains(material);
	}
}
