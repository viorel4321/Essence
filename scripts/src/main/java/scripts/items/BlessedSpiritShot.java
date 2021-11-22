package items;

import Essence.gameserver.Config;
import Essence.gameserver.scripts.ScriptFile;
import Essence.gameserver.handler.IItemHandler;
import Essence.gameserver.handler.ItemHandler;
import Essence.gameserver.model.Playable;
import Essence.gameserver.model.Player;
import Essence.gameserver.model.items.L2ItemInstance;
import Essence.gameserver.serverpackets.ExAutoSoulShot;
import Essence.gameserver.serverpackets.MagicSkillUse;
import Essence.gameserver.data.xml.holder.SystemMessageHolder;
import Essence.gameserver.tables.ItemTable;
import Essence.gameserver.templates.item.L2Item;
import Essence.gameserver.templates.item.L2Weapon;

public class BlessedSpiritShot implements IItemHandler, ScriptFile
{
    private static final int[] _itemIds =
    {
        3947, 3948, 3949, 3950, 3951, 3952
    };
    private static final short[] _skillIds =
    {
        2061, 2160, 2161, 2162, 2163, 2164, 2164, 21633
    };

    public void useItem(Playable playable, L2ItemInstance item)
    {
        if (playable == null || !playable.isPlayer())
        {
            return;
        }
        Player player = (Player) playable;
        if (player.isInOlympiadMode())
        {
            player.sendPacket(SystemMessageHolder.getInstance().get(1508));
            return;
        }
        L2ItemInstance weaponInst = player.getActiveWeaponInstance();
        L2Weapon weaponItem = player.getActiveWeaponItem();
        int SoulshotId = item.getItemId();
        boolean isAutoSoulShot = false;
        L2Item itemTemplate = ItemTable.getInstance().getTemplate(item.getItemId());
        if (player.getAutoSoulShot().contains(SoulshotId))
        {
            isAutoSoulShot = true;
        }
        if (weaponInst == null)
        {
            if (isAutoSoulShot)
            {
                player.removeAutoSoulShot(SoulshotId);
                player.sendPacket(new ExAutoSoulShot(SoulshotId, false));
                player.sendPacket(SystemMessageHolder.getInstance().get(1434).addString(itemTemplate.getName()));
                return;
            }
            player.sendPacket(SystemMessageHolder.getInstance().get(532));
            return;
        }
        if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
        {
            return;
        }
        int spiritshotId = item.getItemId();
        int grade = weaponItem.getCrystalType().externalOrdinal;
        int blessedsoulSpiritConsumption = weaponItem.getSpiritShotCount();
        int count = item.getIntegerLimitedCount();
        if (blessedsoulSpiritConsumption == 0)
        {
            if (isAutoSoulShot)
            {
                player.removeAutoSoulShot(SoulshotId);
                player.sendPacket(new ExAutoSoulShot(SoulshotId, false));
                player.sendPacket(SystemMessageHolder.getInstance().get(1434).addString(itemTemplate.getName()));
                return;
            }
            player.sendPacket(SystemMessageHolder.getInstance().get(532));
            return;
        }
        if (grade == 0 && spiritshotId != 3947 || grade == 1 && spiritshotId != 3948 || grade == 2 && spiritshotId != 3949 || grade == 3 && spiritshotId != 3950 || grade == 4 && spiritshotId != 3951 || grade == 5 && spiritshotId != 3952)
        {
            if (isAutoSoulShot)
            {
                return;
            }
            player.sendPacket(SystemMessageHolder.getInstance().get(530));
            return;
        }
        if (count < blessedsoulSpiritConsumption)
        {
            if (isAutoSoulShot)
            {
                player.removeAutoSoulShot(SoulshotId);
                player.sendPacket(new ExAutoSoulShot(SoulshotId, false));
                player.sendPacket(SystemMessageHolder.getInstance().get(1434).addString(itemTemplate.getName()));
                return;
            }
            player.sendPacket(SystemMessageHolder.getInstance().get(531));
            return;
        }
        weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
        if (Config.UNLIMITED_SS == false)
        {
            player.getInventory().destroyItem(item, blessedsoulSpiritConsumption, false);
        }
        player.sendPacket(SystemMessageHolder.getInstance().get(533));
        player.broadcastPacket(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0));
    }

    public final int[] getItemIds()
    {
        return _itemIds;
    }

    public void onLoad()
    {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    public void onReload()
    {
    }

    public void onShutdown()
    {
    }
}
