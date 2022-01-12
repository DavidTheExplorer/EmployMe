package dte.employme.reloadable;

/**
 * Represents an object that can be reloaded after some time.<p>
 * Used in the <b>/employment reload</b> command.
 */
@FunctionalInterface
public interface Reloadable
{
	void reload();
}