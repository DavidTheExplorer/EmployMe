package dte.employme.messages.translation;

import dte.employme.messages.MessageKey;

public interface TranslationService
{
	String translate(MessageKey messageKey, String targetLanguage);
	String translate(MessageKey messageKey);
	
	String getDefaultLanguage();
	void setDefaultLanguage(String language);
}