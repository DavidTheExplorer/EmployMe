package dte.employme.utils;

import static com.cryptomorin.xseries.XMaterial.LIGHT;
import static dte.employme.utils.java.Predicates.negate;
import static java.util.stream.Collectors.toSet;
import static org.bukkit.Material.BARRIER;
import static org.bukkit.Material.BEDROCK;
import static org.bukkit.Material.COMMAND_BLOCK;
import static org.bukkit.Material.DEBUG_STICK;
import static org.bukkit.Material.JIGSAW;
import static org.bukkit.Material.KNOWLEDGE_BOOK;
import static org.bukkit.Material.SPAWNER;
import static org.bukkit.Material.STRUCTURE_BLOCK;
import static org.bukkit.Material.STRUCTURE_VOID;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.Material;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Sets;

public class MaterialUtils 
{
	private static final Set<Material> OBTAINABLES;

	static 
	{
		Set<Material> unobtainables = Sets.newHashSet(BARRIER, JIGSAW, STRUCTURE_BLOCK, STRUCTURE_VOID, SPAWNER, DEBUG_STICK, KNOWLEDGE_BOOK, BEDROCK, COMMAND_BLOCK);
		unobtainables.addAll(getSupportedMaterials(LIGHT));
		
		OBTAINABLES = Arrays.stream(XMaterial.VALUES)
				.filter(XMaterial::isSupported)
				.filter(xmaterial -> xmaterial.parseMaterial().isItem())
				.filter(negate(MaterialUtils::isAir))
				.map(XMaterial::parseMaterial)
				.filter(negate(unobtainables::contains))
				.collect(toSet());
	}
	
	public static boolean isObtainable(Material material) 
	{
		return OBTAINABLES.contains(material);
	}
	
	private static Set<Material> getSupportedMaterials(XMaterial... xmaterials) 
	{
		return Arrays.stream(xmaterials)
				.filter(XMaterial::isSupported)
				.map(XMaterial::parseMaterial)
				.collect(toSet());
	}
	
	private static boolean isAir(XMaterial xmaterial) 
	{
		switch(xmaterial) 
		{
		case AIR:
		case CAVE_AIR:
		case VOID_AIR:
			return true;
		default:
			return false;
		}
	}
}