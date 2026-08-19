package fr.madu59.ptp.physics;

import java.util.ArrayList;
import java.util.List;

import fr.madu59.ptp.PtpClient;
import fr.madu59.ptp.api.projectiles.ProjectileDataAPI;
import fr.madu59.ptp.util.TrajectoryUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ProjectileData {

    public final double gravity;
    public final double drag;
    public final Vec3 initialVelocity;
    public final Vec3 offset;
    public final Vec3 position;
    public final boolean hasWaterCollision;
    public final double waterDrag;
    public final double underwaterGravity;
    public final PhysicsOrder order;
    public final boolean bypassAntiCheat;

    private final static PhysicsOrder ORDER_PDG = new PhysicsOrder(new PhysicsStep[]{PhysicsStep.POSITION, PhysicsStep.DRAG, PhysicsStep.GRAVITY});
    private final static PhysicsOrder ORDER_GPD = new PhysicsOrder(new PhysicsStep[]{PhysicsStep.GRAVITY, PhysicsStep.POSITION, PhysicsStep.DRAG});
    private final static PhysicsOrder ORDER_GDP = new PhysicsOrder(new PhysicsStep[]{PhysicsStep.GRAVITY, PhysicsStep.DRAG, PhysicsStep.POSITION});

    public ProjectileData(double gravity, double drag, Vec3 initialVelocity, Vec3 offset, Vec3 position, boolean hasWaterCollision, double waterDrag, PhysicsOrder order, boolean bypassAntiCheat) {
        this(gravity, drag, initialVelocity, offset, position, hasWaterCollision, waterDrag, gravity, order, bypassAntiCheat);
    }

    public ProjectileData(double gravity, double drag, Vec3 initialVelocity, Vec3 offset, Vec3 position, boolean hasWaterCollision, double waterDrag, double underwaterGravity, PhysicsOrder order, boolean bypassAntiCheat) {
        this.gravity = gravity;
        this.drag = drag;
        this.initialVelocity = initialVelocity;
        this.offset = offset;
        this.position = position;
        this.hasWaterCollision = hasWaterCollision;
        this.waterDrag = waterDrag;
        this.underwaterGravity = underwaterGravity;
        this.order = order;
        this.bypassAntiCheat = bypassAntiCheat;
    } 

    static public List<ProjectileData> getItemsData(ItemStack itemStack, Player player, boolean isMainHand) {

        List<ProjectileData> projectileDataList = new ArrayList<>();
        if (itemStack == null) return projectileDataList;

        Item item = itemStack.getItem();
        if (item == null) return projectileDataList;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId == null) return projectileDataList;

        if(!ProjectileDataAPI.isBlacklisted(itemId)) {
            var dataProvider = ProjectileDataAPI.getProjectileDataProvider(itemId);
            if(dataProvider != null && ProjectileDataAPI.isEnabled(itemId)){
                dataProvider.accept(itemStack, player, projectileDataList);
            }
        }

        return projectileDataList;
    }

    static public ProjectileData getDropTrajectory(Player player){
        double gravity = 0.04;
        double drag = 0.98;
        double waterDrag = 0.98 * 0.9900000095367432;
        //double underwaterGravity = - (double)(5.0E-4F) / 0.9900000095367432;
        Vec3 offset = new Vec3(0.2, -0.06, 0.2);

        float tickProgress = PtpClient.getTickProgress();
        Vec3 pos = TrajectoryUtils.getAimPos(player, tickProgress).add(new Vec3(0, -0.2,0));

        float xRot = TrajectoryUtils.getViewXRot(player, tickProgress);
        float yRot = TrajectoryUtils.getViewYRot(player, tickProgress);

        float g = Mth.sin((double)(xRot * 0.017453292F));
        float h = Mth.cos((double)(xRot * 0.017453292F));
        float i = Mth.sin((double)(yRot * 0.017453292F));
        float j = Mth.cos((double)(yRot * 0.017453292F));
        float k = 0.5F * 6.2831855F;
        float l = 0.02F * 0.5F;
        Vec3 vel = new Vec3((double)(-i * h * 0.3F) + Math.cos((double)k) * (double)l, (double)(-g * 0.3F + 0.1F), (double)(j * h * 0.3F) + Math.sin((double)k) * (double)l);

        return new ProjectileData(gravity, drag, vel, offset, pos, true, waterDrag, gravity, ORDER_GPD, true);
    }
}