package com.veltro.blazingbarrels.server.game;

/**
 * The WeaponType enum contains the various weapons in the game, organized and retrieved by an integer ID. Each weapon
 * type has a {@link #damage} and {@link #damageDropoff} value, used to determined how much health to remove from a
 * player that is hit by a shot fired from the weapon at a certain distance. Server-side, this enum's only use is in
 * tandem with {@link Packet40WeaponFire} packets.
 * 
 * @author LinearLogic
 * @since 0.2.11
 */
public enum WeaponType {

	/**
	 * The gauss turret is the player's default weapon. Its damage and fire rate are well balanced, making this an easy
	 * weapon for beginners to pick up while still a reliable choice for more advanced players.<p>
	 * 
	 * The player starts with this weapon and can switch back to it if another is selected.
	 */
	GAUSS_TURRET(0, 20, 0),

	/**
	 * The minigun boasts an incredible rate of fire, but packs the lightest punch of all damaging weapons. As a
	 * result, the minigun does not deal the most damage per second, but unlike some other weapons, the minigun will
	 * perform just fine even if not all of its bullets hit their mark.<p>
	 * 
	 * The player can switch to this weapon.
	 */
	MINIGUN(1, 3, 0),

	/**
	 * If the {@link #GAUSS_TURRET} is a pistol and the {@link #MINIGUN} is a machine gun (or just a minigun...), then
	 * the antimatter beam is a sniper. It has much higher power than the previous two weapons, but does experience
	 * damage dropoff. As a sniper class rifle, the antimatter beam also has a slow reload time, so every shot counts.
	 * Nonetheless, this weapon can prove fearsome in the hands of an experienced player - one shot from a gauss turret
	 * followed by an antimatter blast at close range will finish off an opponent.<p>
	 * 
	 * The player can switch to this weapon.
	 */
	ANTIMATTER_BEAM (2, 80, 1.5F),

	/**
	 * The rift jet operates by creating a rift in the spacetime continuum. Spacetime stretches as it tries to fill the
	 * rift, causing the player to be "stretched" in a similar manner. The results are not pretty. As the rift grows in
	 * size, so does the extent to which spacetime is stretched, causing this weapon to hit targets harder the further
	 * away they are.<p>
	 * 
	 * This weapon can only be obtained via supply drop.
	 */
	RIFT_JET(3, 60, -2.5F),

	/**
	 * Every game has something like it. The nuke destroys every player on the server except for its user, who somehow
	 * manages to survive a 400mph superheated radioactive shockwave? Despite being at the heart of the blast?
	 * Go figure. In any event, this weapon is not to be trifled with.<p>
	 * 
	 * This weapon can only be obtained via supply drop.
	 */
	NUKE(4, 9999, 0),

	/**
	 * The electromagnetic pulse doesn't deal damage; rather, it blinds everything in its radius, much like a flashbang
	 * for spaceships. The downside to this otherwise overpowered device is that the blast is centered around the user,
	 * so players will have to sneak up to each other before activating the pulse. If they succeed, however, they'll
	 * be rewarded with a few hilarious seconds watching other players race around frantically, shooting everywhere,
	 * followed by the satisfaction of finishing those players off with a well placed antimatter beam.<p>
	 * 
	 * The player gets one EMP per respawn.
	 */
	EMP(5, 0, 0);

	/**
	 * The integer ID unique to each weapon type (used to identify the weapon type of a {@link Packet40WeaponFire})
	 */
	public int ID;

	/**
	 * The base damage dealt by a shot from the weapon (this value takes into account none of the damage modifiers like
	 * {@link #damageDropoff} and armor)
	 */
	public int damage;

	/**
	 * The amount by which the {@link #damage} decreases for every hundred pixels of distance between the source of the
	 * shot and its victim (positive and negative values as well as zero are allowed)
	 */
	public float damageDropoff;
	
	/**
	 * Enum constructor
	 * 
	 * @param ID The weapon type's {@link #ID}
	 * @param damage The weapon type's {@link #damage}
	 * @param damageDropoff The weapon type's {@link #damageDropoff}
	 */
	WeaponType(int ID, int damage, float damageDropoff) {
		this.ID = ID;
		this.damage = damage;
		this.damageDropoff = damageDropoff;
	}

	/**
	 * Retrieves a WeaponType based on its unique integer ID
	 * 
	 * @param ID A positive integer in the range of the WeaponType IDs
	 * @return The WeaponType with the specified ID
	 */
	public WeaponType getTypeFromID(int ID) {
		switch(ID) {
		case 0:
			return GAUSS_TURRET;
		case 1:
			return MINIGUN;
		case 2:
			return ANTIMATTER_BEAM;
		case 3:
			return RIFT_JET;
		case 4:
			return NUKE;
		case 5:
			return EMP;
		default:
			return null;
		}
	}
}
