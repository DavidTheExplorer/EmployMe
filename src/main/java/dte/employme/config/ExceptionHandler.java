package dte.employme.config;

import java.io.IOException;

@FunctionalInterface
public interface ExceptionHandler
{
	void handle(IOException exception, ConfigFile config);
}