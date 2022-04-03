package dte.employme.utils;

import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionUtils 
{
	public static Set<String> findPermissions(Permissible permissible, Predicate<String> tester) 
	{
		return permissible.getEffectivePermissions().stream()
				.map(PermissionAttachmentInfo::getPermission)
				.filter(tester)
				.collect(toSet());
	}
	
	public static Optional<String> findPermission(Permissible permissible, Predicate<String> tester) 
	{
		Set<String> permissions = findPermissions(permissible, tester);
		
		return permissions.isEmpty() ? Optional.empty() : Optional.ofNullable(permissions.iterator().next());
	}
}
