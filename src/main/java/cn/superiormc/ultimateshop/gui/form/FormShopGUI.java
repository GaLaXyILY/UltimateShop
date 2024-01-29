package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.time.LocalDateTime;
import java.util.Objects;

public class FormShopGUI extends FormGUI {

    private ObjectShop shop;

    private ObjectMenu shopMenu = null;

    public FormShopGUI(Player owner, ObjectShop shop) {
        super(owner);
        this.shop = shop;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        PlayerCache tempVal1 = CacheManager.cacheManager.getPlayerCache(owner.getPlayer());
        ServerCache tempVal2 = ServerCache.serverCache;
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.player-not-found",
                    "player",
                    owner.getPlayer().getName());
            return;
        }
        if (shop.getShopMenu() == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.shop-does-not-have-menu",
                    "shop",
                    shop.getShopName());
            return;
        }
        shopMenu = ObjectMenu.shopMenus.get(shop);
        if (shopMenu == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.shop-menu-not-found",
                    "shop",
                    shop.getShopName(),
                    "menu",
                    shop.getShopMenu());
            return;
        }
        for (ObjectItem tempVal5 : shop.getProductList()) {
            ObjectUseTimesCache tempVal3 = tempVal1.getUseTimesCache().get(tempVal5);
            if (tempVal3 != null && tempVal3.getBuyRefreshTime() != null && tempVal3.getBuyRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §bReset player buy data by GUI open check!");
                }
                tempVal1.getUseTimesCache().get(tempVal5).setBuyUseTimes(0);
                tempVal1.getUseTimesCache().get(tempVal5).setLastBuyTime(null);
            }
            if (tempVal3 != null && tempVal3.getSellRefreshTime() != null && tempVal3.getSellRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §bReset player sell data by GUI open check!");
                }
                tempVal1.getUseTimesCache().get(tempVal5).setSellUseTimes(0);
                tempVal1.getUseTimesCache().get(tempVal5).setLastSellTime(null);
            }
            ObjectUseTimesCache tempVal4 = tempVal2.getUseTimesCache().get(tempVal5);
            if (tempVal4 != null && tempVal4.getBuyRefreshTime() != null && tempVal4.getBuyRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §bReset server buy data by GUI open check!");
                }
                tempVal2.getUseTimesCache().get(tempVal5).setBuyUseTimes(0);
                tempVal2.getUseTimesCache().get(tempVal5).setLastBuyTime(null);
            }
            if (tempVal4 != null && tempVal4.getSellRefreshTime() != null && tempVal4.getSellRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §bReset server sell data by GUI open check!");
                }
                tempVal2.getUseTimesCache().get(tempVal5).setSellUseTimes(0);
                tempVal2.getUseTimesCache().get(tempVal5).setLastSellTime(null);
            }
        }
        menuButtons = shopMenu.getMenu();
        if (ConfigManager.configManager.getBoolean("debug")) {
            for (Integer i : menuButtons.keySet()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fMenu Buttons: " + menuButtons.get(i));
            }
        }
        SimpleForm.Builder tempVal5 = SimpleForm.builder();
        if (Objects.isNull(form)) {
            for (int slot : menuButtons.keySet()) {
                AbstractButton button = menuButtons.get(slot);
                String tempVal3 = TextUtil.parse(
                        CommonUtil.getItemName(button.getDisplayItem(
                                owner.getPlayer(), 1)));
                if (tempVal3.trim().length() == 0) {
                    continue;
                }
                ButtonComponent tempVal6 = null;
                String icon = button.getButtonConfig().getString("bedrock-icon");
                if (icon != null && icon.split(";;").length == 2) {
                    String type = icon.split(";;")[0].toLowerCase();
                    if (type.equals("url")) {
                        tempVal6 = ButtonComponent.of(tempVal3, FormImage.Type.URL, icon.split(";;")[1]);
                    } else if (type.equals("path")) {
                        tempVal6 = ButtonComponent.of(tempVal3, FormImage.Type.PATH, icon.split(";;")[1]);
                    }
                } else {
                    tempVal6 = ButtonComponent.of(tempVal3);
                }
                if (tempVal6 != null) {
                    tempVal5.button(tempVal6);
                }
                menuItems.put(tempVal6, slot);
            }
        }

        tempVal5.title(TextUtil.parse(shopMenu.getString("title", shop.getShopDisplayName())
                .replace("{shop-name}", shop.getShopDisplayName())));
        tempVal5.validResultHandler(response -> {
            menuButtons.get(menuItems.get(response.clickedButton())).clickEvent(ClickType.LEFT, owner.getPlayer());
        });
        form = tempVal5.build();
    }
}
