package com.minecrafttas.lotas_light.duck;

public interface Tickratechanger {

	public float getTickrateSaved();

	public void toggleTickrate0();

	public void enableTickrate0(boolean enable);

	public void advanceTick();

	public boolean isAdvanceTick();

	public long getAdjustedMilliseconds();

	public void disconnect();
}
