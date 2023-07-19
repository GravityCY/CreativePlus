package me.gravityio.creativeplus.screen;

import me.gravityio.creativeplus.entity.client.ClientEntity;

// TODO: Implement

public class CreateEntityScreen extends EditEntityScreen{
    public CreateEntityScreen(ClientEntity clientEntity) {
        super(clientEntity);
    }

//    public static CreateEntityScreen create(Entity readEntity){
//        Entity writeEntity = readEntity.getType().create(readEntity.world);
//
//        if (writeEntity == null) {
////            client.player.sendMessage(Text.translatable("message.creativeplus.spawn_clone_error"));
//        }
//
//        writeEntity.copyFrom(readEntity);
//        writeEntity.setYaw(0);
//        writeEntity.setBodyYaw(0);
//        writeEntity.setHeadYaw(0);
//        writeEntity.setPitch(0);
//
//        ClientEntity clientEntity = ClientEntity.create(readEntity, writeEntity, new NbtCompound());
//        var editScreen = new EditEntityScreen(clientEntity);
//        editScreen.onFinish(() -> onFinish(editScreen, clientEntity));
//        ClientServerCommunication.getEntityNbt(readEntity.getUuid(), false, v -> onRespondNBT(client, editScreen, clientEntity, v));
//        client.setScreen(editScreen);
//    }
//
//    private static void onRespondNBT(MinecraftClient client, EditEntityScreen editScreen, ClientEntity clientEntity, NbtCompound nbt) {
//        if (nbt == null) {
//            client.player.sendMessage(Text.translatable("message.creativeplus.screen.edit.failed_to_get_nbt"), true);
//            editScreen.close();
//            return;
//        }
//
//        clientEntity.setRealNbt(nbt);
//        editScreen.updateNBT();
//    }
}
