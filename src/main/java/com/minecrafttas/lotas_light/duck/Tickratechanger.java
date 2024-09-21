package com.minecrafttas.lotas_light.duck;

public interface Tickratechanger {

	public float getTickrateSaved();

	public void toggleTickrate0();

	public void advanceTick();

	public boolean isAdvanceTick();

	public long getAdjustedMilliseconds();
}
