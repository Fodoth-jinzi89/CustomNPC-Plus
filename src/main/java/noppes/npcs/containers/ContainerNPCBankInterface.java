package noppes.npcs.containers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerBankData;

public class ContainerNPCBankInterface extends ContainerNpcInterface
{
    private static final Logger logger = LogManager.getLogger();
	public InventoryNPC currencyMatrix;
    private EntityPlayer player;
    public SlotNpcBankCurrency currency;
    public int slot = 0;
    public int bankid;
    private PlayerBankData data;
    public ContainerNPCBankInterface(EntityPlayer player, int slot, int bankid)
    {
    	super(player);
    	this.bankid = bankid;
    	this.slot = slot;
    	this.player = player;
    	    	
        currencyMatrix = new InventoryNPC("currency", 1, this);
        if(!isAvailable() || canBeUpgraded()){
        	currency = new SlotNpcBankCurrency(this, currencyMatrix, 0, 80, 29);
        	addSlotToContainer(currency);
        }
        
        NpcMiscInventory items = new NpcMiscInventory(54);
        if(!player.worldObj.isRemote){
    		data = PlayerDataController.Instance.getBankData(player,bankid);
        	items = data.getBankOrDefault(bankid).itemSlots.get(slot);
        }
        
        int xOffset = xOffset();
        int id = 0;
        for (int j = 0; j < getRowNumber(); j++)
        {
            for (int i1 = 0; i1 < 9; i1++)
            {
            	addSlotToContainer(new Slot(items, id, 8 + i1 * 18, 17 + xOffset + j * 18));
            	id++;
            }
        }
        
        if(isUpgraded())
        	xOffset += 54;
        for (int k = 0; k < 3; k++) {
            for (int j1 = 0; j1 < 9; j1++) {
                int id1 = j1 + k * 9 + 9;
                addSlotToContainer(new Slot(player.inventory, id1, 8 + j1 * 18, 86 + xOffset + k * 18));
            }
        }

        for (int l = 0; l < 9; l++) {
            int id2 = l;
            addSlotToContainer(new Slot(player.inventory, id2, 8 + l * 18, 144 + xOffset));
        }
    }
    public int getRowNumber() {
		return 0;
	}
	public int xOffset(){
    	return 0;
    }
    @Override
    public void onCraftMatrixChanged(IInventory inv){
    	
    }
    public boolean isAvailable(){
    	return false;
    }
    public boolean isUpgraded(){
    	return false;
    }
    public boolean canBeUpgraded(){
    	return false;
    }

    public synchronized void setCurrency(ItemStack item){
        currency.item = item;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < getRowNumber() * 9)
            {
                if (!this.mergeItemStack(itemstack1, getRowNumber() * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, getRowNumber() * 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }   
    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);
        if (!entityplayer.worldObj.isRemote){
            ItemStack var3 = this.currencyMatrix.getStackInSlot(0);
            currencyMatrix.setInventorySlotContents(0, null);
            if (var3 != null)
            {
            	entityplayer.dropPlayerItemWithRandomChoice(var3, false);
            }
        }
    }
    
    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
    	//logger.info("SlotID="+slotId+";clickedButton="+clickedButton+";mode="+mode);
    	return super.slotClick(slotId, clickedButton, mode, player);
    }
	

}
