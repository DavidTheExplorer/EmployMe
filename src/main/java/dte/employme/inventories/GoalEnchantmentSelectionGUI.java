package dte.employme.inventories;

import static dte.employme.utils.java.Predicates.negate;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.WHITE;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.conversations.Conversations;
import dte.employme.job.prompts.EnchantmentLevelPrompt;
import dte.employme.messages.service.MessageService;
import dte.employme.utils.items.ItemBuilder;
import dte.employme.utils.java.EnumUtils;

public class GoalEnchantmentSelectionGUI extends ChestGui
{
	private final MessageService messageService;
	private final GoalCustomizationGUI goalCustomizationGUI;
	
	//temp data
	private boolean showCustomizationGuiOnClose = true;

	public GoalEnchantmentSelectionGUI(MessageService messageService, GoalCustomizationGUI goalCustomizationGUI)
	{
		super(6, "Choose an Enchantment:");

		this.messageService = messageService;
		this.goalCustomizationGUI = goalCustomizationGUI;

		setOnClose(event -> 
		{
			if(this.showCustomizationGuiOnClose)
				goalCustomizationGUI.show(event.getPlayer());
		});
		
		addPane(getEnchantmentsPane(Priority.LOWEST));
		update();
	}

	private OutlinePane getEnchantmentsPane(Priority priority) 
	{
		OutlinePane pane = new OutlinePane(0, 0, 9, 6, priority);

		getRemainingEnchantments().stream()
		.map(this::createEnchantmentItem)
		.forEach(pane::addItem);

		return pane;
	}
	
	private Set<Enchantment> getRemainingEnchantments()
	{
		ItemStack currentItem = this.goalCustomizationGUI.getCurrentItem();

		return Arrays.stream(Enchantment.values())
				.filter(negate(currentItem::containsEnchantment))
				.filter(enchantment -> enchantment.canEnchantItem(currentItem))
				.sorted(comparing(enchantment -> enchantment.getKey().getKey())) //sort the enchantments by their names
				.collect(toSet());
	}

	private GuiItem createEnchantmentItem(Enchantment enchantment) 
	{
		ItemStack item = new ItemBuilder(Material.ENCHANTED_BOOK)
				.named(GREEN + EnchantmentUtils.getDisplayName(enchantment))
				.withLore(WHITE + "Click to add this Enchantment to the Goal.")
				.createCopy();

		return new GuiItem(item, event -> 
		{
			Player player = (Player) event.getWhoClicked();

			this.showCustomizationGuiOnClose = false;
			player.closeInventory();

			Conversations.createConversationFactory()
			.withFirstPrompt(new EnchantmentLevelPrompt(enchantment, this.messageService, this.goalCustomizationGUI))
			.buildConversation(player)
			.begin();
		});
	}
}