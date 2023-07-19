/**
 * A Package for Client Entities<br><br>
 *
 * Client Entities are used for providing the {@link me.gravityio.creativeplus.screen.EditEntityScreen EditEntityScreen} with an NBT List of
 * all the DATA of an entity that allows for getting the server entity's current NBT and
 * setting the NBT that will be sent back to the server in order to modify the server entity<br><br>
 *
 * For Example; An {@link net.minecraft.entity.Entity Entity} will have some consistent
 * NBT Tags of Invulnerable, NoGravity and those will be listed in
 * {@link me.gravityio.creativeplus.entity.client.ClientEntity#getNbt  ClientEntity#getNbt} etc.
 *
 * @see me.gravityio.creativeplus.screen.EditEntityScreen EditEntityScreen
 */
package me.gravityio.creativeplus.entity.client;