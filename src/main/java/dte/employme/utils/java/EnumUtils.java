package dte.employme.utils.java;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class EnumUtils
{
	private static final String WORD_PATTERN = Pattern.quote("_");

	/**
	 * Returns a proper display name from any enum's <i>name()</i> method, used when you don't have/need the enum instance.
	 * 
	 * @param enumName The original name of the enum.
	 * @return A proper display name for the specified enum name.
	 */
	public static String fixEnumName(String enumName) 
	{
		String[] words = enumName.toLowerCase().split(WORD_PATTERN);

		return Arrays.stream(words)
				.map(EnumUtils::uppercaseFirstLetter)
				.collect(joining(" "));
	}

	/**
	 * Returns a proper display name from an enum's {@code name()} method.
	 * 
	 * @param enumInstance the enum instance whose name will be fixed.
	 * @return A proper display name from the specified enum instance.
	 */
	public static String fixEnumName(Enum<?> enumInstance) 
	{
		return fixEnumName(enumInstance.name());
	}
	
	public static <E extends Enum<E>> Optional<E> getByName(String name, Class<E> enumClass)
	{
		return Arrays.stream(enumClass.getEnumConstants())
				.filter(instance -> instance.name().equalsIgnoreCase(name))
				.findFirst();
	}
	
	private static String uppercaseFirstLetter(String word) 
	{
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}
}