package dte.employme.messages.translation;

import dte.employme.messages.MessageKey;

public abstract class AbstractTranslationService implements TranslationService
{
	protected String defaultLanguage;
	
	public AbstractTranslationService(String defaultLanguage) 
	{
		this.defaultLanguage = defaultLanguage;
	}
	
	@Override
	public String translate(MessageKey messageKey) 
	{
		return translate(messageKey, this.defaultLanguage);
	}

	@Override
	public String getDefaultLanguage() 
	{
		return this.defaultLanguage;
	}

	@Override
	public void setDefaultLanguage(String language) 
	{
		this.defaultLanguage = language;
	}
}