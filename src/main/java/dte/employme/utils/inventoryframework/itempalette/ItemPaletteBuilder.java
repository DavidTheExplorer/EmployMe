package dte.employme.utils.inventoryframework.itempalette;

import static com.github.stefvanschie.inventoryframework.pane.Orientable.Orientation.HORIZONTAL;
import static dte.employme.utils.InventoryUtils.createWall;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.backButtonListener;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.createRectangle;
import static dte.employme.utils.inventoryframework.InventoryFrameworkUtils.nextButtonListener;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority;

import dte.employme.utils.MaterialUtils;
import dte.employme.utils.inventoryframework.GuiItemBuilder;

public class ItemPaletteBuilder
{
	private String title;
	private Deque<GuiItem> items = new LinkedList<>();
	private int itemsPerPage = (9 * 5); //5 lines by default
	private ItemStack backButton, nextButton;
	
	//search feature data
	private ItemStack searchButton;
	private ConversationFactory searchConversationFactory;
	private Consumer<InventoryClickEvent> searchItemListener = (event) -> {};

	private static final List<GuiItem> OBTAINABLE_ITEMS = Arrays.stream(Material.values())
			.filter(MaterialUtils::isObtainable)
			.map(material -> new GuiItem(new ItemStack(material)))
			.collect(toList());
	
	private ItemPaletteBuilder(Collection<GuiItem> items)
	{
		this.items.addAll(items);
	}
	
	public static ItemPaletteBuilder empty() 
	{
		return new ItemPaletteBuilder(Collections.emptyList());
	}
	
	public static ItemPaletteBuilder withAllItems() 
	{
		return new ItemPaletteBuilder(OBTAINABLE_ITEMS);
	}
	
	public static ItemPaletteBuilder with(Collection<GuiItem> items) 
	{
		return new ItemPaletteBuilder(items);
	}
	
	

	/**
	 * Sets the title of this palette to the provided {@code title}.
	 * 
	 * @return This object for chaining purpose.
	 */
	public ItemPaletteBuilder named(String title) 
	{
		this.title = title;
		return this;
	}

	/**
	 * Sets a transformer that is applied to all materials - so the displayed items are customizable.
	 * 
	 * @param itemTransformer The transformer that is applied to all materials.
	 * @return This object for chaining purpose.
	 */
	public ItemPaletteBuilder map(Function<Material, GuiItem> itemTransformer)
	{
		this.items = this.items.stream()
				.map(item -> itemTransformer.apply(item.getItem().getType()))
				.collect(toCollection(LinkedList::new));
		
		return this;
	}

	/**
	 * Determines what items are shown within this palette.
	 * 
	 * @param itemFilter The logic that determines what items are shown.
	 * @return This object for chaining purpose.
	 */
	public ItemPaletteBuilder filter(Predicate<Material> itemFilter)
	{
		this.items = this.items.stream()
				.filter(item -> itemFilter.test(item.getItem().getType()))
				.collect(toCollection(LinkedList::new));

		return this;
	}
	
	/**
	 * Sets the amount of items that every page in the palette will contain.
	 * 
	 * @param linesAmount The amount of lines per page, in multiples of 9.
	 * @return This object for chaining purpose.
	 */
	public ItemPaletteBuilder showPerPage(int linesAmount) 
	{
		if(linesAmount % 9 != 0)
			throw new IllegalArgumentException(String.format("The amount of items to show per page has to be a multiple of 9! %d was provided.", linesAmount));
		
		this.itemsPerPage = 9 * linesAmount;
		return this;
	}

	/**
	 * Sets the items for the control buttons(<i>next</i> and <i>back</i>).
	 * 
	 * @param backButton The back button.
	 * @param nextButton The next button.
	 * @return This object for chaining purpose.
	 */
	public ItemPaletteBuilder withControlButtons(ItemStack backButton, ItemStack nextButton) 
	{
		this.backButton = backButton;
		this.nextButton = nextButton;
		return this;
	}

	/**
	 * Adds a search item feature for this palette; The search is by name.
	 * 
	 * @param searchItem The search button.
	 * @param typeConversationFactory The conversation factory that asks the player what item he needs.
	 * @return This object for chaining purpose.
	 */
	public ItemPaletteBuilder withSearchFeature(ItemStack searchButton, ConversationFactory typeConversationFactory) 
	{
		this.searchButton = searchButton;
		this.searchConversationFactory = typeConversationFactory;
		return this;
	}

	/**
	 * Adds an optional listener that is called before a search conversation is started with the player.
	 * 
	 * @param listener The listener.
	 * @return This object for chaining purpose.
	 */
	public ItemPaletteBuilder onSearchItemClicked(Consumer<InventoryClickEvent> listener) 
	{
		this.searchItemListener = listener;
		return this;
	}

	/**
	 * The final method that returns the Item Palette object.
	 * 
	 * @return The Item Palette.
	 */
	public ItemPaletteGUI build()
	{
		ItemPaletteGUI palette = new ItemPaletteGUI(6, this.title);
		
		//add panes
		PaginatedPane itemsPane = createItemsPane();
		palette.init(itemsPane);
		
		palette.addPane(itemsPane);
		palette.addPane(createControlPane(palette, itemsPane));
		palette.addPane(createRectangle(Priority.LOWEST, 1, 5, 7, 1, new GuiItem(new ItemStack(createWall(Material.BLACK_STAINED_GLASS_PANE)))));
		
		//register listeners
		palette.setOnTopClick(event -> event.setCancelled(true));
		
		return palette;
	}

	private PaginatedPane createItemsPane() 
	{
		PaginatedPane pane = new PaginatedPane(0, 0, 9, 5, Priority.LOWEST);

		//create and add the necessary amount of pages
		for(int i = 0, pagesAmount = (this.items.size() / this.itemsPerPage) +1; i < pagesAmount; i++)
			pane.addPane(i, createPage(this.items));

		pane.setPage(0);

		return pane;
	}


	private OutlinePane createControlPane(ChestGui palette, PaginatedPane itemsPane)
	{
		OutlinePane pane = new OutlinePane(0, 5, 9, 1, Priority.LOW);
		pane.setOrientation(HORIZONTAL);
		pane.setGap(this.searchButton != null ? 3 : 7);

		//add the back, search(if was set), and next buttons
		pane.addItem(createBackButton(palette, itemsPane));

		if(this.searchButton != null) 
			pane.addItem(createSearchButton());

		pane.addItem(createNextButton(palette, itemsPane));

		return pane;
	}

	private GuiItem createBackButton(ChestGui palette, PaginatedPane itemsPane) 
	{
		return new GuiItemBuilder()
				.forItem(this.backButton)
				.whenClicked(backButtonListener(palette, itemsPane))
				.build();
	}

	private GuiItem createNextButton(ChestGui palette, PaginatedPane itemsPane) 
	{
		return new GuiItemBuilder()
				.forItem(this.nextButton)
				.whenClicked(nextButtonListener(palette, itemsPane))
				.build();
	}

	private GuiItem createSearchButton() 
	{
		return new GuiItemBuilder()
				.forItem(this.searchButton)
				.whenClicked(event ->
				{
					this.searchItemListener.accept(event);

					Player player = (Player) event.getWhoClicked();
					player.closeInventory();

					this.searchConversationFactory.buildConversation(player).begin();
				})
				.build();
	}

	private Pane createPage(Deque<GuiItem> items)
	{
		OutlinePane page = new OutlinePane(0, 0, 9, 6, Priority.LOWEST);
		page.setOrientation(HORIZONTAL);

		for(int i = 1; i <= this.itemsPerPage; i++) 
		{
			if(!items.isEmpty()) //a little trick to avoid NoSuchElementException at the last page
				page.addItem(items.removeFirst());
		}

		return page;
	}
}