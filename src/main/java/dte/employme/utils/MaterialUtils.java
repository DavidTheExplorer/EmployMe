package dte.employme.utils;

import static com.cryptomorin.xseries.XMaterial.BARRIER;
import static com.cryptomorin.xseries.XMaterial.BEDROCK;
import static com.cryptomorin.xseries.XMaterial.COMMAND_BLOCK;
import static com.cryptomorin.xseries.XMaterial.DEBUG_STICK;
import static com.cryptomorin.xseries.XMaterial.JIGSAW;
import static com.cryptomorin.xseries.XMaterial.KNOWLEDGE_BOOK;
import static com.cryptomorin.xseries.XMaterial.LIGHT;
import static com.cryptomorin.xseries.XMaterial.SPAWNER;
import static com.cryptomorin.xseries.XMaterial.STRUCTURE_BLOCK;
import static com.cryptomorin.xseries.XMaterial.STRUCTURE_VOID;
import static dte.employme.utils.java.Predicates.negate;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.event.Listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;

public class MaterialUtils implements Listener
{
	private static final Set<Material> OBTAINABLE_ITEMS;

	static 
	{
		//list of unobtainable items that exist on the server(for example LIGHT only exists from 1.17+)
		Set<Material> unobtainables = getUnobtainableItems();
		
		OBTAINABLE_ITEMS = Arrays.stream(XMaterial.VALUES)
				.filter(XMaterial::isSupported)
				.filter(xmaterial -> xmaterial.parseMaterial().isItem())
				.filter(negate(XTag.AIR::isTagged))
				.map(XMaterial::parseMaterial)
				.filter(negate(unobtainables::contains))
				.collect(toSet());
	}
	
	public static boolean isObtainable(Material material) 
	{
		return OBTAINABLE_ITEMS.contains(material);
	}

	private static Set<Material> getUnobtainableItems()
	{
		return Stream.of(BARRIER, JIGSAW, STRUCTURE_BLOCK, STRUCTURE_VOID, SPAWNER, DEBUG_STICK, KNOWLEDGE_BOOK, BEDROCK, COMMAND_BLOCK, LIGHT)
				.filter(XMaterial::isSupported)
				.map(XMaterial::parseMaterial)
				.collect(toSet());
	}
}